package org.com.server;

import org.com.game.role.General;
import org.com.game.state.GameRecord;
import org.com.game.state.GameState;
import org.com.net.PersistentConnectionToClient;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
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

    private void notifyGameOver(ChessMessage message, String account, ChessMessage.Type condition){
        logger.info("通知{}游戏结束", account);
        findConnection(account).send(new ChessMessage(message, condition, null, null));
    }

    private GameConnection findConnection(String account){
        if (account.equals(redAccount)) return redConnection;
        else return blackConnection;
    }

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
                    handleGiveUpAction(message);
                    break;
                case DRAW_REQUEST:
                    handleDrawRequest(message);
                    break;
                case DRAW_ACTION:
                    handleDrawAction(message);
                    break;
                case REPEAL_REQUEST:
                    handleRepealRequest(message);
                    break;
                case REPEAL_ACTION:
                    handleRepealAction();
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
        private void handleMoveAction(ChessMessage message) {
            GameRecord record = (GameRecord) message.getMessage();
            gameState.doAction(record);
            gameRecords.addLast(record);
            findConnection(message.getReceiver()).send(message);

            if (record.getEatenChess() instanceof General){
                notifyGameOver(null, message.getSender(), ChessMessage.Type.WIN);
                notifyGameOver(null, message.getReceiver(), ChessMessage.Type.LOSE);
                GameServer.this.stopListen();
            }
        }
        private void handleDrawRequest(ChessMessage message) {
            String receiver = message.getReceiver();
            findConnection(receiver).send(message);
        }
        private void handleDrawAction(ChessMessage message) {
            notifyGameOver(null, redAccount, ChessMessage.Type.DRAW_ACTION);
            notifyGameOver(null, blackAccount, ChessMessage.Type.DRAW_ACTION);
            GameServer.this.stopListen();
        }
        private void handleRepealRequest(ChessMessage message) {
            String receiver = message.getReceiver();
            findConnection(receiver).send(message);
        }
        private void handleRepealAction() {
            if (gameRecords.isEmpty()) return;
            GameRecord record = gameRecords.removeLast();
            gameState.doRepeal(record);

            redConnection.send(new ChessMessage(record, ChessMessage.Type.REPEAL_ACTION, null, null));
            blackConnection.send(new ChessMessage(record, ChessMessage.Type.REPEAL_ACTION, null, null));
        }
        private void handleGiveUpAction(ChessMessage message) {
            String losePlayer = message.getSender();
            String winPlayer = message.getReceiver();
            notifyGameOver(null, winPlayer, ChessMessage.Type.WIN);
            notifyGameOver(null, losePlayer, ChessMessage.Type.LOSE);
            GameServer.this.stopListen();
        }
    };
}
