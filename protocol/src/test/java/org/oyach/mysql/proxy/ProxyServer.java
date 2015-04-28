package org.oyach.mysql.proxy;

import org.oyach.mysql.proxy.plugin.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * mysql 客户端连接
 *
 * @author oyach
 * @since 0.0.1
 */
public class ProxyServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    public int port;
    public boolean listening = true;
    public ServerSocket listener = null;
    public ArrayList<Base> plugins = new ArrayList<Base>();

    public static Properties config = new Properties();
    public ProxyServer(int port) {
        Thread.currentThread().setName("Listener: "+port);
        this.port = port;
    }

    public void run() {
        try {
            this.listener = new ServerSocket(this.port);
        }
        catch (IOException e) {
            logger.error("Could not listen on port " + this.port);
            System.exit(-1);
        }

        logger.info("Listening on "+this.port);

        String[] ps = new String[0];
        ExecutorService tp = Executors.newCachedThreadPool();

        if (config.getProperty("plugins") != null)
            ps = config.getProperty("plugins").split(",");

        while (this.listening) {
            plugins = new ArrayList<Base>();
            for (String p: ps) {
                try {
                    plugins.add((Base) Base.class.getClassLoader().loadClass(p.trim()).newInstance());
                    logger.info("Loaded plugin "+p);
                }
                catch (ClassNotFoundException e) {
                    logger.error("["+p+"] "+e);
                    continue;
                }
                catch (InstantiationException e) {
                    logger.error("["+p+"] "+e);
                    continue;
                }
                catch (IllegalAccessException e) {
                    logger.error("["+p+"] "+e);
                    continue;
                }
            }
            try {
                tp.submit(new Engine(this.port, this.listener.accept(), plugins));
            }
            catch (IOException e) {
                logger.error("Accept fatal " + e);
                this.listening = false;
            }
        }

        try {
            tp.shutdown();
            this.listener.close();
        }
        catch (IOException e) {}
    }
}
