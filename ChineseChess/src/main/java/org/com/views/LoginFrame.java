package org.com.views;

import org.com.entity.User;
import org.com.net.ChessClient;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.ChessRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
    延迟实现的任务：
    - 窗口关闭时发送下线通知
 */
public class LoginFrame extends JFrame implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(LoginFrame.class);
    private JTextField accountText;
    private JTextField passwordText;
    private String mainServerIP = "127.0.0.1";
    private int mainServerPort = 65140;
    ChessClient clientServer;

    HallRoom hallRoom;

    LoginFrame(){
        clientServer = new ChessClient();
        new Thread(clientServer).start();

        viewLoginFrame();
        viewAccount();
        viewPassword();
        viewConfirmButton();
        viewRegisterButton();

        setVisible(true);
    }
    private void viewLoginFrame() {
        setTitle("一起开始中国象棋吧");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
    }
    private void viewPassword() {
        passwordText = new JTextField();
        JLabel passwordLabel = new JLabel("请输入密码:");
        passwordLabel.setBounds(20, 80, 80, 20);
        passwordText.setBounds(90, 80, 250, 20);
        add(passwordLabel);
        add(passwordText);
    }
    private void viewAccount() {
        accountText = new JTextField();
        JLabel accountLabel = new JLabel("请输入账号:");
        accountLabel.setBounds(20, 20, 80, 20);
        accountText.setBounds(90, 20, 250, 20);
        add(accountLabel);
        add(accountText);
    }
    private void viewRegisterButton() {
        JButton registerButton = new JButton("注册");
        registerButton.setBounds(140, 200, 100, 30);
        registerButton.setActionCommand("register");
        registerButton.addActionListener(this);
        add(registerButton);
    }
    private void viewConfirmButton() {
        JButton confirmButton = new JButton("确定");
        confirmButton.setBounds(140, 140, 100, 30);
        confirmButton.setActionCommand("loginConfirm");
        confirmButton.addActionListener(this);
        add(confirmButton);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "loginConfirm":
                try {
                    loginConfirm(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "register":
                register();
                break;
            default:
                break;
        }
    }
    private void loginConfirm(ActionEvent e) throws IOException {
        String account = accountText.getText();
        String password = passwordText.getText();
        if ("".equals(account) || "".equals(password)){
            ChessRoomTool.showErrorBox("账号密码不能为空");
            return;
        }

        Sender sender = new Sender();
        sender.connect(mainServerIP, mainServerPort,1000);

        ChessMessage message = new ChessMessage(new Object[] {new User(account, password), clientServer.getPort()}, ChessMessage.Type.LOGIN, null, null);
        ChessMessage response = sender.send(message);

        if (response.getType() == ChessMessage.Type.SUCCESS){
            logger.info("登录成功");
            dispose();
            hallRoom = new HallRoom(new User(account, password));
            clientServer.setHallRoom(hallRoom);
        } else {
            ChessRoomTool.showErrorBox("账号密码错误");
        }
    }
    private void register(){

    }
    public static void main(String[] args) {
        new LoginFrame();
    }
}
