package org.com.client.callback;

import org.com.entity.User;

public interface ConnectionCallBack {
    void acquireLobbyConnection(User user, int lobbyPort);
    void acquireGameRoomConnection(boolean group, String opponent, int gameRoomPort);
    void exitGameRoom();
}
