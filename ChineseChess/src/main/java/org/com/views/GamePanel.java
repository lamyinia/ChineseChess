package org.com.views;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel {
    String VIEW_BASE_PATH = "files/img2/";
    String VIEW_CHESS_BOARD = VIEW_BASE_PATH + "chessboard.jpg";
    private BufferedImage boardImage; // 使用BufferedImage便于获取尺寸

    public GamePanel() {
        try {
            // 加载棋盘图片（使用绝对路径或确保路径正确）
            boardImage = ImageIO.read(new File(VIEW_CHESS_BOARD));
        } catch (IOException e) {
            e.printStackTrace();
            boardImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (boardImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = boardImage.getWidth();
            int imgHeight = boardImage.getHeight();
            double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;

            g.drawImage(boardImage, x, y, scaledWidth, scaledHeight, this);
        } else {
            g.setColor(Color.RED);
            g.drawString("棋盘图片加载失败", 50, 50);
        }
    }
}
