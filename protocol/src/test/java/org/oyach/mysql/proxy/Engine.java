package org.oyach.mysql.proxy;

import org.oyach.mysql.protocol.Flags;
import org.oyach.mysql.protocol.Handshake;
import org.oyach.mysql.protocol.HandshakeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 连接mysql服务器
 *
 * @author oyach
 * @since 0.0.1
 */
public class Engine implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Engine.class);

    /**
     * 连接端口
     */
    private int port;

    /**
     * 记录客户端连接
     */
    private Socket clientSocket;

    private InputStream clientIn;

    private OutputStream clientOut;

    /**
     * 记录过程步骤
     */
    private int mode = Flags.MODE_INIT;

    private int nextMode = Flags.MODE_INIT;

    private boolean running = true;

    /** 插件 */
    private Base plugin;

    public Handshake handshake = null;
    public HandshakeResponse authReply = null;

    // Packet Buffer. ArrayList so we can grow/shrink dynamically
    public ArrayList<byte[]> buffer = new ArrayList<byte[]>();
    public int offset = 0;


    public Engine(int port, Socket clientSocket, Base plugin) throws Exception {
        this.port = port;
        this.clientSocket = clientSocket;
        this.plugin = plugin;

        this.clientSocket.setTcpNoDelay(true); // 立即发送数据
        this.clientSocket.setTrafficClass(0x10); // 低延迟
        this.clientSocket.setKeepAlive(true); // 保持空闲连接

        this.clientIn = new BufferedInputStream(this.clientSocket.getInputStream(), 16384);
        this.clientOut = this.clientSocket.getOutputStream();
    }

    public void run() {

        try {
            while (this.running) {
                switch (this.mode) {
                    case Flags.MODE_INIT:
                        logger.trace("MODE_INIT");
                        this.nextMode = Flags.MODE_READ_HANDSHAKE;
                        init();
                        break;

                    case Flags.MODE_READ_HANDSHAKE:
                        logger.trace("MODE_READ_HANDSHAKE");
                        this.nextMode = Flags.MODE_SEND_HANDSHAKE;
                        readHandshake();
                        break;

                    case Flags.MODE_SEND_HANDSHAKE:
                        logger.trace("MODE_SEND_HANDSHAKE");
                        this.nextMode = Flags.MODE_READ_AUTH;
                        sendHandshake();
                        break;

                    case Flags.MODE_READ_AUTH:
                        logger.trace("MODE_READ_AUTH");
                        this.nextMode = Flags.MODE_SEND_AUTH;
                        readAuth();
                        break;

                    case Flags.MODE_SEND_AUTH:
                        logger.trace("MODE_SEND_AUTH");
                        this.nextMode = Flags.MODE_READ_AUTH_RESULT;
                        sendAuth();
                        break;

                    case Flags.MODE_READ_AUTH_RESULT:
                        logger.trace("MODE_READ_AUTH_RESULT");
                        this.nextMode = Flags.MODE_SEND_AUTH_RESULT;
                        readAuthResult();
                        break;

                    case Flags.MODE_SEND_AUTH_RESULT:
                        logger.trace("MODE_SEND_AUTH_RESULT");
                        this.nextMode = Flags.MODE_READ_QUERY;
                        sendAuthResult();
                        break;

                    case Flags.MODE_READ_QUERY:
                        logger.trace("MODE_READ_QUERY");
                        this.nextMode = Flags.MODE_SEND_QUERY;
                        readQuery();
                        break;

                    case Flags.MODE_SEND_QUERY:
                        logger.trace("MODE_SEND_QUERY");
                        this.nextMode = Flags.MODE_READ_QUERY_RESULT;
                        sendQuery();
                        break;

                    case Flags.MODE_READ_QUERY_RESULT:
                        logger.trace("MODE_READ_QUERY_RESULT");
                        this.nextMode = Flags.MODE_SEND_QUERY_RESULT;
                        readQueryResult();
                        break;

                    case Flags.MODE_SEND_QUERY_RESULT:
                        logger.trace("MODE_SEND_QUERY_RESULT");
                        this.nextMode = Flags.MODE_CLEANUP;
                        sendQueryResult();
                        break;

                    case Flags.MODE_CLEANUP:
                        logger.trace("MODE_CLEANUP");
                        this.nextMode = Flags.MODE_SEND_HANDSHAKE;
                        cleanUp();
                        break;

                    default:
                        logger.error("未知过程: " + this.mode);
                        this.halt();
                }
                this.mode = this.nextMode;
            }

            logger.info("线程退出");
            this.clientSocket.close();

        } catch (Exception e) {
            logger.debug(e.getMessage());
        } finally {
            try {
                this.clientSocket.close();
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
    }


    public void init() {
        try {
            plugin.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readHandshake() {
        try {
            plugin.readHandshake(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHandshake() {
        try {
            plugin.sendHandshake(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readAuth() {
        try {
            plugin.readAuth(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAuth() {
        try {
            plugin.sendAuth(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readAuthResult() {
        try {
            plugin.readAuthResult(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAuthResult() {
        try {
            plugin.sendAuthResult(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readQuery() {
        try {
            plugin.readQuery(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendQuery() {
        try {
            plugin.sendQuery(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readQueryResult() {
        try {
            plugin.readQueryResult(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendQueryResult() {
        try {
            plugin.sendQueryResult(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        try {
            plugin.cleanUp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void halt() {
        logger.trace("停止");
        this.running = false;
    }
}
