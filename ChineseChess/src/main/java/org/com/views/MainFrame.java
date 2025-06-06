package org.com.views;

import org.com.protocal.ChessMessage;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(){
        setSize(800, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(new GamePanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
