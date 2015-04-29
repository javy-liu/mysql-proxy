package org.oyach.mysql.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.oyach.mysql.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by oych on 15/4/29.
 *
 * @author oyach
 * @since 0.0.1
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    public Socket mysqlSocket = null;
    public InputStream mysqlIn = null;
    public OutputStream mysqlOut = null;

    public Handshake handshake = null;
    public HandshakeResponse authReply = null;
    ArrayList<byte[]> buffer = new ArrayList<byte[]>();

    public NettyServerHandler() {
        try {
            this.mysqlSocket = new Socket("127.0.0.1", 3306);
            this.mysqlSocket.setPerformancePreferences(0, 2, 1);
            this.mysqlSocket.setTcpNoDelay(true);
            this.mysqlSocket.setTrafficClass(0x10);
            this.mysqlSocket.setKeepAlive(true);

            this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
            this.mysqlOut = this.mysqlSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        System.out.println("========channelRead0===========");


    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("------------channelActive-------------");

        byte[] packet = Packet.read_packet(this.mysqlIn);
        this.handshake = Handshake.loadFromPacket(packet);

        // Remove some flags from the reply
        this.handshake.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        this.handshake.removeCapabilityFlag(Flags.CLIENT_SSL);
        this.handshake.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);

        // Set the default result set creation to the server's character set
        ResultSet.characterSet = this.handshake.characterSet;

        // Set Replace the packet in the buffer
//        ArrayList<byte[]> buffer = new ArrayList<byte[]>();
//        ByteBuf byteBuf = new EmptyByteBuf(buffer.size());
        buffer.add(this.handshake.toPacket());

        for (byte[] bytes : buffer){
            ctx.writeAndFlush(buffer);
//            ctx.writeAndFlush(buffer);
        }


        buffer.clear();

//        super.channelActive(ctx);
    }

    public void read_auth() throws IOException {
        logger.trace("read_auth");
//        byte[] packet = Packet.read_packet(this.clientIn);
//        this.buffer.add(packet);

//        context.authReply = HandshakeResponse.loadFromPacket(packet);

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
    }
}
