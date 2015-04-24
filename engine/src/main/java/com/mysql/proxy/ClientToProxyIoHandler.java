package com.mysql.proxy;

import java.net.SocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.proxy.AbstractProxyIoHandler;

/**
 * Handles the client to proxy part of the proxied connection.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ClientToProxyIoHandler extends AbstractProxyIoHandler {
    private final ServerToProxyIoHandler connectorHandler = new ServerToProxyIoHandler();
    
    private final IoConnector connector;
    
    private final SocketAddress remoteAddress;

    private static final Charset CHARSET = Charset.forName("iso8859-1");
    public static final String OTHER_IO_SESSION = AbstractProxyIoHandler.class.getName()+".OtherIoSession";
    
    public ClientToProxyIoHandler(IoConnector connector, SocketAddress remoteAddress) 
    {
        this.connector = connector;
        this.remoteAddress = remoteAddress;
        connector.setHandler(connectorHandler);
    }

	@Override
	public void proxySessionOpened(final IoSession session) throws Exception
	{
		connector.connect(remoteAddress).addListener(new IoFutureListener<ConnectFuture>() {
            public void operationComplete(ConnectFuture future) {
                try {
                    future.getSession().setAttribute(OTHER_IO_SESSION, session);
                    session.setAttribute(OTHER_IO_SESSION, future.getSession());
                    IoSession session2 = future.getSession();
                    session2.resumeRead();
                    session2.resumeWrite();
                } catch (RuntimeIoException e) {
                    // Connect failed
                    session.close(true);
                } finally {
                    session.resumeRead();
                    session.resumeWrite();
                }
            }
        });
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception 
	{
	    if (session.getAttribute( OTHER_IO_SESSION ) != null) {
	        IoSession sess = (IoSession) session.getAttribute(OTHER_IO_SESSION);
	        sess.setAttribute(OTHER_IO_SESSION, null);
	        sess.close(false);
	        session.setAttribute(OTHER_IO_SESSION, null);
	    }
	}
}
