package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.ChessRoomTool;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* 监听，多线程，先收再发 */
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
    protected ServerSocket socket;
    protected Logger logger;

    public Server(){}
    protected abstract void handle(Socket affair, ChessMessage message);

    protected void listen() throws IOException {
        logger.info(serverName + " 开始监听");
        while (true){
            Socket affair = socket.accept();
            new Thread(new HandleThread(affair)).start();
        }
    }
    private void handleMessage(Socket affair) throws IOException {
        ChessMessage message;
        try {
            message = ChessRoomTool.receiveMessage(affair);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        handle(affair, message);
        ChessRoomTool.closeSocket(affair);
    }
    int findFreeServerSocket(int i){
        if (i >= 65536) return -1;
        try {
            socket = new ServerSocket(i);
            return i;
        } catch (IOException e) {
            return findFreeServerSocket(i+1);
        }
    }

}
