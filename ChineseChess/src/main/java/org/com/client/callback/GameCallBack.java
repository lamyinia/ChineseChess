package org.com.client.callback;

import org.com.game.state.GameRecord;

public interface GameCallBack {
    void repealRequestEvent();
    void repealActionEvent();
    void drawRequestEvent();
    void drawActionEvent();
    void moveChessEvent(GameRecord record);
    void exitGameEvent();
}
