package com.github.jmpjct.plugin;

/*
 * Create an empty abstract class to allow plugins to only
 * implement their required differences.
 */
import org.apache.log4j.Logger;

import com.mysql.proxy.plugin.Context;

import java.io.IOException;


public abstract class Base {
    public Logger logger = Logger.getLogger("Plugin.Base");
    
    public void init(Context context) throws IOException {}
    
    public void read_handshake(Context context) throws IOException {}
    public void send_handshake(Context context) throws IOException {}
    
    public void read_auth(Context context) throws IOException {}
    public void send_auth(Context context) throws IOException {}
    
    public void read_auth_result(Context context) throws IOException {}
    public void send_auth_result(Context context) throws IOException {}
    
    public void read_query(Context context) throws IOException {}
    public void send_query(Context context) throws IOException {}
    
    public void read_query_result(Context context) throws IOException {}
    public void send_query_result(Context context) throws IOException {}
    
    public void cleanup(Context context) throws IOException {}
}
