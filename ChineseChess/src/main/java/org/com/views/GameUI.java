package org.com.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(GameUI.class);

    private String gameServerIp;
    private int gameServerPort;
    private GamePanel gamePanel = new GamePanel();
    boolean group;

    public GameUI(String gameServerIp, int gameServerPort, boolean group){
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
        this.group = group;

        setSize(800, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameUI("1",1, false);
    }
}
