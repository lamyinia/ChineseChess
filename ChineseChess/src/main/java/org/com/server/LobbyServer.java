package org.com.server;

import org.com.net.PersistentConnectionToClient;
import org.com.protocal.ChessMessage;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.com.protocal.ChessMessage.Type.ACQUIRE_LOBBY_LIST;
import static org.com.protocal.ChessMessage.Type.HEARTBEAT;
import static org.com.tools.GameRoomTool.DEFAULT_FIND_PORT;

/**
 * 突然发现写的有点逆天，套了 3 个 Runnable
 * @author lanye
 * @date 2025/06/10
 */
public class LobbyServer extends Server implements Runnable {
    public int lobbyPort;
    Set <String> lobbySet;
    ConcurrentHashMap <String, LobbyConnection> lobbyMap;

    public LobbyServer(){
        logger = LoggerFactory.getLogger(LobbyServer.class);
        serverName = "大厅处理服务器";
        lobbyPort = findFreeServerSocket(DEFAULT_FIND_PORT);
        lobbySet = Collections.synchronizedSet(new HashSet<>());
        lobbyMap = new ConcurrentHashMap<>();
    }

    private void addConnection(Socket affair, ChessMessage message){
        String sender = message.getSender();

        Runnable runner = new LobbyConnection(affair);
        new Thread(runner).start();

        lobbyMap.put(sender, (LobbyConnection) runner);
        lobbySet.add(sender);
    }
    private void removeConnection(String account){
        lobbyMap.remove(account);
        lobbySet.remove(account);
    }

    @Override
    protected void handle(Socket affair, ChessMessage message) {
        logger.info("大厅服务器接受到类型是{}的请求", message.getType().toString());
        switch (message.getType()){
            case ACQUIRE_LOBBY:
                addConnection(affair, message);
            default:
                break;
        }
    }
    @Override
    public void run(){
        listen();
    }


    public class LobbyConnection extends PersistentConnectionToClient implements Runnable {
        public LobbyConnection(){}
        public LobbyConnection(Socket socket) {
            super(socket);
            connectionlogger = LoggerFactory.getLogger(LobbyConnection.class);
        }
        @Override
        protected void handle(ChessMessage message) {
            if (message.getType() != HEARTBEAT && message.getType() != ACQUIRE_LOBBY_LIST){
                connectionlogger.info("收到类型是 {} 的请求", message.getType());
            }

            switch (message.getType()){
                case HEARTBEAT:
                    handleHeartbeat();
                    break;
                case FIGHT:
                    handleNotifyFight(message);
                    break;
                case PRESENT:
                    handlePresent(message);
                    break;
                case ABSENT:
                    handleAbsent(message);
                    break;
                case LOGOUT:
                    handleLogout(message);
                    break;
                case ACQUIRE_LOBBY_LIST:
                    handleAcquireLobbyList(message);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void run(){
            connectionlogger.info("开始监听 {}", socket.getInetAddress().getHostAddress());
            listen();
        }
        private void handleHeartbeat(){
            // 心跳发送
        }
        private void handleAcquireLobbyList(ChessMessage message){
            String sender = message.getSender();
//            for (String s : lobbySet) {
//                System.out.print(s + " ");
//            }
            lobbyMap.get(sender).send(new ChessMessage(lobbySet, ChessMessage.Type.ACQUIRE_LOBBY_LIST_SUCCESS, null, null));
        }
        private void handleLogout(ChessMessage message){
            String sender = message.getSender();
            removeConnection(sender);
        }
        private void handlePresent(ChessMessage message){
            String sender = message.getSender();
            lobbySet.add(sender);
        }
        private void handleAbsent(ChessMessage message) {
            String sender = message.getSender();
            lobbySet.remove(sender);
        }
        private void handleNotifyFight(ChessMessage message){
            String receiver = message.getReceiver();
            lobbyMap.get(receiver).send(message);
        }
    }
}
