package org.com.client.controller;

import org.com.client.callback.ConnectionCallBack;
import org.com.client.callback.GameCallBack;
import org.com.client.view.GameUI;
import org.com.game.state.GameRecord;
import org.com.net.PersistentConnectionToServer;
import org.com.protocal.ChessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class GameController implements GameCallBack  {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    private ConnectionCallBack callBack;
    private GameConnection gameConnection;
    public GameUI gameUI;

    String currentAccount, opponentAccount;

    public GameController(){}
    public GameController(ConnectionCallBack callBack, Socket socket, boolean group, String currentAccount, String opponentAccount){
        this.callBack = callBack;
        this.currentAccount = currentAccount;
        this.opponentAccount = opponentAccount;
        gameConnection = new GameConnection(socket);
        new Thread(gameConnection).start();
        gameUI = new GameUI(this, group, currentAccount, opponentAccount);
    }
    public void close(){
        gameUI.dispose();
        gameConnection.close();
    }


    @Override
    public void repealRequestEvent() {
        gameConnection.send(new ChessMessage(null, ChessMessage.Type.REPEAL_REQUEST, currentAccount, opponentAccount));
    }
    @Override
    public void repealActionEvent() {
        gameConnection.send(new ChessMessage(null, ChessMessage.Type.REPEAL_ACTION, currentAccount, opponentAccount));
    }
    @Override
    public void drawRequestEvent() {
        gameConnection.send(new ChessMessage(null, ChessMessage.Type.DRAW_REQUEST, currentAccount, opponentAccount));
    }
    @Override
    public void drawActionEvent() {
        gameConnection.send(new ChessMessage(null, ChessMessage.Type.DRAW_ACTION, currentAccount, opponentAccount));
    }
    @Override
    public void moveChessEvent(GameRecord record) {
        gameUI.gamePanel.action(record);
        gameUI.gamePanel.setSelectedChess(null);
        gameUI.updateHintLabel();
        gameConnection.send(new ChessMessage(record, ChessMessage.Type.MOVE, currentAccount, opponentAccount));
    }

    /**
     * 点击关闭或者投降，告诉服务器我输了
     */
    @Override
    public void exitGameEvent(){
        logger.info("你认输了，你是{}", currentAccount);
        gameConnection.send(new ChessMessage(null, ChessMessage.Type.GIVE_UP, currentAccount, opponentAccount));
    }

    public class GameConnection extends PersistentConnectionToServer implements Runnable {

        public GameConnection(){}
        public GameConnection(Socket socket){
            super(socket);
            connectionlogger = LoggerFactory.getLogger(GameConnection.class);
        }

        @Override
        protected void handle(ChessMessage message) {
            switch (message.getType()){
                case MOVE:
                    handleMove(message);
                    break;
                case REPEAL_REQUEST:
                    handleRepealRequest();
                    break;
                case REPEAL_ACTION:
                    handleRepealAction(message);
                    break;
                case DRAW_REQUEST:
                    handleDrawRequest();
                    break;
                case DRAW_ACTION:
                    handleGameOver("和对手平局");
                    break;
                case WIN:
                    handleGameOver("赢");
                    break;
                case LOSE:
                    handleGameOver("输");
                    break;
                default:
                    break;
            }
        }
        private void handleRepealRequest(){
            gameUI.confirmRepealRequest();
        }
        private void handleRepealAction(ChessMessage message){
            GameRecord record = (GameRecord) message.getMessage();
            gameUI.gamePanel.repealAction(record);
        }
        private void handleDrawRequest(){
            gameUI.confirmDrawRequest();
        }
        private void handleMove(ChessMessage message){
            GameRecord record = (GameRecord) message.getMessage();
            gameUI.gamePanel.action(record);
            gameUI.updateHintLabel();
        }
        private void handleGameOver(String condition){
            gameUI.showResult(condition);
            connectionlogger.info("游戏结束");

            callBack.exitGameRoom();
        }

        @Override
        public void run() {
            startHeartbeat();
            listen();
            heartBeatScheduler.shutdown();
        }
    }
}
