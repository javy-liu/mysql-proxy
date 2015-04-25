package org.oyach.mysql.proxy.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by oych on 15/4/25.
 *
 * @author oyach
 * @since 0.0.1
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        logger.info("------------------{},{},{},{}", 1,2,3,4);

        logger.warn("------------------{},{},{},{}", 11,22,33,44);
        logger.trace("------------------{},{},{},{}", 111,222,333,444);
        logger.debug("------------------{},{},{},{}", 1111,2222,3333,4444);
        logger.info("------------------{},{},{},{}", 11111,22222,33333,44444);
        logger.error("11111");
    }
}
