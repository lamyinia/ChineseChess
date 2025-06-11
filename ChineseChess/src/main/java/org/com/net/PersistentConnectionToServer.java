package org.com.net;

import org.com.protocal.ChessMessage;
import org.com.tools.GameRoomTool;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端建立到服务端的长连接
 * @author lanye
 * @date 2025/06/10
 */
public abstract class PersistentConnectionToServer extends PersistentConnection {
    protected ScheduledExecutorService heartBeatScheduler;

    public PersistentConnectionToServer(){}
    public PersistentConnectionToServer(Socket socket){
        super(socket);
    }
    protected void startHeartbeat(){
        heartBeatScheduler = Executors.newSingleThreadScheduledExecutor();  // 是否会堆积请求
        heartBeatScheduler.scheduleAtFixedRate(() -> {
            send(new ChessMessage(null, ChessMessage.Type.HEARTBEAT, null, null));
        }, 0, GameRoomTool.DEFAULT_HEARTBEAT_PERIOD, TimeUnit.MILLISECONDS);
        connectionlogger.info("开始向服务器发送心跳");
    }
}
