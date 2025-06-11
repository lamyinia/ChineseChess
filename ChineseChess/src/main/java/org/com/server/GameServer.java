package org.com.server;

import org.com.game.state.GameRecord;
import org.com.game.state.GameState;
import org.com.net.PersistentConnectionToClient;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameServer extends Server implements Runnable {
    GameConnection redConnection, blackConnection;

    String redAccount, blackAccount;
    private int gamePort;


    List <GameRecord> gameRecords = Collections.synchronizedList(new ArrayList<>());
    private GameState gameState = new GameState();

    GameServer(){
        logger = LoggerFactory.getLogger(GameServer.class);
        gamePort = findFreeServerSocket(GameRoomTool.DEFAULT_FIND_PORT);
        serverName = "游戏服务器" + String.valueOf(gamePort);
    }

    public int getGamePort() {
        return gamePort;
    }
    @Override
    protected void handle(Socket socket, ChessMessage message) {
        logger.info("%s 收到类型是 %s请求".formatted(serverName, message.getType()));
        switch (message.getType()){
            case ACQUIRE_GAME_ROOM:
                handleEnterGameRoom(socket, message);
                break;
        }
    }
    private void handleEnterGameRoom(Socket socket, ChessMessage message){
        boolean group = (boolean) message.getMessage();
        if (!group) {
            redConnection = new GameConnection(socket);
            new Thread(redConnection).start();
            redAccount = message.getSender();
            logger.info("红方的 {} 进入游戏", redAccount);
        } else {
            blackConnection = new GameConnection(socket);
            new Thread(blackConnection).start();
            blackAccount = message.getSender();
            logger.info("黑方的 {} 进入游戏", blackAccount);
        }
    }
//    private void handleMoveAction(ChessMessage message){
//        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());
//
//        GameRecord record = (GameRecord) message.getMessage();
//        gameRecords.addLast(record);
//        gameState.doAction(record);
//
//        try {
//            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(message);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (record.getEatenChess() instanceof General){
//            notifyGameOver(findOtherPlayer(message.getReceiver()), ChessMessage.Type.WIN);
//            notifyGameOver(findOtherPlayer(message.getSender()), ChessMessage.Type.LOSE);
//        }
//    }
//    private void handleGiveUpEvent(ChessMessage message){
//        MainServer.Client winPlayer = findOtherPlayer(message.getSender());
//        MainServer.Client losePlayer = findOtherPlayer(message.getReceiver());
//        logger.info("{}赢了,{}输了", winPlayer.getUser().getAccount(), losePlayer.getUser().getAccount());
//        notifyGameOver(winPlayer, ChessMessage.Type.WIN);
//        notifyGameOver(losePlayer, ChessMessage.Type.LOSE);
//    }
//    private void handleDrawRequestEvent(ChessMessage message){
//        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());
//        try {
//            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(new ChessMessage(null,
//                    ChessMessage.Type.DRAW_REQUEST, null, null));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void handleDrawActionEvent(){
//        notifyGameOver(redPlayer, ChessMessage.Type.DRAW_ACTION);
//        notifyGameOver(blackPlayer, ChessMessage.Type.DRAW_ACTION);
//    }
//    private void handleRepealRequestEvent(ChessMessage message){
//        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());
//        try {
//            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(new ChessMessage(null,
//                    ChessMessage.Type.REPEAL_REQUEST, null, null));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void handleRepealActionEvent(){
//        if (gameRecords.isEmpty()) return;
//
//        GameRecord record = gameRecords.removeLast();
//        try {
//            new Sender(redPlayer.getIp(), redPlayer.getPort(), 1000).sendOnly(new ChessMessage(record,
//                    ChessMessage.Type.REPEAL_ACTION, null, null));
//            new Sender(blackPlayer.getIp(), blackPlayer.getPort(), 1000).sendOnly(new ChessMessage(record,
//                    ChessMessage.Type.REPEAL_ACTION, null, null));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void notifyGameOver(MainServer.Client player, ChessMessage.Type condition){
//        logger.info("通知{}游戏结束", player.getUser().getAccount());
//        try {
//            new Sender(player.getIp(), player.getPort(), 1000).sendOnly(new ChessMessage(null,
//                    condition, null, null));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//


    @Override
    public void run() {
        listen();
    }

    public class GameConnection extends PersistentConnectionToClient implements Runnable {
        public GameConnection(){}
        public GameConnection(Socket socket) {
            super(socket);
            connectionlogger = LoggerFactory.getLogger(GameConnection.class);
        }

        @Override
        protected void handle(ChessMessage message) {
            connectionlogger.info("收到类型是 {} 的请求", message.getType());
            switch (message.getType()){
                case HEARTBEAT:
                    handleHeartbeat();
                    break;
                case MOVE:
                    handleMoveAction(message);
                    break;
                case GIVE_UP:
//                    handleGiveUpEvent(message);
                    break;
                case DRAW_REQUEST:
                    handleDrawRequestEvent(message);
                    break;
                case DRAW_ACTION:
                    handleDrawActionEvent(message);
                    break;
                case REPEAL_REQUEST:
                    handleRepealRequestEvent(message);
                    break;
                case REPEAL_ACTION:
                    handleRepealActionEvent(message);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void run(){
            listen();
        }

        private void handleHeartbeat(){
            // 心跳发送
        }

        private void handleDrawRequestEvent(ChessMessage message) {

        }
        private void handleMoveAction(ChessMessage message) {

        }
        private void handleDrawActionEvent(ChessMessage message) {

        }
        private void handleRepealRequestEvent(ChessMessage message) {

        }
        private void handleRepealActionEvent(ChessMessage message) {

        }
    };
}
