//package org.com.net;
//
//import org.com.game.state.GameRecord;
//import org.com.protocal.ChessMessage;
//import org.com.server.Server;
//import org.com.tools.GameRoomTool;
//import org.com.client.view.LobbyUI;
//import org.slf4j.LoggerFactory;
//
//import javax.swing.*;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Vector;
//
//public class ChessClient extends Server implements Runnable {
//    private int port;
//    LobbyUI hallRoom;
//    GameUI gameRoom;
//
//    public ChessClient(){
//        logger = LoggerFactory.getLogger(ChessClient.class);
//        port = findFreeServerSocket(1025);
//        serverName = "客户端服务器";
//    }
//    public int getPort() {
//        return port;
//    }
//    public void setHallRoom(LobbyUI hallRoom) {
//        this.hallRoom = hallRoom;
//    }
//    public GameUI getGameRoom() {
//        return gameRoom;
//    }
//    public void setGameRoom(GameUI gameRoom) {
//        this.gameRoom = gameRoom;
//    }
//    public LobbyUI getHallRoom() {
//        return hallRoom;
//    }
//
//    @Override
//    protected void handle(Socket affair, ChessMessage message) {
//        logger.info("客户端服务器接收到来自服务器类型是：" + message.getType() + "的响应");
//        switch (message.getType()){
//            case ACQUIRE_LOBBY_LIST:
//                acquireHallList(message);
//                break;
//            case FIGHT:
//                acquireGameRoom(message);
//                break;
//            case MOVE:
//                handleMoveAction(message);
//                break;
//            case REPEAL_REQUEST:
//                handleRepealRequest();
//                break;
//            case REPEAL_ACTION:
//                handleRepealAction(message);
//                break;
//            case DRAW_REQUEST:
//                handleDrawRequest();
//                break;
//            case DRAW_ACTION:
//                handleGameOver("和对手平局");
//                break;
//            case WIN:
//                handleGameOver("赢");
//                break;
//            case LOSE:
//                handleGameOver("输");
//                break;
//            default:
//                break;
//        }
//    }
//    private void acquireHallList(ChessMessage message){
//        Vector<String> data = (Vector<String>) message.getMessage();
//        hallRoom.addHallList(data);
//    }
//    private void acquireGameRoom(ChessMessage message){
//        hallRoom.notifyGameRoom(message, message.getReceiver(), message.getSender());
//    }
//    private void handleRepealRequest(){
//        gameRoom.confirmRepealRequest();
//    }
//    private void handleDrawRequest(){
//        gameRoom.confirmDrawRequest();
//    }
//    private void handleMoveAction(ChessMessage message){
//        gameRoom.gamePanel.action((GameRecord)message.getMessage());
//        gameRoom.updateHintLabel();
//    }
//    private void handleRepealAction(ChessMessage message){
//        gameRoom.gamePanel.repealAction((GameRecord)message.getMessage());
//    }
//    private void handleGameOver(String condition){
//        JOptionPane.showMessageDialog(null, "你%s了".formatted(condition));
//
//        logger.info("游戏结束");
//
//        gameRoom.dispose();
//        onlineHandle();
//
//        hallRoom.HallListRequest();
//        hallRoom.setVisible(true);
//    }
//    private void onlineHandle(){
//        try {
//            new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT,1000).sendOnly
//                    (new ChessMessage(new Object[] {hallRoom.currentUser, port}, ChessMessage.Type.ONLINE, null, null));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            listen();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
