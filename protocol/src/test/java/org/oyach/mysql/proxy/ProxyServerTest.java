package org.oyach.mysql.proxy;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试代理服务器
 *
 * @author oyach
 * @since 0.0.1
 */
public class ProxyServerTest {
    private static int port = 5050;



    @Test
    public void test01() throws Exception {

        new ProxyServer(port).run();


    }
}