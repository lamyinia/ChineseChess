package org.com.protocal;

public class ChessMessage {
    private Object message;
    private Type type;
    private String sender;
    private String receiver;
    private String senderGroup;
    private String receiverGroup;

    public enum Type {
        LOGIN,
        REGISTER,
        FORGET,
        ACQUIRE_ONLINE,
        FIGHT,
        FIGHT_SUCCESS,
        MOVE,
        EAT,
        PEACE,
        PEACE_SUCCESS,
        PEACE_FAILURE,
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
