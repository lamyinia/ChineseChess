package org.com.client.view;

import org.com.game.role.Chess;
import org.com.game.state.GameRecord;
import org.com.game.state.GameState;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    private BufferedImage boardImage;

    public volatile GameState gameState = new GameState();
    public volatile Chess selectedChess;

    public GamePanel() {
        try {
            boardImage = ImageIO.read(GameRoomTool.VIEW_CHESS_BOARD);
        } catch (IOException e) {
            e.printStackTrace();
            boardImage = new BufferedImage(800, 900, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = boardImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 800, 900);
        }
    }
    public Chess getChessByXY(Point point){
        return gameState.board[point.x][point.y];
    }
    public Chess getChessByXY(int x, int y){
        return gameState.board[x][y];
    }
    public Chess getSelectedChess() {
        return selectedChess;
    }
    public void setSelectedChess(Chess selectedChess) {
        this.selectedChess = selectedChess;
        repaint();
    }
    public GameState getGameState() {
        return gameState;
    }

    public void action(GameRecord record){
        logger.info("棋子将要移动");

        gameState.doAction(record);
        repaint();
    }
    public void repealAction(GameRecord record){
        logger.info("现在正在悔棋");
        gameState.doRepeal(record);
        repaint();
    }

    void paintChess(Graphics g){
        for (int i = 0; i < 10; ++ i){
            for (int j = 0; j < 9; ++ j){
                if (gameState.board[i][j] != null){
                    gameState.board[i][j].drawChess(g, this);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 替换paint方法
        g.drawImage(boardImage, 0, 0, this);
        paintChess(g);
        if (selectedChess != null) selectedChess.drawSelection(g);
    }
}
