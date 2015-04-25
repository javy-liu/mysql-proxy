package org.oyach.mysql.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * mysql 客户端连接
 *
 * @author oyach
 * @since 0.0.1
 */
public class ProxyServer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    /** 代理服务器端口 */
    private int port;

    private ServerSocket serverSocket;


    public ProxyServer(int port) {
        Thread.currentThread().setName("Listener: " + port);
        this.port = port;
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("该端口已经占用: " + port);
            System.exit(-1);
        }

        logger.info("监听端口: " + port);

        /** 启动一个线程池来处理接收到的请求 */
        ExecutorService executorService = Executors.newCachedThreadPool();


        while (true){

            try {
                executorService.submit(new Engine(3306, this.serverSocket.accept(), new Proxy()));
            } catch (Exception e) {

            }
        }
    }
}
