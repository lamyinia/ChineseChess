package org.com.protocal;

import java.io.Serializable;

public class ChessMessage implements Serializable {
    private Object message;
    private Type type;
    private String sender;
    private String receiver;
    private String senderGroup;
    private String receiverGroup;

    public enum Type {
        LOGIN,
        OFFLINE,
        REGISTER,
        FORGET,
        ACQUIRE_PLAYER,
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

    public String getSenderGroup() {
        return senderGroup;
    }

    public void setSenderGroup(String senderGroup) {
        this.senderGroup = senderGroup;
    }

    public String getReceiverGroup() {
        return receiverGroup;
    }

    public void setReceiverGroup(String receiverGroup) {
        this.receiverGroup = receiverGroup;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "ChessMessage{" +
                "message=" + message +
                ", type=" + type +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderGroup='" + senderGroup + '\'' +
                ", receiverGroup='" + receiverGroup + '\'' +
                '}';
    }
}
