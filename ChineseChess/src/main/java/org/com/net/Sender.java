package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.SocketTool;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/* 请求，单线程先发再收*/
public class Sender {
    Socket socket;
    public Sender(){
        socket = new Socket();
    }
    public Sender(String ip, int port, int timeout){
        socket = new Socket();
        connect(ip, port, timeout);
    }
    public void connect(String ip, int port, int timeout) {
        try {
            socket.connect(new InetSocketAddress(ip, port), timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 发送必须有响应
    public ChessMessage send(ChessMessage message) throws IOException {
        try {
            SocketTool.sendMessage(socket, message);
            ChessMessage response = SocketTool.receiveMessage(socket);
            return response;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            SocketTool.closeSocket(socket); // 确保最终关闭Socket
        }
    }
    // 发送必须没有响应
    public void sendOnly(ChessMessage message) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            output.writeObject(message);
            output.flush();
        } finally {
            SocketTool.closeSocket(socket);
        }
    }
}
