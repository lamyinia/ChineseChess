package org.com.views;

import org.com.entity.User;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.ChessRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Vector;

public class HallRoom extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(HallRoom.class);

    private final DefaultListModel model;
    private final JList list;

    private User currentUser;

    private String mainServerIP = "127.0.0.1";
    private int mainServerPort = 65140;

    private void viewHallFrame() {
        setTitle("游戏大厅");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    HallRoom(User user){
        this.currentUser = user;

        viewHallFrame();

        model = new DefaultListModel();
        list = new JList(model);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    logger.info("双击" + list.getSelectedIndex());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
        playerListRequest();
    }
    private void playerListRequest(){
        new Thread(() -> {
            Sender sender = new Sender(mainServerIP, mainServerPort, 1000);
            ChessMessage message = new ChessMessage(null, ChessMessage.Type.ACQUIRE_PLAYER, null, null);
            try {
                sender.sendOnly(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void addPlayerList(Vector<String> data){
        model.clear();
        data.forEach(item -> model.addElement(item));
        list.validate();
    }
}
