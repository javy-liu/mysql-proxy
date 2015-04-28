package org.oyach.mysql.proxy;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

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



        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "jmp.properties");
        InputStream inputStream = new FileInputStream(file);
        ProxyServer.config.load(inputStream);
        inputStream.close();



        String[] ports = ProxyServer.config.getProperty("ports").split(",");
        for (String port: ports) {
            new ProxyServer(Integer.parseInt(port.trim())).run();
        }

    }
}