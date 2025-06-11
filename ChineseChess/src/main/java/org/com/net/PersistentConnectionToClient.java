package org.com.net;

import java.net.Socket;

/**
 * 服务端得到客户端的长连接
 * @author lanye
 * @date 2025/06/10
 */
public abstract class PersistentConnectionToClient extends PersistentConnection {
    public PersistentConnectionToClient(){}
    public PersistentConnectionToClient(Socket socket){
        super(socket);
    }
}
