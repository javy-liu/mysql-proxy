package com.mysql.proxy.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MySQLProtocolDecoder implements ProtocolDecoder
{

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		System.out.println("decode "+in.capacity());
		//String hexDump = in.getHexDump();
		in.position(0);
		in.limit(in.capacity());
		while(in.hasRemaining())
		{
			byte[] array = in.array();
			System.out.println(array);
			in.position(in.capacity());
		}
		out.write("디코드 메시지");
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		System.out.println("finish decode");
	}

	public void dispose(IoSession session) throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		System.out.println("dispose decode");
	}

}
