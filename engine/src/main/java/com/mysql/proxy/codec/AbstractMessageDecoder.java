package com.mysql.proxy.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class AbstractMessageDecoder implements MessageDecoder
{

	public MessageDecoderResult decodable(IoSession session, IoBuffer in)
	{

		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception
	{
		// TODO 자동 생성된 메소드 스텁
		
	}

}
