package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;
import org.com.tools.SocketTool;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lanye
 * @date 2025/06/10
 */
public abstract class PersistentConnection {
    protected Socket socket;
    private volatile Instant lastActiveTime;
    private AtomicBoolean isActive;
    private AtomicBoolean isListen;
    private Lock writeLock = new ReentrantLock();

    protected Logger connectionlogger;

    protected abstract void handle(ChessMessage message);

    public PersistentConnection(){}
    public PersistentConnection(Socket socket) {
        this.socket = socket;
        this.lastActiveTime = Instant.now();
        this.isActive = new AtomicBoolean(true);
        this.isListen = new AtomicBoolean(true);
    }

    /**
     * 可以再优化一下断连的方法
     */
    protected void listen(){
        while (isActive()){
            ChessMessage message = receive();
            if (message.getType() == ChessMessage.Type.DISCONNECT_FIRST
                    || message.getType() == ChessMessage.Type.DISCONNECT_SECOND){
                if (message.getType() == ChessMessage.Type.DISCONNECT_FIRST){
                    send(new ChessMessage(null, ChessMessage.Type.DISCONNECT_SECOND, null, null));
                    isActive.set(false);
                    SocketTool.closeSocket(socket);
                }
                break;
            }
            new Thread(new HandleThread(message)).start();
        }

        isListen.set(false);
    }

    public void send(ChessMessage message){
        if (!isActive.get()) return;

        writeLock.lock();
        try {
            SocketTool.sendMessage(socket, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }
    protected ChessMessage receive(){

        try {
            return SocketTool.receiveMessage(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isActive(){
        return isActive.get() && socket != null && !socket.isClosed() && socket.isConnected();
    }
    public void close(){
        if (socket.isConnected()){
            send(new ChessMessage(null, ChessMessage.Type.DISCONNECT_FIRST, null, null));
        }
        isActive.set(false);

        // 如果上面的数据丢失，就陷入死循环了
        while (isListen.get());

        SocketTool.closeSocket(socket);
        connectionlogger.info("连接断开");
    }


    public class HandleThread implements Runnable {
        ChessMessage affair;
        HandleThread(ChessMessage affair){
            this.affair = affair;
        }
        @Override
        public void run() {
            handle(affair);
        }
    }
}
