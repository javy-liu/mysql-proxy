package org.oyach.mysql.proxy.plugin;

/*
 * Create an empty abstract class to allow plugins to only
 * implement their required differences.
 */

import org.oyach.mysql.proxy.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class Base {
    protected static final Logger logger = LoggerFactory.getLogger(Base.class);

    public void init(Engine context) throws IOException {
    }

    public void read_handshake(Engine context) throws IOException {
    }

    public void send_handshake(Engine context) throws IOException {
    }

    public void read_auth(Engine context) throws IOException {
    }

    public void send_auth(Engine context) throws IOException {
    }

    public void read_auth_result(Engine context) throws IOException {
    }

    public void send_auth_result(Engine context) throws IOException {
    }

    public void read_query(Engine context) throws IOException {
    }

    public void send_query(Engine context) throws IOException {
    }

    public void read_query_result(Engine context) throws IOException {
    }

    public void send_query_result(Engine context) throws IOException {
    }

    public void cleanup(Engine context) throws IOException {
    }
}
