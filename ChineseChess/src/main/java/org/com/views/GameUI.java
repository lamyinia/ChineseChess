package org.com.views;

import org.com.game.role.Chess;
import org.com.game.state.GameRecord;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameUI extends JFrame implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(GameUI.class);

    private String gameServerIp;
    private int gameServerPort;

    public GamePanel gamePanel;
    boolean group;

    String currentPlayer, opponentPlayer;

    JLabel hintLabel;

    /**
     * 服务器ip，服务器端口，你的阵营，你是谁，你的对手是谁
     */
    public GameUI(String gameServerIp, int gameServerPort, boolean group, String currentPlayer, String opponentPlayer){
        logger.info("你是{}, 你在和 {} 在博弈象棋", currentPlayer, opponentPlayer);
        setTitle("你是%s方，你的目的是击败%s方".formatted((!group ? "红" : "黑"), (!group ? "黑" : "红")));
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
        this.group = group;
        this.currentPlayer = currentPlayer;
        this.opponentPlayer = opponentPlayer;

        setLayout(new BorderLayout());
        setSize(1000, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        decorateGamePanel();
        decorateFunctionPanel();

        setResizable(false);
        setVisible(true);
    }

    /**
     * 启用双缓冲减少闪烁，添加立体边框，添加鼠标悬停效果
     */
    private void decorateGamePanel() {

        gamePanel = new GamePanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
            }
        };
        gamePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gamePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point targetPoint = GameRoomTool.getPointFromImage(e.getPoint());
                if (GameRoomTool.isALegalPoint(targetPoint)) {
                    if (gamePanel.getChessByXY(targetPoint) != null){
                        gamePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        gamePanel.setCursor(Cursor.getDefaultCursor());
                    }
                } else {
                    gamePanel.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isMyTurn()) return;

                Point targetPoint = GameRoomTool.getPointFromImage(e.getPoint());
                logger.info("点击的 x 坐标是 {}，y 坐标是 {}", (int)targetPoint.getX(), (int)targetPoint.getY());

                if (!GameRoomTool.isALegalPoint(targetPoint)) return;

                Chess originSelection = gamePanel.getSelectedChess();
                Chess nextSelection = gamePanel.getChessByXY(targetPoint);
                handleChessSelection(originSelection, nextSelection, targetPoint);
            }
        });

        add(gamePanel, BorderLayout.CENTER);
    }
    private void handleChessSelection(Chess origin, Chess next, Point target) {
        if (origin == null) handleFirstSelection(next);
        else handleSecondSelection(origin, next, target);
    }
    private void handleFirstSelection(Chess next) {
        if (next != null) {
            if (next.isGroup() == group) {
                gamePanel.setSelectedChess(next);
            } else {
                GameRoomTool.showError(this,"不能使用对方的棋子");
            }
        }
    }
    private void handleSecondSelection(Chess origin, Chess next, Point target) {
        if (next != null) {
            if (next.isGroup() == group) {
                gamePanel.setSelectedChess(origin == next ? null : next);
            } else if (origin.isAbleMove(target, gamePanel.getGameState())) {
                moveChess(origin, next, target, true);
            }
        } else if (origin.isAbleMove(target, gamePanel.getGameState())) {
            moveChess(origin, null, target, false);
        }
    }
    private void moveChess(Chess chess, Chess targetChess, Point target, boolean isEat) {
        Point origin = chess.getPoint();
        chess.setPoint(target);
        GameRecord record = new GameRecord(origin, target, chess, targetChess, isEat);

        gamePanel.action(record);
        gamePanel.setSelectedChess(null);
        updateHintLabel();

        try {
            new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(record, ChessMessage.Type.MOVE,
                    currentPlayer, opponentPlayer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateHintLabel(){
        hintLabel.setText(!gamePanel.getGameState().gameTurn.get() ? "红方回合" : "黑方回合");
    }

    private void decorateFunctionPanel(){
        JPanel functionPanel = new JPanel(new GridLayout(4, 1));
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));
        functionPanel.setBorder(BorderFactory.createEmptyBorder(40, 35, 40, 35)); // 增加内边距

        hintLabel = new JLabel("红方回合", SwingConstants.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        hintLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 30, 30), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));


        JButton repealButton = GameRoomTool.createStyledButton("悔棋");
        JButton drawButton = GameRoomTool.createStyledButton("求和");
        JButton giveUpButton = GameRoomTool.createStyledButton("认输");

        repealButton.setActionCommand("REPEAL");
        drawButton.setActionCommand("DRAW");
        giveUpButton.setActionCommand("GIVE_UP");
        repealButton.addActionListener(this);
        drawButton.addActionListener(this);
        giveUpButton.addActionListener(this);

        functionPanel.add(hintLabel);
        functionPanel.add(Box.createRigidArea(new Dimension(0, 45))); // 增加间距
        functionPanel.add(repealButton);
        functionPanel.add(Box.createRigidArea(new Dimension(0, 45))); // 增加间距
        functionPanel.add(drawButton);
        functionPanel.add(Box.createRigidArea(new Dimension(0, 45))); // 增加间距
        functionPanel.add(giveUpButton);

        add(functionPanel, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        logger.info("功能按钮被点击");
        switch (command){
            case "REPEAL":
                handleRepealEvent();
                break;
            case "DRAW":
                handleDrawEvent();
                break;
            case "GIVE_UP":
                handleGiveUpEvent();
                break;
        }
    }
    private boolean isMyTurn(){
        return gamePanel.getGameState().gameTurn.get() == group;
    }
    private void handleRepealEvent(){
        try {
            new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.REPEAL_REQUEST, currentPlayer, opponentPlayer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleDrawEvent(){
        try {
            new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.DRAW_REQUEST, currentPlayer, opponentPlayer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleGiveUpEvent(){
        try {
            new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.GIVE_UP, currentPlayer, opponentPlayer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirmRepealRequest(){
        int result = JOptionPane.showConfirmDialog(null, "对方请求悔棋，是否确定");
        if (result == JOptionPane.YES_OPTION){
            try {
                new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(null,
                        ChessMessage.Type.REPEAL_ACTION, currentPlayer, opponentPlayer));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void confirmDrawRequest(){
        int result = JOptionPane.showConfirmDialog(null, "对方求和，是否确定");
        if (result == JOptionPane.YES_OPTION){
            try {
                new Sender(gameServerIp, gameServerPort, 1000).sendOnly(new ChessMessage(null,
                        ChessMessage.Type.DRAW_ACTION, currentPlayer, opponentPlayer));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        new GameUI("1",1, false, "1", "2");
    }

}
