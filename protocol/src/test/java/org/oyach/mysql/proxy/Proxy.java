//package org.oyach.mysql.proxy;
//
//import org.oyach.mysql.protocol.*;
//import org.oyach.mysql.proxy.plugin.Base;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//
///**
// * Created by oych on 15/4/26.
// *
// * @author oyach
// * @since 0.0.1
// */
//public class Proxy implements Base {
//    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);
//
//    private int mysqlPort = 3306;
//    private String mysqlHost = "127.0.0.1";
//
//
//    public Socket mysqlSocket;
//    public InputStream mysqlIn;
//    public OutputStream mysqlOut;
//
//    public void init(Engine context) throws Exception {
//        logger.trace("init");
//
//        // 初始化
//        this.mysqlSocket = new Socket(this.mysqlHost, this.mysqlPort);
//        //设置性能参数，可设置任意整数，数值越大，相应的参数重要性越高（连接时间，延迟，带宽）
//        this.mysqlSocket.setPerformancePreferences(0, 2, 1);
//        this.mysqlSocket.setTcpNoDelay(true);
//        this.mysqlSocket.setTrafficClass(0x10);
//        this.mysqlSocket.setKeepAlive(true);
//
//        logger.info("Connected to mysql server at {}:{}", this.mysqlHost, this.mysqlPort);
//        this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
//        this.mysqlOut = this.mysqlSocket.getOutputStream();
//
//    }
//
//    public void readHandshake(Engine context) throws Exception {
//        logger.trace("readHandshake");
//        byte[] packet = Packet.read_packet(this.mysqlIn);
//
//        context.handshake = Handshake.loadFromPacket(packet);
//
//        // Remove some flags from the reply
//        context.handshake.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
//        context.handshake.removeCapabilityFlag(Flags.CLIENT_SSL);
//        context.handshake.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);
//
//        // Set the default result set creation to the server's character set
//        ResultSet.characterSet = context.handshake.characterSet;
//
//        // Set Replace the packet in the buffer
//        context.buffer.add(context.handshake.toPacket());
//    }
//
//    public void sendHandshake(Engine context) throws Exception {
//        logger.trace("send_handshake");
//        Packet.write(context.clientOut, context.buffer);
//        context.clear_buffer();
//    }
//
//    public void readAuth(Engine context) throws Exception {
//        logger.trace("read_auth");
//        byte[] packet = Packet.read_packet(context.clientIn);
//        context.buffer.add(packet);
//
//        context.authReply = HandshakeResponse.loadFromPacket(packet);
//
//        if (!context.authReply.hasCapabilityFlag(Flags.CLIENT_PROTOCOL_41)) {
//            logger.error("We do not support Protocols under 4.1");
//            context.halt();
//            return;
//        }
//
//        context.authReply.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
//        context.authReply.removeCapabilityFlag(Flags.CLIENT_SSL);
//        context.authReply.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);
//
//        context.schema = context.authReply.schema;
//    }
//
//    public void sendAuth(Engine context) throws Exception {
//        logger.trace("send_auth");
//        Packet.write(this.mysqlOut, context.buffer);
//        context.clear_buffer();
//    }
//
//    public void readAuthResult(Engine context) throws Exception {
//        logger.trace("read_auth_result");
//        byte[] packet = Packet.read_packet(this.mysqlIn);
//        context.buffer.add(packet);
//        if (Packet.getType(packet) != Flags.OK) {
//            logger.error("Auth is not okay!");
//        }
//    }
//
//    public void sendAuthResult(Engine context) throws Exception {
//        logger.trace("read_auth_result");
//        Packet.write(context.clientOut, context.buffer);
//        context.clear_buffer();
//    }
//
//    public void readQuery(Engine context) throws Exception {
//        logger.trace("read_query");
//        context.bufferResultSet = false;
//
//        byte[] packet = Packet.read_packet(context.clientIn);
//        context.buffer.add(packet);
//
//        context.sequenceId = Packet.getSequenceId(packet);
//        logger.trace("Client sequenceId: {}", context.sequenceId);
//
//        switch (Packet.getType(packet)) {
//            case Flags.COM_QUIT:
//                logger.trace("COM_QUIT");
//                context.halt();
//                break;
//
//            // Extract out the new default schema
//            case Flags.COM_INIT_DB:
//                logger.trace("COM_INIT_DB");
//                context.schema = Com_Initdb.loadFromPacket(packet).schema;
//                break;
//
//            // Query
//            case Flags.COM_QUERY:
//                logger.trace("COM_QUERY");
//                context.query = Com_Query.loadFromPacket(packet).query;
//                logger.debug("query ===> {}", context.query);
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    public void sendQuery(Engine context) throws Exception {
//        logger.trace("send_query");
//        Packet.write(this.mysqlOut, context.buffer);
//        context.clear_buffer();
//    }
//
//    public void readQueryResult(Engine context) throws Exception {
//        logger.trace("read_query_result");
//
//        byte[] packet = Packet.read_packet(this.mysqlIn);
//        context.buffer.add(packet);
//
//        context.sequenceId = Packet.getSequenceId(packet);
//
//        switch (Packet.getType(packet)) {
//            case Flags.OK:
//            case Flags.ERR:
//                break;
//
//            default:
//                context.buffer = Packet.read_full_result_set(this.mysqlIn, context.clientOut, context.buffer, context.bufferResultSet);
//                break;
//        }
//    }
//
//    public void sendQueryResult(Engine context) throws Exception {
//        logger.trace("send_query_result");
//        Packet.write(context.clientOut, context.buffer);
//        context.clear_buffer();
//    }
//
//    public void cleanUp(Engine context) throws Exception {
//        logger.trace("cleanup");
//        if (this.mysqlSocket == null) {
//            return;
//        }
//
//        try {
//            this.mysqlSocket.close();
//        }
//        catch(IOException e) {}
//    }
//}
