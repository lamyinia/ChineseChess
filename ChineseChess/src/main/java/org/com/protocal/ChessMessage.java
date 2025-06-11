package org.com.protocal;

import java.io.Serializable;

// 一份 ChessMessage 多大 ?
public class ChessMessage implements Serializable {
    private Object message;
    private Type type;
    private String sender;
    private String receiver;

    public enum Type {
        HEARTBEAT,
        LOGIN,
        ONLINE,
        OFFLINE,
        REGISTER,
        FORGET,
        ACQUIRE_LOBBY,
        ACQUIRE_LOBBY_SUCCESS,
        ACQUIRE_LOBBY_LIST,
        ACQUIRE_LOBBY_LIST_SUCCESS,
        ACQUIRE_GAME_ROOM,
        ACQUIRE_GAME_ROOM_SUCCESS,
        FIGHT,
        MOVE,
        DRAW_ACTION,
        DRAW_REQUEST,
        REPEAL_ACTION,
        REPEAL_REQUEST,
        WIN,
        LOSE,
        SUCCESS,
        FAILURE,
        GIVE_UP,
    }
    public ChessMessage(){}
    public ChessMessage(Object message, Type type, String sender, String receiver){
        this.message = message;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getSender() {
        return sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public Object getMessage() {
        return message;
    }
    public void setMessage(Object message) {
        this.message = message;
    }
}
