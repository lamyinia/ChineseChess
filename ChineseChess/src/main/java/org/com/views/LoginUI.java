package org.com.views;

import org.com.entity.User;
import org.com.net.ChessClient;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.com.tools.SocketTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LoginUI extends JFrame implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(LoginUI.class);
    private JTextField accountText;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel contentPanel;

    ChessClient clientServer;

    HallRoom hallRoom;

    public LoginUI() {
        clientServer = new ChessClient();
        new Thread(clientServer).start();

        initUI();
        setVisible(true);
    }
    private Component getComponentByName(Container container, Class<?> componentClass) {
        for (Component comp : container.getComponents()) {
            if (componentClass.isInstance(comp)) {
                return comp;
            } else if (comp instanceof Container) {
                Component child = getComponentByName((Container) comp, componentClass);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }
    private void initUI() {
        setTitle("中国象棋 - 登录");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true); // 去掉默认边框
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20)); // 圆角窗体

        // 创建主内容面板
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 248, 250));

        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createFormPanel(), BorderLayout.CENTER);
        contentPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        setDropShadow();
    }

    private void setDropShadow() {
        // 创建一个带阴影的面板
        JPanel shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        shadowPanel.setBackground(new Color(230, 235, 240));
        shadowPanel.add(contentPanel, BorderLayout.CENTER);

        // 设置窗体内容
        getContentPane().add(shadowPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 游戏标题
        JLabel titleLabel = new JLabel("中国象棋");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 副标题
        JLabel subtitleLabel = new JLabel("经典策略，智慧对决");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(230, 230, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // 添加象棋图标
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(createChessIcon());
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private Icon createChessIcon() {
        int size = 60;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制背景圆形
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(0, 0, size, size);

        // 绘制象棋图标
        g2d.setColor(new Color(70, 130, 180));
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 36));

        FontMetrics fm = g2d.getFontMetrics();
        String text = "象";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();

        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(img);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 账号输入
        JPanel accountPanel = createInputPanel("账号：", "", true);
        accountText = (JTextField) getComponentByName(accountPanel, JTextField.class);

        // 密码输入
        JPanel passwordPanel = createInputPanel("密码：", "", false);
        passwordText = (JPasswordField) getComponentByName(passwordPanel, JPasswordField.class);

        // 记住密码
        JCheckBox rememberCheck = new JCheckBox("记住密码");
        rememberCheck.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        rememberCheck.setOpaque(false);
        rememberCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        rememberCheck.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 0));

        // 忘记密码
        JLabel forgotLabel = new JLabel("忘记密码?");
        forgotLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(70, 130, 180));
        forgotLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginUI.this, "请联系管理员重置密码", "忘记密码",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 按钮容器
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // 登录按钮
        loginButton = createGradientButton("登录", new Color(70, 130, 180));
        loginButton.setActionCommand("LOGIN_CONFIRM");
        loginButton.addActionListener(this);

        // 注册按钮
        registerButton = createGradientButton("注册", new Color(100, 149, 237));
        registerButton.setActionCommand("REGISTER");
        registerButton.addActionListener(this);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        formPanel.add(accountPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.setOpaque(false);
        optionPanel.add(rememberCheck, BorderLayout.WEST);
        optionPanel.add(forgotLabel, BorderLayout.EAST);
        formPanel.add(optionPanel);

        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JPanel createInputPanel(String labelText, String placeholder, boolean isAccount) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(60, 30));

        JComponent inputField;
        if (isAccount) {
            inputField = new JTextField(placeholder) {
                // 重写paintComponent添加圆角效果
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 绘制圆角背景
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                    super.paintComponent(g);
                    g2.dispose();
                }
            };
        } else {
            inputField = new JPasswordField() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            ((JPasswordField) inputField).setEchoChar('*');
        }

        inputField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputField.setBackground(Color.WHITE);
        inputField.setOpaque(false); // 让自定义背景生效

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.add(inputField, BorderLayout.CENTER);

        panel.add(label, BorderLayout.WEST);
        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createGradientButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color startColor = baseColor;
                Color endColor = baseColor.darker();

                if (getModel().isPressed()) {
                    startColor = baseColor.darker();
                    endColor = baseColor;
                }

                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.setOpaque(false);

        // 版权信息
        JLabel copyrightLabel = new JLabel("© 2025 中国象棋 版权所有 lanye");
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(150, 150, 150));

        footerPanel.add(copyrightLabel);

        return footerPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        logger.info("{}命令", command);
        switch (command) {
            case "LOGIN_CONFIRM":
                try {
                    loginConfirm(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "REGISTER":
                register();
                break;
            default:
                break;
        }
    }


    private void loginConfirm(ActionEvent e) throws IOException {
        String account = accountText.getText();
        String password = new String(passwordText.getPassword());

        if ("".equals(account) || "".equals(password)){
            SocketTool.showErrorBox("账号密码不能为空");
            return;
        }

        new Thread(() -> {
            try {
                ChessMessage response = new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, 1000).send
                        (new ChessMessage(new Object[] {new User(account, password), clientServer.getPort()}, ChessMessage.Type.LOGIN, null, null));
                SwingUtilities.invokeLater(() -> {
                    if (response.getType() == ChessMessage.Type.SUCCESS) {
                        logger.info("登录成功");
                        dispose();
                        hallRoom = new HallRoom(new User(account, password), clientServer);
                        clientServer.setHallRoom(hallRoom);
                    } else {
                        logger.info("登录失败");
                        SocketTool.showErrorBox((String)response.getMessage());

                        JOptionPane.getRootFrame().setAlwaysOnTop(true);
                        Toolkit.getDefaultToolkit().beep();
                    }
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    SocketTool.showErrorBox("连接服务器失败: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void register() {
        JDialog registerDialog = new JDialog(this, "注册新账号", true);
        registerDialog.setSize(400, 300);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new BorderLayout());
        registerDialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel accountPanel = createInputPanel("账号：", "请输入账号", true);
        JPanel passwordPanel = createInputPanel("密码：", "请输入密码", false);
        JPanel confirmPanel = createInputPanel("确认：", "请再次输入密码", false);

        JButton registerBtn = createGradientButton("注册", new Color(100, 149, 237));
        registerBtn.addActionListener(e -> {
            // 这里添加注册逻辑
            JOptionPane.showMessageDialog(registerDialog, "注册功能尚未实现", "提示",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        formPanel.add(accountPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(confirmPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(registerBtn);

        registerDialog.add(formPanel);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new LoginUI();
        });
    }
}