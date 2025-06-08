package org.com.net;

import org.com.game.role.General;
import org.com.game.state.GameRecord;
import org.com.game.state.GameState;
import org.com.protocal.ChessMessage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameServer extends Server implements Runnable {

    private int gamePort;
    MainServer.Client redPlayer;
    MainServer.Client blackPlayer;

    List <GameRecord> gameRecords = Collections.synchronizedList(new ArrayList<>());
    private GameState gameState = new GameState();

    GameServer(MainServer.Client player1, MainServer.Client player2, boolean separation){
        serverName = "{%s}和{%s}的游戏房间服务器".formatted(player1.getUser().getAccount(), player2.getUser().getAccount());
        logger = LoggerFactory.getLogger(GameServer.class);
        gamePort = findFreeServerSocket(1025);

        if (separation){
            redPlayer = player1;
            blackPlayer = player2;
        } else {
            redPlayer = player2;
            blackPlayer = player1;
        }
        logger.info("{}是红方，{}是黑方", redPlayer.getUser().getAccount(), blackPlayer.getUser().getAccount());
    }

    public int getGamePort() {
        return gamePort;
    }
    @Override
    protected void handle(Socket affair, ChessMessage message) {
        logger.info("%s 收到类型是 %s请求".formatted(serverName, message.getType()));
        switch (message.getType()){
            case MOVE:
                handleMoveAction(message);
                break;
            case GIVE_UP:
                handleGiveUpEvent(message);
                break;
            case DRAW_REQUEST:
                handleDrawRequestEvent(message);
                break;
            case DRAW_ACTION:
                handleDrawActionEvent();
                break;
            case REPEAL_REQUEST:
                handleRepealRequestEvent(message);
                break;
            case REPEAL_ACTION:
                handleRepealActionEvent();
                break;
            default:
                break;
        }
    }
    private void handleMoveAction(ChessMessage message){
        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());

        GameRecord record = (GameRecord) message.getMessage();
//        logger.info("游戏服务器处理 Move 请求，发起者是{},接收者是{}，既{}", message.getSender(), message.getReceiver(), otherPlayer.getUser().getAccount());
        gameRecords.addLast(record);
        gameState.doAction(record);

        try {
            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (record.getEatenChess() instanceof General){
            notifyGameOver(findOtherPlayer(message.getReceiver()), ChessMessage.Type.WIN);
            notifyGameOver(findOtherPlayer(message.getSender()), ChessMessage.Type.LOSE);
        }
    }
    private void handleGiveUpEvent(ChessMessage message){
        MainServer.Client winPlayer = findOtherPlayer(message.getSender());
        MainServer.Client losePlayer = findOtherPlayer(message.getReceiver());
        notifyGameOver(winPlayer, ChessMessage.Type.WIN);
        notifyGameOver(losePlayer, ChessMessage.Type.LOSE);
    }
    private void handleDrawRequestEvent(ChessMessage message){
        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());
        try {
            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.DRAW_REQUEST, null, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleDrawActionEvent(){
        notifyGameOver(redPlayer, ChessMessage.Type.DRAW_ACTION);
        notifyGameOver(blackPlayer, ChessMessage.Type.DRAW_ACTION);
    }
    private void handleRepealRequestEvent(ChessMessage message){
        MainServer.Client otherPlayer = findOtherPlayer(message.getSender());
        try {
            new Sender(otherPlayer.getIp(), otherPlayer.getPort(), 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.REPEAL_REQUEST, null, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleRepealActionEvent(){
        if (gameRecords.isEmpty()) return;

        GameRecord record = gameRecords.removeLast();
        try {
            new Sender(redPlayer.getIp(), redPlayer.getPort(), 1000).sendOnly(new ChessMessage(record,
                    ChessMessage.Type.REPEAL_ACTION, null, null));
            new Sender(blackPlayer.getIp(), blackPlayer.getPort(), 1000).sendOnly(new ChessMessage(record,
                    ChessMessage.Type.REPEAL_ACTION, null, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void notifyGameOver(MainServer.Client player, ChessMessage.Type condition){
        logger.info("通知{}游戏结束", player.getUser().getAccount());
        try {
            new Sender(player.getIp(), player.getPort(), 1000).sendOnly(new ChessMessage(null,
                    condition, null, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    MainServer.Client findOtherPlayer(String account){
        if (account.equals(redPlayer.getUser().getAccount())){
            return blackPlayer;
        } else {
            return redPlayer;
        }
    }


    @Override
    public void run() {
        try {
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
