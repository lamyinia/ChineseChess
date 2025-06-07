package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.SocketTool;
import org.com.views.GameUI;
import org.com.views.HallRoom;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ChessClient extends Server implements Runnable {
    private int port;
    HallRoom hallRoom;
    GameUI gameRoom;

    public ChessClient(){
        logger = LoggerFactory.getLogger(ChessClient.class);
        port = findFreeServerSocket(1025);
        serverName = "客户端服务器，端口在 " + String.valueOf(port);
    }
    public int getPort() {
        return port;
    }
    public void setHallRoom(HallRoom hallRoom) {
        this.hallRoom = hallRoom;
    }
    public GameUI getGameRoom() {
        return gameRoom;
    }
    public void setGameRoom(GameUI gameRoom) {
        this.gameRoom = gameRoom;
    }
    public HallRoom getHallRoom() {
        return hallRoom;
    }

    @Override
    protected void handle(Socket affair, ChessMessage message) {
        logger.info("接收到来自主服务器类型是：" + message.getType() + "的响应");
        switch (message.getType()){
            case ACQUIRE_HALL_LIST:
                acquireHallList(message);
                break;
            case FIGHT:
                acquireGameRoom(message);
                break;
            default:
                break;
        }
    }
    private void acquireHallList(ChessMessage message){
        Vector<String> data = (Vector<String>) message.getMessage();
        hallRoom.addHallList(data);
    }
    private void acquireGameRoom(ChessMessage message){
        hallRoom.notifyGameRoom(message);
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
