package org.com.protocal;

import java.io.Serializable;

public class ChessMessage implements Serializable {
    private Object message;
    private Type type;
    private String sender;
    private String receiver;

    public enum Type {
        LOGIN,
        OFFLINE,
        REGISTER,
        FORGET,
        ACQUIRE_HALL_LIST,
        FIGHT,
        MOVE,
        EAT,
        DRAW,
        SUCCESS,
        FAILURE,
        GIVE_UP,
        REPEAL
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
