package com.github.jmpjct;

/*
 * Java Mysql Proxy
 * Main binary. Just listen for connections and pass them over
 * to the proxy module
 */

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import org.apache.log4j.Logger;
import com.github.jmpjct.plugin.Base;
import com.github.jmpjct.JMP;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.github.jmpjct.plugin.*;

public class JMP_Thread implements Runnable {
    public int port;
    public boolean listening = true;
    public ServerSocket listener = null;
    public ArrayList<Base> plugins = new ArrayList<Base>();
    public Logger logger = Logger.getLogger("JMP_Thread");
    
    public JMP_Thread(int port) {
        Thread.currentThread().setName("Listener: "+port);
        this.port = port;
    }
    
    public void run() {
        try {
            this.listener = new ServerSocket(this.port);
        }
        catch (IOException e) {
            this.logger.fatal("Could not listen on port "+this.port);
            System.exit(-1);
        }
        
        this.logger.info("Listening on "+this.port);
        
        String[] ps = new String[0];
        ExecutorService tp = Executors.newCachedThreadPool();
        
        if (JMP.config.getProperty("plugins") != null)
            ps = JMP.config.getProperty("plugins").split(",");
        
        while (this.listening) {
            plugins = new ArrayList<Base>();
            for (String p: ps) {
                try {
                    plugins.add((Base) Base.class.getClassLoader().loadClass(p.trim()).newInstance());
                    this.logger.info("Loaded plugin "+p);
                }
                catch (java.lang.ClassNotFoundException e) {
                    this.logger.error("["+p+"] "+e);
                    continue;
                }
                catch (java.lang.InstantiationException e) {
                    this.logger.error("["+p+"] "+e);
                    continue;
                }
                catch (java.lang.IllegalAccessException e) {
                    this.logger.error("["+p+"] "+e);
                    continue;
                }
            }
            try {
                tp.submit(new Engine(this.port, this.listener.accept(), plugins));
            }
            catch (java.io.IOException e) {
                this.logger.fatal("Accept fatal "+e);
                this.listening = false;
            }
        }
    
        try {
            tp.shutdown();
            this.listener.close();
        }
        catch (java.io.IOException e) {}
    }
}
