package org.oyach.mysql.proxy.plugin.example;

/*
 * Example plugin. Just log timing information and hook names
 */


import org.oyach.mysql.proxy.Engine;
import org.oyach.mysql.proxy.plugin.Base;

public class Example extends Base {

    public void init(Engine context) {
        logger.info("Plugin_Example->init");
    }

    public void read_handshake(Engine context) {
        logger.info("Plugin_Example->read_handshake");
    }

    public void read_auth(Engine context) {
        logger.info("Plugin_Example->read_auth");
    }

    public void read_auth_result(Engine context) {
        logger.info("Plugin_Example->read_auth_result");
    }

    public void read_query(Engine context) {
        logger.info("Plugin_Example->read_query");
    }

    public void read_query_result(Engine context) {
        logger.info("Plugin_Example->read_query_result");
    }

    public void send_query_result(Engine context) {
        logger.info("Plugin_Example->send_query_result");
    }

    public void cleanup(Engine context) {
        logger.info("Plugin_Example->cleanup");
    }

}
