package com.mysql.proxy.plugin;

/*
 * Create an empty abstract class to allow plugins to only
 * implement their required differences.
 */
//import org.apache.log4j.Logger;
import java.io.IOException;

import com.github.jmpjct.mysql.proto.Packet;

public abstract class Base implements AutoCloseable
{
    //public Logger logger = Logger.getLogger("Plugin.Base");
	//Engine context;
	public Base()
	{
	}
	
    public void init() throws IOException {}
    
    public void readHandshake(Packet packet) throws IOException {}
    public void sendHandshake(Packet packet) throws IOException {}
    
    public void readAuth(Packet packet) throws IOException {}
    public void sendAuth(Packet packet) throws IOException {}
    
    public void readAuthResult(Packet packet) throws IOException {}
    public void sendAuthResult(Packet packet) throws IOException {}
    
    public void readQuery(Packet packet) throws IOException {}
    public void sendQuery(Packet packet) throws IOException {}
    
    public void readQueryResult(Packet packet) throws IOException {}
    public void sendQueryResult(Packet packet) throws IOException {}
    
    public void close() throws IOException {};
    
    //public void cleanup() throws IOException {}
}
