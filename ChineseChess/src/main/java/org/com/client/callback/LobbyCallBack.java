package org.com.client.callback;

public interface LobbyCallBack {
    void fightEvent(String sender, String receiver);
    void logoutEvent();
    void refreshLobbyList();
    void fightRequestEvent();
}
