package org.com.net;

import org.com.entity.User;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class GameServer extends Server implements Runnable {

    private int gamePort;
    MainServer.Client RedPlayer, BlackPlayer;

    GameServer(MainServer.Client player1, MainServer.Client player2, boolean separation){
        serverName = "{%s}和{%s}的游戏房间服务器".formatted(player1.getUser().getAccount(), player2.getUser().getAccount());
        logger = LoggerFactory.getLogger(GameServer.class);
        gamePort = findFreeServerSocket(1025);
        if (separation){
            RedPlayer = player1;
            BlackPlayer = player2;
        } else {
            RedPlayer = player2;
            BlackPlayer = player1;
        }
    }

    public int getGamePort() {
        return gamePort;
    }
    @Override
    protected void handle(Socket affair, ChessMessage message) {

    }
    @Override
    public void run() {
        String gameId = GameRoomTool.generateGameId(RedPlayer.getUser(), BlackPlayer.getUser());
        try {
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
