package org.com.net;

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
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainServer extends Server {
    public class Client {
        private User user;
        private String ip;
        private int port;
        public Client(User user, String ip, int port) {
            this.user = user;
            this.ip = ip;
            this.port = port;
        }
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
        public String getIp() {
            return ip;
        }
        public void setIp(String ip) {
            this.ip = ip;
        }
        public int getPort() {
            return port;
        }
        public void setPort(int port) {
            this.port = port;
        }
    }
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

    private ConcurrentHashMap<String, User> userTable = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Client> onlineTable = new ConcurrentHashMap<>();
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
            case ONLINE:
                onlineHandle(affair, message);
                break;
            case OFFLINE:
                offlineHandle(message);
                break;
            case ACQUIRE_HALL_LIST:
                HallListHandle();
                break;
            case FIGHT:
                openGameRoomHandle(affair, message);
                break;
            default:
                break;
        }
    }
    private void loginHandle(Socket affair, ChessMessage message){
        logger.info("客户端登录请求处理");

        ChessMessage response = new ChessMessage();
        Object[] objects = (Object[]) message.getMessage();
        User user = (User) objects[0];
        int affairPort = (int) objects[1];

        String account = user.getAccount();

        if (userTable.get(account) == null || !user.getPassword().equals(userTable.get(account).getPassword())){
            response.setMessage("账号密码输入错误");
            response.setType(ChessMessage.Type.FAILURE);
        } else if (onlineTable.get(account) != null) {
            response.setMessage("改账号已经登录了");
            response.setType(ChessMessage.Type.FAILURE);
        } else {
            onlineHandle(affair, message);
            logger.info("上线用户、地址及端口" + account + " " + affair.getInetAddress().getHostAddress() + " " + affairPort);
            response.setType(ChessMessage.Type.SUCCESS);
        }

        try {
            SocketTool.sendMessage(affair, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param affair 根据 socket 找到它的 ip
     * @param message 传 Object[User, 服务器的端口]
     */
    private void onlineHandle(Socket affair, ChessMessage message){
        Object[] objects = (Object[]) message.getMessage();
        User user = (User) objects[0];
        int affairPort = (int) objects[1];
        onlineTable.put(user.getAccount(), new Client(user, affair.getInetAddress().getHostAddress(), affairPort));
    }
    private void offlineHandle(ChessMessage message){
        logger.info("下线请求处理");
        String sender = message.getSender();
        onlineTable.remove(sender);

        HallListHandle();
    }

    private void openGameRoomHandle(Socket affair, ChessMessage message){
        logger.info("客户端房间请求处理");

        String player1 = message.getSender();
        String player2 = message.getReceiver();
        Client client1 = onlineTable.get(player1);
        Client client2 = onlineTable.get(player2);

        try {
            boolean separation = System.currentTimeMillis()%2 == 1;  // true 时 player1 是红方
            GameServer gameServer = new GameServer(client1, client2, separation);
            message.setMessage(new Object[]{GameRoomTool.MAIN_SERVER_IP, gameServer.getGamePort(), separation});

            new Sender(client2.getIp(), client2.getPort(), 1000).sendOnly(message);
            SocketTool.sendMessage(affair, new ChessMessage(new Object[]{GameRoomTool.MAIN_SERVER_IP, gameServer.getGamePort(), !separation},
                    ChessMessage.Type.SUCCESS, null, null));

            gameThreadPool.submit(gameServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void HallListHandle(){
        logger.info("全局更新客户端的大厅在线用户");
        Vector<String> items = new Vector<>();
        onlineTable.forEach((u, _) -> items.add(u));
        ChessMessage response = new ChessMessage(items, ChessMessage.Type.ACQUIRE_HALL_LIST, null, null);
        onlineTable.forEach((account, client) -> {
            try {
//                logger.info("更新用户 " + account + " 的大厅列表");
                new Sender(client.getIp(), client.getPort(), 1000).sendOnly(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static void main(String[] args) throws IOException {
        final int targetPort = 65140;
        new MainServer(targetPort);
    }
}
