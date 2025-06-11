package org.com.client.controller;

import org.com.client.callback.ConnectionCallBack;
import org.com.client.callback.GameCallBack;
import org.com.client.view.GameUI;
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

    String currentAccount;

    public GameController(){}
    public GameController(ConnectionCallBack callBack, Socket socket, boolean group, String currentAccount, String opponent){
        this.callBack = callBack;
        this.currentAccount = currentAccount;
        gameConnection = new GameConnection(socket);
        new Thread(gameConnection).start();
        gameUI = new GameUI(this, group, currentAccount, opponent);
    }

    @Override
    public void exitGame(){
        callBack.exitGameRoom();
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

            }
        }
        @Override
        public void run() {
            startHeartbeat();
            listen();
            heartBeatScheduler.shutdown();
        }
    }
}
