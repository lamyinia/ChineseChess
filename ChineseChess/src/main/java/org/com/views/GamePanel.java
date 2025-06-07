package org.com.views;

import org.com.game.role.ChessFactory;
import org.com.game.state.GameState;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    private BufferedImage boardImage;
    private AtomicBoolean isLocked = new AtomicBoolean(false);
    GameState gameState = new GameState();

    public GamePanel() {
        initializeChess();
        try {
            boardImage = ImageIO.read(new File(GameRoomTool.VIEW_CHESS_BOARD));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addMouseEvent();
    }
    private void addMouseEvent(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLocked.get()) return;
                Point point = GameRoomTool.getPointFromImage(e.getPoint());
                logger.info("点击的 x 坐标是 {}，y 坐标是 {}", (int)point.getX(), (int)point.getY());
            }
        });
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
    public void paint(Graphics g){
        super.paint(g);
        g.drawImage(boardImage, 0, 0, this);
        paintChess(g);
    }
}
