package com.mysql.proxy.protocol;

import java.util.BitSet;

public enum ClientFlags2 
{
	
	//Use the improved version of Old Password Authentication, New in 3.21.x(x < 17), Deprecated in 4.1.1
    CLIENT_LONG_PASSWORD                (0x0001),
    //Send found rows instead of affected rows in EOF_Packet
    CLIENT_FOUND_ROWS                   (0x0002),
    //Longer flags in Protocol::ColumnDefinition320, New in 3.21.20, Deprecated in 4.1
    CLIENT_LONG_FLAG                    (0x0004),
    //One can specify db on connect in Handshake Response Packet, 
    CLIENT_CONNECT_WITH_DB              (0x0008),
    //Don't allow database.table.column
    CLIENT_NO_SCHEMA                    (0x0010),
    //Compression protocol supported
    CLIENT_COMPRESS                     (0x0020),
    CLIENT_ODBC                         (0x0040),
    //Can use LOAD DATA LOCAL
    CLIENT_LOCAL_FILES                  (0x0080),
    //parser can ignore spaces before '('
    CLIENT_IGNORE_SPACE                 (0x0100),
    //supports the 4.1 protocol, this value was CLIENT_CHANGE_USER in 3.22, unused in 4.0
    CLIENT_PROTOCOL_41                  (0x0200),
    //wait_timeout vs. wait_interactive_timeout
    CLIENT_INTERACTIVE                  (0x0400),
    //supports SSL
    CLIENT_SSL                          (0x0800),
    CLIENT_IGNORE_SIGPIPE               (0x1000),
    CLIENT_TRANSACTIONS                 (0x2000),
    //unused, Was named CLIENT_PROTOCOL_41 in 4.1.0, New in 4.1.0, Deprecated in 4.1.1
    CLIENT_RESERVED                     (0x4000),
    CLIENT_SECURE_CONNECTION            (0x8000),
    //was named CLIENT_MULTI_QUERIES in 4.1.0, renamed later
    CLIENT_MULTI_STATEMENTS             (0x00010000),
    CLIENT_MULTI_RESULTS                (0x00020000),
    CLIENT_PS_MULTI_RESULTS             (0x00040000),
    CLIENT_PLUGIN_AUTH             		(0x00080000),
    CLIENT_CONNECT_ATTRS				(0x00100000),
    CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA				(0x00200000),
    CLIENT_SSL_VERIFY_SERVER_CERT       (0x40000000),
    CLIENT_REMEMBER_OPTIONS             (0x80000000),
    
    CLIENT_ALL_FLAGS (CLIENT_LONG_PASSWORD , 
            CLIENT_FOUND_ROWS , 
            CLIENT_LONG_FLAG , 
            CLIENT_CONNECT_WITH_DB , 
            CLIENT_NO_SCHEMA , 
            CLIENT_COMPRESS , 
            CLIENT_ODBC , 
            CLIENT_LOCAL_FILES , 
            CLIENT_IGNORE_SPACE , 
            CLIENT_PROTOCOL_41 , 
            CLIENT_INTERACTIVE , 
            CLIENT_SSL , 
            CLIENT_IGNORE_SIGPIPE , 
            CLIENT_TRANSACTIONS , 
            CLIENT_RESERVED , 
            CLIENT_SECURE_CONNECTION , 
            CLIENT_MULTI_STATEMENTS , 
            CLIENT_MULTI_RESULTS , 
            CLIENT_SSL_VERIFY_SERVER_CERT , 
            CLIENT_REMEMBER_OPTIONS),
    CLIENT_BASIC_FLAGS  ((((CLIENT_ALL_FLAGS.toInt() & ~CLIENT_SSL.toInt()) & ~CLIENT_COMPRESS.toInt()) & ~CLIENT_SSL_VERIFY_SERVER_CERT.toInt()));
    
    private int value;
    private BitSet bitSet;
    
    private ClientFlags2(int value)
    {
    	this.value =  value;
    	this.bitSet = BitSet.valueOf(new long[] { value });
    }
    
    private ClientFlags2(ClientFlags2... value)
    {
    	for (ClientFlags2 flag : value)
		{
			this.bitSet.or(flag.toBitSet());
		}
    }
    
    public void addFlag(ClientFlags2 flag) 
	{
    	this.bitSet.or(flag.toBitSet());
    }
    
    public void removeFlag(ClientFlags2 flag) 
    {
    	BitSet clone = (BitSet) flag.toBitSet().clone();
    	clone.flip(0, clone.length());
    	this.bitSet.and(clone);
    }
    
    public void toggleFlag(ClientFlags2 flag) 
    {
        this.value ^= flag.toInt();
    }
    
    public boolean hasFlag(ClientFlags2 flag) 
    {
        return ((this.value & flag.toInt()) > 0);
    }
    
    public int toInt()
    {
    	return this.value;
    }
    
    public BitSet toBitSet()
    {
    	return this.bitSet;
    }
}
