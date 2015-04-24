package com.mysql.proxy.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;

public class MySQLProtocolCodecFactory extends PrefixedStringCodecFactory 
{
	private ProtocolEncoder encoder = new MySQLProtocolEncoder();
	private ProtocolDecoder decoder= new MySQLProtocolDecoder();
	
	public ProtocolEncoder getEncoder(IoSession session) throws Exception 
	{
	    return encoder;
	}
	
	public ProtocolDecoder getDecoder(IoSession session) throws Exception 
	{
	    return decoder;
	}
}
