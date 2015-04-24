package com.mysql.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.github.jmpjct.mysql.proto.Auth_Challenge;
import com.github.jmpjct.mysql.proto.Flags;
import com.github.jmpjct.mysql.proto.Packet;
import com.github.jmpjct.mysql.proto.ResultSet;
import com.mysql.proxy.protocol.Handshake;

public class MySQLIoHandlerAdapter extends IoHandlerAdapter
{

	public Socket mysqlSocket = null;
    public InputStream mysqlIn = null;
    public OutputStream mysqlOut = null;
    
	public void sessionCreated(IoSession session) throws Exception
	{
		System.out.println(session);
	}

	public void sessionOpened(IoSession session) throws Exception
	{
		try
		{
		// Connect to the mysql server on the other side
        this.mysqlSocket = new Socket("127.0.0.1", 3306);
        this.mysqlSocket.setPerformancePreferences(0, 2, 1);
        this.mysqlSocket.setTcpNoDelay(true);
        this.mysqlSocket.setTrafficClass(0x10);
        this.mysqlSocket.setKeepAlive(true);
        
        //this.logger.info("Connected to mysql server at "+this.mysqlHost+":"+this.mysqlPort);
        this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
        this.mysqlOut = this.mysqlSocket.getOutputStream();

        byte[] packet = Packet.read_packet(this.mysqlIn);
        Auth_Challenge authChallenge = Auth_Challenge.loadFromPacket(packet);
        byte[] packet2 = authChallenge.toPacket();
        if(packet.equals(packet2))
        {
        	System.out.println("equal");
        }
        authChallenge.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        authChallenge.removeCapabilityFlag(Flags.CLIENT_SSL);
        authChallenge.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);
        
        // Set the default result set creation to the server's character set
        //ResultSet.characterSet = authChallenge.characterSet;
        
        // Set Replace the packet in the buffer
        //context.buffer.add(authChallenge.toPacket());
		//session.write(authChallenge.toPacket());
        Handshake handshake = new Handshake(session.getId());
        
        byte[] bytes = handshake.toPacket().getBytes();
		session.write(bytes);
		ArrayList<byte[]>  b =  new ArrayList<byte[]> ();
		b.add(authChallenge.toPacket());
		//Packet.write(session, b);
        System.out.println(authChallenge);
		}catch(Exception e)
		{
			System.err.println(e);
		}
	}

	public void sessionClosed(IoSession session) throws Exception
	{
		System.out.println(session);
	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception
	{
		System.out.println(status);
	}



	public void messageReceived(IoSession session, Object message)
			throws Exception
	{
		System.out.println("messageReceived "+message);

	}

	public void messageSent(IoSession session, Object message) throws Exception
	{
		System.out.println("messageSent "+message);
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception
	{
		System.out.println(cause);
		
	}

}
