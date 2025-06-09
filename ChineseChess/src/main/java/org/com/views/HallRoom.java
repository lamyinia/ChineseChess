package org.com.views;

import org.com.entity.User;
import org.com.net.ChessClient;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

public class HallRoom extends JFrame {
    private static class PlayerListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            label.setIconTextGap(15);

            label.setIcon(new ImageIcon(createPlayerIcon()));

            if (!isSelected) {
                label.setBackground(index % 2 == 0 ?
                        new Color(250, 250, 255) :
                        new Color(245, 248, 255));
            }
            return label;
        }

        private Image createPlayerIcon() {
            int size = 30;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();

            // 启用抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制圆形头像
            g2d.setColor(new Color(70, 130, 180, 100));
            g2d.fillOval(0, 0, size, size);

            // 绘制玩家图标
            g2d.setColor(new Color(70, 130, 180));
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 18));

            FontMetrics fm = g2d.getFontMetrics();
            String text = "象";
            int x = (size - fm.stringWidth(text)) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();

            g2d.drawString(text, x, y);

            g2d.dispose();
            return img;
        }
    }
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    private JPanel createInfoPanel(String title, String value, Color bgColor, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        titleLabel.setForeground(titleColor);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        valueLabel.setForeground(Color.DARK_GRAY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
    private void decorateStatusPanel() {
        statusPanel.setLayout(new GridLayout(1, 3, 10, 0));
        statusPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        statusPanel.setBackground(new Color(240, 240, 245));

        // 用户信息
        JPanel userPanel = createInfoPanel("当前账号", currentUser.getAccount(), Color.WHITE, new Color(100, 149, 237));

        // 刷新按钮
        JButton refreshButton = new JButton("刷新列表");
        styleButton(refreshButton, new Color(50, 205, 50));
        refreshButton.addActionListener(e -> HallListRequest());

        // 退出按钮
        JButton exitButton = new JButton("退出大厅");
        styleButton(exitButton, new Color(220, 20, 60));
        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(HallRoom.this, "确定要退出游戏大厅吗？", "退出确认",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                offLineHandle();
                System.exit(0);
            }
        });

        statusPanel.add(userPanel);
        statusPanel.add(refreshButton);
        statusPanel.add(exitButton);

        add(statusPanel, BorderLayout.SOUTH);
    }




    private static final Logger logger = LoggerFactory.getLogger(HallRoom.class);

    private final DefaultListModel model;
    private final JList list;
    private final JPanel statusPanel;

    public User currentUser;
    private Vector<String> onlinePlayer;//登录的用户数据

    ChessClient clientServer;  // 离线后能否关闭 ChessClient

    HallRoom(User user, ChessClient chessServer){
        this.currentUser = user;
        this.clientServer = chessServer;
        model = new DefaultListModel();
        list = new JList(model);
        statusPanel = new JPanel();

        decorateHallRoom();
        decorateHallList();
        decorateStatusPanel();

        setVisible(true);
        HallListRequest();
    }
    private void decorateHallRoom() {
        setTitle("游戏大厅");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("正在关闭窗口");
                int result = JOptionPane.showConfirmDialog(null, "是否关闭窗口");
                if (result == JOptionPane.YES_OPTION){
                    offLineHandle();
                    System.exit(0);
                }
            }
        });
    }
    private void decorateHallList() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel titleLabel = new JLabel("在线玩家列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel("0 人在线");
        countLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        countLabel.setForeground(new Color(200, 230, 255));
        countLabel.setName("countLabel");
        headerPanel.add(countLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        list.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        list.setBackground(new Color(255, 255, 255));
        list.setSelectionBackground(new Color(70, 130, 180, 100));
        list.setSelectionForeground(new Color(50, 90, 140));
        list.setBorder(new EmptyBorder(10, 15, 10, 15));
        list.setFixedCellHeight(45);
        list.setCellRenderer(new PlayerListCellRenderer());

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    logger.info("双击" + list.getSelectedIndex());
                    String receiver = onlinePlayer.elementAt(list.getSelectedIndex());

                    if (receiver.equals(currentUser.getAccount())) {
                        JOptionPane.showMessageDialog(HallRoom.this, "不能挑战自己", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    ChessMessage message = new ChessMessage(null, ChessMessage.Type.FIGHT,
                            currentUser.getAccount(), receiver);

                    try {
                        ChessMessage response = new Sender(GameRoomTool.MAIN_SERVER_IP,
                                GameRoomTool.MAIN_SERVER_PORT, 1000).send(message);
                        if (response.getType() == ChessMessage.Type.SUCCESS) {
                            logger.info("{} 和 {} 对局的房间请求成功", currentUser.getAccount(), receiver);
                            notifyGameRoom(response, currentUser.getAccount(), receiver);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                list.setCursor(Cursor.getDefaultCursor());
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 200, 210)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void HallListRequest(){
        new Thread(() -> {
            try {
                new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, 1000).sendOnly(new ChessMessage(null,
                        ChessMessage.Type.ACQUIRE_HALL_LIST, currentUser.getAccount(), null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void notifyGameRoom(ChessMessage message, String currentPlayer, String opponentPlayer){
        setVisible(false);
        offLineHandle();

        Object[] thing = (Object[]) message.getMessage();
        GameUI gameRoom = new GameUI((String) thing[0], (int) thing[1], (boolean) thing[2], currentPlayer, opponentPlayer);
        clientServer.setGameRoom(gameRoom);
    }
    public void addHallList(Vector<String> data) {
        onlinePlayer = data;
        model.clear();
        data.forEach(model::addElement);

        JLabel countLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        countLabel.setText(data.size() + " 人在线");

        list.validate();
    }
    private void offLineHandle(){
        try {
            new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, 1000).sendOnly(new ChessMessage(null,
                    ChessMessage.Type.OFFLINE, currentUser.getAccount(), null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
