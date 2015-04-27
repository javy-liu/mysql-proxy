package org.oyach.mysql.proxy;

import org.oyach.mysql.protocol.Flags;
import org.oyach.mysql.protocol.Handshake;
import org.oyach.mysql.protocol.Packet;
import org.oyach.mysql.protocol.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by oych on 15/4/26.
 *
 * @author oyach
 * @since 0.0.1
 */
public class Proxy implements Base {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);

    private int mysqlPort = 3306;
    private String mysqlHost = "127.0.0.1";


    public Socket mysqlSocket;
    public InputStream mysqlIn;
    public OutputStream mysqlOut;

    public void init(Engine context) throws Exception {
        logger.trace("init");

        // 初始化
        this.mysqlSocket = new Socket(this.mysqlHost, this.mysqlPort);
        //设置性能参数，可设置任意整数，数值越大，相应的参数重要性越高（连接时间，延迟，带宽）
        this.mysqlSocket.setPerformancePreferences(0, 2, 1);
        this.mysqlSocket.setTcpNoDelay(true);
        this.mysqlSocket.setTrafficClass(0x10);
        this.mysqlSocket.setKeepAlive(true);

        logger.info("Connected to mysql server at {}:{}", this.mysqlHost, this.mysqlPort);
        this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
        this.mysqlOut = this.mysqlSocket.getOutputStream();

    }

    public void readHandshake(Engine context) throws Exception {
        logger.trace("readHandshake");
        byte[] packet = Packet.read_packet(this.mysqlIn);

        context.handshake = Handshake.loadFromPacket(packet);

        // Remove some flags from the reply
        context.handshake.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        context.handshake.removeCapabilityFlag(Flags.CLIENT_SSL);
        context.handshake.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);

        // Set the default result set creation to the server's character set
        ResultSet.characterSet = context.handshake.characterSet;

        // Set Replace the packet in the buffer
        context.buffer.add(context.handshake.toPacket());
    }

    public void sendHandshake(Engine context) throws Exception {
        this.logger.trace("send_handshake");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }

    public void readAuth(Engine context) throws Exception {

    }

    public void sendAuth(Engine context) throws Exception {

    }

    public void readAuthResult(Engine context) throws Exception {

    }

    public void sendAuthResult(Engine context) throws Exception {

    }

    public void readQuery(Engine context) throws Exception {

    }

    public void sendQuery(Engine context) throws Exception {

    }

    public void readQueryResult(Engine context) throws Exception {

    }

    public void sendQueryResult(Engine context) throws Exception {

    }

    public void cleanUp(Engine context) throws Exception {

    }
}
