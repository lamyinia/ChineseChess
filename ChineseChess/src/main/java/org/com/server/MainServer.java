package org.com.server;

import org.com.entity.User;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.com.tools.SocketTool;
import org.com.tools.SQLTool;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainServer extends Server {

    private static class GameThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        GameThreadFactory() {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = "game-server-pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
    private class GameRejectedPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof GameServer){
                GameServer game = (GameServer) r;
                logger.error("游戏服务器线程池已满，拒绝新游戏");
            }
        }
    }

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_POOL_SIZE = 50;

    private ConcurrentHashMap <String, User> userTable = new ConcurrentHashMap<>();
    private ConcurrentHashMap <String, Boolean> onlineTable = new ConcurrentHashMap<>();

    LobbyServer lobbyServer;
    private ConcurrentHashMap <String, GameServer> activeGames = new ConcurrentHashMap<>();

    private ExecutorService gameThreadPool;

    MainServer(int port) throws IOException {
        serverName = "主服务器";
        logger = LoggerFactory.getLogger(MainServer.class);

        gameThreadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,  // 核心线程数
                MAX_POOL_SIZE,   // 最大线程数
                60L, TimeUnit.SECONDS, // 空闲线程存活时间
                new LinkedBlockingQueue<>(100), // 工作队列
                new GameThreadFactory(), // 自定义线程工厂
                new GameRejectedPolicy() // 自定义拒绝策略
        );
        lobbyServer = new LobbyServer();
        new Thread(lobbyServer).start();

        logger.info("主服务器的核心线程数量是{}，最大线程数量是{}", CORE_POOL_SIZE, MAX_POOL_SIZE);

        initPort(port);
        initData();
        listen();
    }

    private void initPort(int port) throws IOException {
        socket = new ServerSocket(port);
    }
    private void initData(){
        try(Connection conn = SQLTool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet ret = stmt.executeQuery("select * from user")){

            while (ret.next()){
                String account = ret.getString("account");
                String password = ret.getString("password");
                userTable.put(account, new User(account, password));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handle(Socket affair, ChessMessage message) {

        switch (message.getType()){
            case LOGIN:
                loginHandle(affair, message);
                break;
            case LOGOUT:
                logoutHandle(message);
                break;
            case ACQUIRE_GAME_ROOM:
                openGameRoomHandle(affair, message);
                break;
            default:
                break;
        }
    }
    private void loginHandle(Socket affair, ChessMessage message){
        logger.info("客户端登录请求处理");

        ChessMessage response = new ChessMessage();
        User user = (User) message.getMessage();
        String account = user.getAccount();

        if (userTable.get(account) == null || !user.getPassword().equals(userTable.get(account).getPassword())){
            response.setMessage("账号密码输入错误");
            response.setType(ChessMessage.Type.FAILURE);
        } else if (onlineTable.get(account) != null) {
            response.setMessage("改账号已经登录了");
            response.setType(ChessMessage.Type.FAILURE);
        } else {
            onlineTable.put(account, true);
            response.setMessage(lobbyServer.lobbyPort);
            response.setType(ChessMessage.Type.SUCCESS);
            logger.info("上线用户:{}，ip 地址是 {}", account, affair.getInetAddress().getHostAddress());
        }

        try {
            SocketTool.sendMessage(affair, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            SocketTool.closeSocket(affair);
        }
    }
    private void logoutHandle(ChessMessage message){
        String sender = message.getSender();
        onlineTable.remove(sender);
    }

    private void openGameRoomHandle(Socket affair, ChessMessage message){
        logger.info("处理客户端房间请求");

        boolean separation = System.currentTimeMillis()%2 == 1;
        GameServer gameServer = new GameServer();

        message.setMessage(new Object[]{gameServer.getGamePort(), separation});
        message.setType(ChessMessage.Type.ACQUIRE_GAME_ROOM_SUCCESS);

        try {
            SocketTool.sendMessage(affair, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            SocketTool.closeSocket(affair);
        }

        logger.info("游戏服务器已经创建，端口在: {}", gameServer.getGamePort());
        gameThreadPool.submit(gameServer);
    }

    public static void main(String[] args) throws IOException {
        new MainServer(GameRoomTool.MAIN_SERVER_PORT);
    }
}
