package org.com.tools;

import org.com.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class GameRoomTool {
    public static final String MAIN_SERVER_IP = "YOUR_SERVER_IP";
    public static final int MAIN_SERVER_PORT = 65140;
    public static final int SIZE = 80;
    public static final int MARGIN = 40;
    public static final int SPACE = 80;
//    public static final String VIEW_BASE_PATH = "files/img/";
//    public static final String VIEW_CHESS_BOARD = VIEW_BASE_PATH + "ChessBoard.jpg";
    public static final URL VIEW_BASE_PATH = GameRoomTool.class.getClassLoader().getResource("files/img/");
    public static final URL VIEW_CHESS_BOARD = GameRoomTool.class.getClassLoader().getResource("files/img/ChessBoard.jpg");

    public static final String IMG_SUFFIX = ".jpg";
    public static final int DEFAULT_FIND_PORT = 1025;
    public static final int DEFAULT_SOCKET_TIMEOUT = 20000;
    public static final int DEFAULT_HEARTBEAT_PERIOD = 8000;
    public static final int DEFAULT_LOBBY_LIST_REQUEST_PERIOD = 5000;

    public static URL getChessImage(boolean group, String name) {
        return GameRoomTool.class.getClassLoader().getResource(
                "files/img/" + (group ? "Black" : "Red") + name + IMG_SUFFIX
        );
    }
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(120, 40)); // 统一最大尺寸
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 25, 5, 25)
        ));
        button.setFocusPainted(false); // 去除焦点边框
        // 鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
            }
        });
        return button;
    }
    public static void showError(Component component,String message) {
        JOptionPane.getRootFrame().setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(component, message, "操作错误", JOptionPane.WARNING_MESSAGE);
    }
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
