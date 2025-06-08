package org.com.tools;

import org.com.entity.User;

import java.awt.*;

public class GameRoomTool {
    public static final String MAIN_SERVER_IP = "127.0.0.1";
    public static final int MAIN_SERVER_PORT = 65140;
    public static final int SIZE = 80;
    public static final int MARGIN = 40;
    public static final int SPACE = 80;
    public static final String VIEW_BASE_PATH = "files/img/";
    public static final String VIEW_CHESS_BOARD = VIEW_BASE_PATH + "ChessBoard.jpg";
    public static final String IMG_SUFFIX = ".jpg";

    public static String generateGameId(User player1, User player2){
        String account1 = player1.getAccount(), account2 = player2.getAccount();
        if (account1.compareTo(account2) > 0) {
            String temp = account1;
            account1 = account2;
            account2 = temp;
        }
        return account1 + "_" + account2 + "_" + System.currentTimeMillis();
    }
    public static Point getPointFromImage(Point point){
        Point ret = new Point();
        ret.x = (point.y - MARGIN + SIZE/2) / SPACE;
        ret.y = (point.x - MARGIN + SIZE/2) / SPACE;
        return ret;
    }
    public static boolean isALegalPoint(Point point){
        return point.x >= 0 && point.x <= 9 && point.y >= 0 && point.y <= 8;
    }
}
