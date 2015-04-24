package com.mysql.proxy.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


public class MySQLProtocolEncoder implements  ProtocolEncoder 
{

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		System.out.println("encode "+message);
		 IoBuffer buf = IoBuffer.allocate(16);
		           buf.setAutoExpand(true); // Enable auto-expand for easier encoding
		   
		           // Encode a header
		           buf.put((byte[])message);
		           buf.flip();
		           out.write(buf);
		//out.write(message);
	}

	public void dispose(IoSession session) throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		System.out.println("dispose encode");
	}
}
