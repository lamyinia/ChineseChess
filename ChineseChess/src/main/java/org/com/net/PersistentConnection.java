package org.com.net;

import org.com.protocal.ChessMessage;
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
    private Lock writeLock = new ReentrantLock();

    protected Logger connectionlogger;

    protected abstract void handle(ChessMessage message);

    public PersistentConnection(){}
    public PersistentConnection(Socket socket) {
        this.socket = socket;
        this.lastActiveTime = Instant.now();
        this.isActive = new AtomicBoolean(true);
    }

    protected void listen(){
        while (isActive()){
            ChessMessage message = receive();
            new Thread(new HandleThread(message)).start();
        }
    }

    public void send(ChessMessage message){
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
        isActive.set(false);
        SocketTool.closeSocket(socket);
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
