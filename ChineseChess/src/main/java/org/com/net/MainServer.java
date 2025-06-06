package org.com.net;

import org.com.entity.User;
import org.com.protocal.ChessMessage;
import org.com.tools.ChessRoomTool;
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
import java.util.concurrent.ConcurrentHashMap;

public class MainServer extends Server {
    public class Client {
        private User user;
        String ip;
        int port;
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

    ConcurrentHashMap<String, User> userTable = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Client> onlineTable = new ConcurrentHashMap<>();

    MainServer(int port) throws IOException {
        serverName = "主服务器";
        logger = LoggerFactory.getLogger(MainServer.class);

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

    private void loginHandle(Socket affair, ChessMessage message){
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
            onlineTable.put(account, new Client(user, affair.getInetAddress().getHostAddress(), affairPort));
            logger.info("上线用户、地址及端口" + account + " " + affair.getInetAddress().getHostAddress() + " " + affairPort);
            response.setType(ChessMessage.Type.SUCCESS);
        }

        try {
            ChessRoomTool.sendMessage(affair, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void offlineHandle(){

    }
    private void openRoomHandle(){

    }
    private void playerListHandle(){
        logger.info("全局更新在线用户");
        Vector<String> items = new Vector<>();
        onlineTable.forEach((u, _) -> items.add(u));
        ChessMessage response = new ChessMessage(items, ChessMessage.Type.ACQUIRE_PLAYER, null, null);
        onlineTable.forEach((account, client) -> {
            Sender sender = new Sender(client.getIp(), client.getPort(), 1000);
            try {
                logger.info("发送给 " + account);
                ChessMessage useless = sender.send(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void handle(Socket affair, ChessMessage message) {

        switch (message.getType()){
            case LOGIN:
                loginHandle(affair, message);
                break;
            case OFFLINE:
                offlineHandle();
                break;
            case ACQUIRE_PLAYER:
                playerListHandle();
                break;
            case FIGHT:
                openRoomHandle();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        int targetPort = 65140;
        new MainServer(targetPort);
    }
}
