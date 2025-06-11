package org.com.client.controller;

import org.com.client.callback.ConnectionCallBack;
import org.com.client.callback.LoginCallBack;
import org.com.client.view.LoginUI;
import org.com.entity.User;
import org.com.net.Sender;
import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements LoginCallBack {
    LoginUI loginUI;

    ConnectionCallBack callBack;
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);


    public LoginController(ConnectionCallBack callBack){
        this.callBack = callBack;
        loginUI = new LoginUI(this);
    }

    @Override
    public void loginEvent(String account, String password) {
        ChessMessage response =
                new Sender(GameRoomTool.MAIN_SERVER_IP, GameRoomTool.MAIN_SERVER_PORT, GameRoomTool.DEFAULT_SOCKET_TIMEOUT)
                        .send(new ChessMessage(new User(account, password), ChessMessage.Type.LOGIN, null, null));
        if (response.getType() == ChessMessage.Type.SUCCESS){
            int lobbyPort = (int) response.getMessage();
            loginUI.dispose();
            callBack.acquireLobbyConnection(new User(account, password), lobbyPort);
        } else {
            loginUI.showError((String) response.getMessage());
        }
    }
}
