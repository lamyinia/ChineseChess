package org.com.views;

import org.com.game.role.Chess;
import org.com.game.role.ChessFactory;
import org.com.game.state.GameRecord;
import org.com.game.state.GameState;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    private BufferedImage boardImage;
    public volatile GameState gameState = new GameState();
    public volatile Chess selectedChess;

    List<GameRecord> gameRecords = Collections.synchronizedList(new ArrayList<>());

    public GamePanel() {
        initializeChess();
        try {
            boardImage = ImageIO.read(new File(GameRoomTool.VIEW_CHESS_BOARD));
        } catch (IOException e) {
            e.printStackTrace();
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
        gameRecords.addLast(record);

        gameState.doAction(record);
        repaint();
    }
    public void repealAction(){
        if (gameRecords.isEmpty()) return;
        logger.info("现在正在悔棋");

        gameState.doRepeal(gameRecords.removeLast());
        repaint();
    }

    private void initializeChess(){
        String[] chessName = {"Rook", "Horse", "Bishop", "Guard", "General", "Guard", "Bishop", "Horse",
                "Rook", "Cannon", "Cannon", "Solider", "Solider", "Solider", "Solider", "Solider"};
        int[] chessXs = {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3, 3, 3, 3};
        int[] chessYs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 1, 7, 0, 2, 4, 6, 8};

        for (int i = 0; i < 16; ++ i){
            gameState.board[chessXs[i]][chessYs[i]] = ChessFactory.create(chessName[i], false, new Point(chessXs[i], chessYs[i]));
        }
        for (int i = 0; i < 16; ++ i){
            gameState.board[9 - chessXs[i]][chessYs[i]] = ChessFactory.create(chessName[i], true, new Point(9 - chessXs[i], chessYs[i]));
        }
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
