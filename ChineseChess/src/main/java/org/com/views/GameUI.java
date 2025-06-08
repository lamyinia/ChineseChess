package org.com.views;

import org.com.game.role.Chess;
import org.com.game.state.GameRecord;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(GameUI.class);

    private String gameServerIp;
    private int gameServerPort;
    private GamePanel gamePanel;
    private AtomicBoolean isLocked = new AtomicBoolean(false);
    boolean group;

    JLabel hintLabel = new JLabel();

    public GameUI(String gameServerIp, int gameServerPort, boolean group){
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
        this.group = group;

        setLayout(new BorderLayout());
        setSize(800, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        decorateGamePanel();
        decorateFunctionPanel();

        setResizable(false);
        setVisible(true);
    }

    private void decorateGamePanel() {
        gamePanel = new GamePanel();
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLocked.get()) return;

                Point targetPoint = GameRoomTool.getPointFromImage(e.getPoint());
                logger.info("点击的 x 坐标是 {}，y 坐标是 {}", (int)targetPoint.getX(), (int)targetPoint.getY());

                if (!GameRoomTool.isALegalPoint(targetPoint)) return;

                Chess originSelection = gamePanel.getSelectedChess();
                Chess nextSelection = gamePanel.getChessByXY(targetPoint);

                if (originSelection == null){
                    if (nextSelection != null){
                        if (nextSelection.getGroup() == group){
                            gamePanel.setSelectedChess(nextSelection);
                        } else {
                            JOptionPane.showMessageDialog(null, "不能使用对方的棋子");
                        }
                    }
                } else {
                    if (nextSelection != null){
                        if (nextSelection.getGroup() == group){
                            gamePanel.setSelectedChess(nextSelection);
                        } else {
                            
                        }
                    } else {
                        Point originPoint = gamePanel.getSelectedChess().getPoint();
                        if (originSelection.isAbleMove(targetPoint, gamePanel.getGameState())){
                            originSelection.setPoint(targetPoint);
                            GameRecord moveRecord = new GameRecord(originPoint, targetPoint, originSelection, null, false);
                            gamePanel.action(moveRecord);

                            gamePanel.setSelectedChess(null);
                            // 发送移动信号
                        }
                    }
                }
            }
        });

        add(gamePanel, BorderLayout.CENTER);
    }
    private void decorateFunctionPanel(){

    }

    public static void main(String[] args) {
        new GameUI("1",1, false);
    }
}
