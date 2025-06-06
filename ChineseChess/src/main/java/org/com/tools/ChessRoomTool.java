package org.com.tools;

import org.com.protocal.ChessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChessRoomTool {
    private static final Logger logger = LoggerFactory.getLogger(ChessRoomTool.class);

    public static String showErrorBox(String message) {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
        return message;
    }

    public static void sendMessage(Socket socket, ChessMessage message) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(message);
        output.flush();
    }
    public static ChessMessage receiveMessage(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        return (ChessMessage) input.readObject();
    }
    public static void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.warn("关闭Socket时发生异常", e);
            }
        }
    }

    public static Socket create(String ip, int port) throws IOException {
        return new Socket(InetAddress.getByName(ip), port);
    }
}
