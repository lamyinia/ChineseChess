package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.ChessRoomTool;
import org.com.views.GameRoom;
import org.com.views.HallRoom;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ChessClient extends Server implements Runnable{
    private int port;
    HallRoom hallRoom;
    GameRoom gameRoom;

    public ChessClient(){
        logger = LoggerFactory.getLogger(ChessClient.class);
        port = findFreeServerSocket(1025);
        serverName = "客户端服务器" + String.valueOf(port);
    }
    public int getPort() {
        return port;
    }
    public void setHallRoom(HallRoom hallRoom) {
        this.hallRoom = hallRoom;
    }
    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    private void acquirePlayerList(Socket affair, ChessMessage message){
        Vector<String> data = (Vector<String>) message.getMessage();
        ChessMessage response = new ChessMessage(null, null, null, null);
        try {
            ChessRoomTool.sendMessage(affair, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hallRoom.addPlayerList(data);
    }

    @Override
    protected void handle(Socket affair, ChessMessage message) {
        logger.info("接收到来自主服务器类型是：" + message.getType() + "的响应");
        switch (message.getType()){
            case ACQUIRE_PLAYER:
                acquirePlayerList(affair, message);
                break;
            default:
                break;
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
