package org.com.client.view;

import org.com.client.callback.LobbyCallBack;
import org.com.entity.User;
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
import java.util.Vector;

public class LobbyUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(LobbyUI.class);

    private final DefaultListModel model;
    private final JList list;
    private final JPanel statusPanel;

    public String currentAccount;
    private Vector <String> onlinePlayer;//登录的用户数据
    LobbyCallBack callBack;

    public LobbyUI(LobbyCallBack callBack, String currentAccount){
        this.callBack = callBack;
        this.currentAccount = currentAccount;
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

                    if (receiver.equals(currentAccount)) {
                        JOptionPane.showMessageDialog(LobbyUI.this, "不能挑战自己", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    callBack.fightEvent(currentAccount, receiver);
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
        callBack.refreshLobbyList();
    }
    public void notifyGameRoom(ChessMessage message, String currentPlayer, String opponentPlayer){
        setVisible(false);
        offLineHandle();

        Object[] thing = (Object[]) message.getMessage();
    }
    public void addLobbyList(Vector<String> data) {
        onlinePlayer = data;
        model.clear();
        data.forEach(model::addElement);

        JLabel countLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        countLabel.setText(data.size() + " 人在线");

        list.validate();
    }
    private void offLineHandle(){
        callBack.logoutEvent();
    }


    /**
     * 渲染部分
     * @author lanye
     * @date 2025/06/11
     */
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
        JPanel userPanel = createInfoPanel("当前账号", currentAccount, Color.WHITE, new Color(100, 149, 237));

        // 刷新按钮
        JButton refreshButton = new JButton("刷新列表");
        styleButton(refreshButton, new Color(50, 205, 50));
        refreshButton.addActionListener(e -> HallListRequest());

        // 退出按钮
        JButton exitButton = new JButton("退出大厅");
        styleButton(exitButton, new Color(220, 20, 60));
        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(LobbyUI.this, "确定要退出游戏大厅吗？", "退出确认",
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
}
