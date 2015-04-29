package org.oyach.mysql.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by oych on 15/4/29.
 *
 * @author oyach
 * @since 0.0.1
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println("========initChannel==========");
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("framer", new LengthFieldPrepender(4));
        pipeline.addLast("decoder", new ByteArrayDecoder());
//
        pipeline.addLast("encoder", new ByteArrayEncoder());
        pipeline.addLast("handler", new NettyServerHandler());
    }
}
