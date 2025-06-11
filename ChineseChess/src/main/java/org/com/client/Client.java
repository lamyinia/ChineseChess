package org.com.client;

import org.com.client.callback.ConnectionCallBack;
import org.com.client.controller.GameController;
import org.com.client.controller.LobbyController;
import org.com.client.controller.LoginController;
import org.com.entity.User;
import org.com.log.LogViewer;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.com.tools.SocketTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client implements ConnectionCallBack {
    private LoginController loginController;
    private LobbyController lobbyController;
    private GameController gameController;

    private User currentUser;

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    private void startApp(){
//        new LogViewer();
        loginController = new LoginController(this);
    }

    public static void main(String[] args) {
        new Client().startApp();
    }


    @Override
    public void acquireLobbyConnection(User user, int lobbyPort){
        Socket socket = new Socket();
        currentUser = user;
        try {
            socket.connect(new InetSocketAddress(GameRoomTool.MAIN_SERVER_IP, lobbyPort), GameRoomTool.DEFAULT_SOCKET_TIMEOUT);
        } catch (IOException e) {
            logger.info("连接服务器大厅错误");
            throw new RuntimeException(e);
        }
        try {
            SocketTool.sendMessage(socket, new ChessMessage(null, ChessMessage.Type.ACQUIRE_LOBBY, currentUser.getAccount(), null));
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        lobbyController = new LobbyController(this, socket, user.getAccount());
    }

    /**
     * @param group 阵营
     * @param opponent 对手
     * @param gameRoomPort 游戏服务端口
     */
    @Override
    public void acquireGameRoomConnection(boolean group, String opponent, int gameRoomPort){
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(GameRoomTool.MAIN_SERVER_IP, gameRoomPort), GameRoomTool.DEFAULT_SOCKET_TIMEOUT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SocketTool.sendMessage(socket, new ChessMessage(group, ChessMessage.Type.ACQUIRE_GAME_ROOM, currentUser.getAccount(), opponent));
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        lobbyController.exitLobby();
        gameController = new GameController(this, socket, group, currentUser.getAccount(), opponent);
    }
    @Override
    public void exitGameRoom(){
        gameController.close();
        gameController = null;
        lobbyController.reEnterLobby();
    }

    @Override
    public void exitLobby() {
        new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, GameRoomTool.DEFAULT_SOCKET_TIMEOUT)
                .sendOnly(new ChessMessage(null, ChessMessage.Type.LOGOUT, currentUser.getAccount(), null));
    }
}
