package org.com.server;

import org.com.protocal.ChessMessage;
import org.com.tools.SocketTool;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监听发来的 Socket，处理事务
 * @author lanye
 * @date 2025/06/08
 */
public abstract class Server {
    class HandleThread implements Runnable {
        Socket affair;
        HandleThread(Socket affair){
            this.affair = affair;
        }
        @Override
        public void run() {
            try {
                handleMessage(affair);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected String serverName;
    protected Logger logger;
    protected volatile ServerSocket socket;
    protected final AtomicBoolean running = new AtomicBoolean(false);

    public Server(){}
    protected abstract void handle(Socket affair, ChessMessage message);

    protected void listen(){
        logger.info(serverName + ", 端口在 " + socket.getLocalPort() + " 开始监听");
        running.set(true);

        while (running.get()){
            Socket affair = null;
            try {
                affair = socket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new Thread(new HandleThread(affair)).start();
        }

        logger.info(serverName + ", 端口在 " + socket.getLocalPort() + " 停止监听");
    }
    private void handleMessage(Socket affair) throws IOException {
        ChessMessage message;
        try {
            message = SocketTool.receiveMessage(affair);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        handle(affair, message);
    }
    public void stopListen(){
        running.set(false);
    }
    protected int findFreeServerSocket(int i){
        if (i >= 65536) return -1;
        try {
            socket = new ServerSocket(i);
            return i;
        } catch (IOException e) {
            return findFreeServerSocket(i+1);
        }
    }
}
