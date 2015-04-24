package com.mysql.proxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.mysql.proxy.codec.MySQLProtocolCodecFactory;

public class Engine
{

	private int listenPort;
	private NioSocketAcceptor acceptor;

	
    
	/**
	 * Instantiates a new engine.
	 *
	 * @param listenPort the listen port
	 * @param plugins the plugins
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Engine(int listenPort, String plugins) throws IOException
	{
		this.listenPort = listenPort;
		acceptor = new NioSocketAcceptor();
		//acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec", new ProtocolCodecFilter(new MySQLProtocolCodecFactory()));
		
		// Create TCP/IP connector.
//		IoConnector connector = new NioSocketConnector();
//		// Set connect timeout.
//		connector.setConnectTimeoutMillis(30*1000L);
//		String ip = "127.0.0.1";
//		int port =  3306;
//		ClientToProxyIoHandler handler = new ClientToProxyIoHandler(connector, new InetSocketAddress(ip, port));
//		acceptor.setHandler(handler);
		
		acceptor.setHandler(new MySQLIoHandlerAdapter());

		acceptor.bind(new InetSocketAddress(listenPort));
		System.out.println("Listening on port " + listenPort);
		for (;;) 
		{
          System.out.println("R: " + acceptor.getStatistics().getReadBytesThroughput() +
        		  ", W: " + acceptor.getStatistics().getWrittenBytesThroughput());
	        try
			{
				Thread.sleep(3000);
			} catch (InterruptedException e)
			{
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
      }
	}
}
