package org.com.client.controller;

import org.com.client.callback.ConnectionCallBack;
import org.com.client.callback.LobbyCallBack;
import org.com.client.view.LobbyUI;
import org.com.entity.User;
import org.com.net.PersistentConnectionToServer;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyController implements LobbyCallBack {
    private final Logger logger = LoggerFactory.getLogger(LobbyController.class);
    ConnectionCallBack callBack;

    LobbyConnection lobbyConnection;
    public LobbyUI lobbyUI;
    String currentAccount;

    public LobbyController(ConnectionCallBack callBack, Socket socket, String currentAccount){
        this.callBack = callBack;
        lobbyConnection = new LobbyConnection(socket);
        this.currentAccount = currentAccount;
        lobbyUI = new LobbyUI(this, currentAccount);

        new Thread(lobbyConnection).start();
    }

    @Override
    public void refreshLobbyList(){
        lobbyConnection.handleRequestLobbyList();
    }
//    @Override
//    public void fightRequestEvent(){
//
//    }
    @Override
    public void fightEvent(String sender, String receiver){
        // 询问 receiver 是否同意，如果同意，继续下面逻辑，谁发起的挑战，谁发送请求房间的 Socket
        ChessMessage response =
                new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, GameRoomTool.DEFAULT_SOCKET_TIMEOUT)
                        .send(new ChessMessage(null, ChessMessage.Type.ACQUIRE_GAME_ROOM, sender, receiver));
        if (response.getType() == ChessMessage.Type.ACQUIRE_GAME_ROOM_SUCCESS){
            response.setType(ChessMessage.Type.FIGHT);
            lobbyConnection.send(response);
            Object[] things = (Object[]) response.getMessage();
            callBack.acquireGameRoomConnection((boolean) things[1],  receiver, (int) things[0]);
        }
    }
    @Override
    public void logoutEvent(){

    }

    public class LobbyConnection extends PersistentConnectionToServer implements Runnable {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


        public LobbyConnection(){}
        public LobbyConnection(Socket socket){
            super(socket);
            connectionlogger = LoggerFactory.getLogger(LobbyConnection.class);
        }

        @Override
        protected void handle(ChessMessage message) {
            connectionlogger.info("收到类型是{}的响应", message.getType());
            switch (message.getType()){
                case ACQUIRE_LOBBY_LIST_SUCCESS:
                    handleAddLobbyList(message);
                    break;
                case FIGHT:
                    handleFight(message);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void run() {
            startHeartbeat();
            scheduler.scheduleAtFixedRate(() -> {
                handleRequestLobbyList();
            }, 3000, GameRoomTool.DEFAULT_LOBBY_LIST_REQUEST_PERIOD, TimeUnit.MILLISECONDS);
            // 不设置 delay 会有并发问题?
            connectionlogger.info("开始向服务器轮询大厅列表请求");

            listen();

            scheduler.shutdown();
            heartBeatScheduler.shutdown();
        }

        public void handleAddLobbyList(ChessMessage message){
            Set <String> data = (Set<String>) message.getMessage();
            lobbyUI.addLobbyList(new Vector<>(data));
        }
        public void handleRequestLobbyList(){
            send(new ChessMessage(null, ChessMessage.Type.ACQUIRE_LOBBY_LIST, currentAccount, null));
        }
        public void handleFight(ChessMessage message){
            Object[] things = (Object[]) message.getMessage();

            callBack.acquireGameRoomConnection(!((boolean)things[1]),  message.getSender(), (int)things[0]);
        }
    }
}
