package org.com.views;

import org.com.entity.User;
import org.com.net.ChessClient;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
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
    private Vector<String> onlinePlayer;//登录的用户数据

    ChessClient clientServer;

    HallRoom(User user, ChessClient chessServer){
        this.currentUser = user;
        this.clientServer = chessServer;
        model = new DefaultListModel();
        list = new JList(model);

        viewHallFrame();
        decorateList();

        setVisible(true);
        HallListRequest();
    }
    private void viewHallFrame() {
        setTitle("游戏大厅");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private void decorateList() {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    logger.info("双击" + list.getSelectedIndex());
                    String receiver = onlinePlayer.elementAt(list.getSelectedIndex());

                    if (receiver.equals(currentUser.getAccount())) return;
                    ChessMessage message = new ChessMessage(null, ChessMessage.Type.FIGHT, currentUser.getAccount(), receiver);

                    try {
                        ChessMessage response = new Sender(GameRoomTool.MAIN_SERVER_IP,
                                GameRoomTool.MAIN_SERVER_PORT, 1000).send(message);
                        if (response.getType() == ChessMessage.Type.SUCCESS){
                            logger.info("{} 和 {} 对局的房间请求成功", currentUser.getAccount(), receiver);
                            notifyGameRoom(response);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void HallListRequest(){
        new Thread(() -> {
            try {
                new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, 1000).sendOnly(new ChessMessage(null,
                        ChessMessage.Type.ACQUIRE_HALL_LIST, null, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void notifyGameRoom(ChessMessage message){
        setVisible(false);
        Object[] thing = (Object[]) message.getMessage();
        new GameUI((String)thing[0], (int)thing[1], (boolean)thing[2]);
    }
    public void addHallList(Vector<String> data){
        onlinePlayer = data;
        model.clear();
        data.forEach(item -> model.addElement(item));
        list.validate();
    }
}
