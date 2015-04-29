package org.oyach.mysql.proxy;

import org.junit.Test;

/**
 * Created by oych on 15/4/30.
 *
 * @author oyach
 * @since 0.0.1
 */
public class StringTest {


    @Test
    public void testName() throws Exception {
        String sql = "select username from users ";
        byte[] bytes = sql.getBytes();
        System.out.println();
    }
}
