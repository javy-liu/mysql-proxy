package org.oyach.mysql.proxy.plugin.proxy;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;

import org.oyach.mysql.protocol.*;
import org.oyach.mysql.proxy.Engine;
import org.oyach.mysql.proxy.ProxyServer;
import org.oyach.mysql.proxy.plugin.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Proxy extends Base {
    private static final Logger logger = LoggerFactory.getLogger(Proxy.class);


    // MySql server stuff
    public String mysqlHost = "";
    public int mysqlPort = 0;
    public Socket mysqlSocket = null;
    public InputStream mysqlIn = null;
    public OutputStream mysqlOut = null;
    
    public void init(Engine context) throws IOException, UnknownHostException {
        logger.trace("init");
        
        String[] phs = ProxyServer.config.getProperty("proxyHosts").split(",");
        for (String ph: phs) {
            String[] hi = ph.split(":");
            if (context.port == Integer.parseInt(hi[0].trim())) {
                this.mysqlHost = hi[1].trim();
                this.mysqlPort = Integer.parseInt(hi[2].trim());
                break;
            }
        }
        
        // Connect to the mysql server on the other side
        this.mysqlSocket = new Socket(this.mysqlHost, this.mysqlPort);
        this.mysqlSocket.setPerformancePreferences(0, 2, 1);
        this.mysqlSocket.setTcpNoDelay(true);
        this.mysqlSocket.setTrafficClass(0x10);
        this.mysqlSocket.setKeepAlive(true);
        
        logger.info("Connected to mysql server at " + this.mysqlHost + ":" + this.mysqlPort);
        this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
        this.mysqlOut = this.mysqlSocket.getOutputStream();
    }
    
    public void read_handshake(Engine context) throws IOException {
        logger.trace("read_handshake");
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
    
    public void send_handshake(Engine context) throws IOException {
        logger.trace("send_handshake");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }
    
    public void read_auth(Engine context) throws IOException {
        logger.trace("read_auth");
        byte[] packet = Packet.read_packet(context.clientIn);
        context.buffer.add(packet);
        
        context.authReply = HandshakeResponse.loadFromPacket(packet);
        
        if (!context.authReply.hasCapabilityFlag(Flags.CLIENT_PROTOCOL_41)) {
            logger.error("We do not support Protocols under 4.1");
            context.halt();
            return;
        }
        
        context.authReply.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        context.authReply.removeCapabilityFlag(Flags.CLIENT_SSL);
        context.authReply.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);
        
        context.schema = context.authReply.schema;
    }
    
    public void send_auth(Engine context) throws IOException {
        logger.trace("send_auth");
        Packet.write(this.mysqlOut, context.buffer);
        context.clear_buffer();
    }
    
    public void read_auth_result(Engine context) throws IOException {
        logger.trace("read_auth_result");
        byte[] packet = Packet.read_packet(this.mysqlIn);
        context.buffer.add(packet);
        if (Packet.getType(packet) != Flags.OK) {
            logger.error("Auth is not okay!");
        }
    }
    
    public void send_auth_result(Engine context) throws IOException {
        logger.trace("read_auth_result");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }
    
    public void read_query(Engine context) throws IOException {
        logger.trace("read_query");
        context.bufferResultSet = false;
        
        byte[] packet = Packet.read_packet(context.clientIn);
        context.buffer.add(packet);
        
        context.sequenceId = Packet.getSequenceId(packet);
        logger.trace("Client sequenceId: "+context.sequenceId);

        // 3 表示大小  1 表示类型 sql
        // 1 退出  2初始化数据库 3查询
        switch (Packet.getType(packet)) {
            case Flags.COM_QUIT:
                logger.trace("COM_QUIT");
                context.halt();
                break;
            
            // Extract out the new default schema
            case Flags.COM_INIT_DB:
                logger.trace("COM_INIT_DB");
                context.schema = Com_Initdb.loadFromPacket(packet).schema;
                break;
            
            // Query
            case Flags.COM_QUERY:
                logger.trace("COM_QUERY");
                context.query = Com_Query.loadFromPacket(packet).query;
                logger.debug("query ====> {}", context.query);
//                if (context.query.contains("select")){
//                    context.buffer.set(context.buffer.size() - 1, new byte[]{27, 0, 0, 0, 3,
//                            115 ,
//                            101 ,
//                            108 ,
//                            101 ,
//                            99 ,
//                            116 ,
//                            32 ,
//                            117 ,
//                            115 ,
//                            101 ,
//                            114 ,
//                            110 ,
//                            97 ,
//                            109 ,
//                            101 ,
//                            32 ,
//                            102 ,
//                            114 ,
//                            111 ,
//                            109 ,
//                            32 ,
//                            117 ,
//                            115 ,
//                            101 ,
//                            114 ,
//                            115 });
//                }


                break;
            case Flags.COM_FIELD_LIST:
                logger.trace("COM_FIELD_LIST");
                context.query = Com_Query.loadFromPacket(packet).query;
                logger.debug("query ====> {}", context.query);
                break;
            default:
                break;
        }
    }
    
    public void send_query(Engine context) throws IOException {
        logger.trace("send_query");
        Packet.write(this.mysqlOut, context.buffer);
        context.clear_buffer();
    }
    
    public void read_query_result(Engine context) throws IOException {
        logger.trace("read_query_result");
        
        byte[] packet = Packet.read_packet(this.mysqlIn);
        context.buffer.add(packet);
        
        context.sequenceId = Packet.getSequenceId(packet);
        
        switch (Packet.getType(packet)) {
            case Flags.OK:
            case Flags.ERR:
                break;
            
            default:
                context.buffer = Packet.read_full_result_set(this.mysqlIn, context.clientOut, context.buffer, context.bufferResultSet);
                break;
        }
    }
    
    public void send_query_result(Engine context) throws IOException {
        logger.trace("send_query_result");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }
    
    public void cleanup(Engine context) {
        logger.trace("cleanup");
        if (this.mysqlSocket == null) {
            return;
        }
        
        try {
            this.mysqlSocket.close();
        }
        catch(IOException e) {}
    }
}
