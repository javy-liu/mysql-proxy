package com.mysql.proxy.protocol;

public class ClientFlags 
{
	
	//Use the improved version of Old Password Authentication, New in 3.21.x(x < 17), Deprecated in 4.1.1
    public static final int CLIENT_LONG_PASSWORD                = 0x0001;
    //Send found rows instead of affected rows in EOF_Packet
    public static final int CLIENT_FOUND_ROWS                   = 0x0002;
    //Longer flags in Protocol::ColumnDefinition320, New in 3.21.20, Deprecated in 4.1
    public static final int CLIENT_LONG_FLAG                    = 0x0004;
    //One can specify db on connect in Handshake Response Packet, 
    public static final int CLIENT_CONNECT_WITH_DB              = 0x0008;
    //Don't allow database.table.column
    public static final int CLIENT_NO_SCHEMA                    = 0x0010;
    //Compression protocol supported
    public static final int CLIENT_COMPRESS                     = 0x0020;
    public static final int CLIENT_ODBC                         = 0x0040;
    //Can use LOAD DATA LOCAL
    public static final int CLIENT_LOCAL_FILES                  = 0x0080;
    //parser can ignore spaces before '('
    public static final int CLIENT_IGNORE_SPACE                 = 0x0100;
    //supports the 4.1 protocol, this value was CLIENT_CHANGE_USER in 3.22, unused in 4.0
    public static final int CLIENT_PROTOCOL_41                  = 0x0200;
    //wait_timeout vs. wait_interactive_timeout
    public static final int CLIENT_INTERACTIVE                  = 0x0400;
    //supports SSL
    public static final int CLIENT_SSL                          = 0x0800;
    public static final int CLIENT_IGNORE_SIGPIPE               = 0x1000;
    public static final int CLIENT_TRANSACTIONS                 = 0x2000;
    //unused, Was named CLIENT_PROTOCOL_41 in 4.1.0, New in 4.1.0, Deprecated in 4.1.1
    public static final int CLIENT_RESERVED                     = 0x4000;
    public static final int CLIENT_SECURE_CONNECTION            = 0x8000;
    //was named CLIENT_MULTI_QUERIES in 4.1.0, renamed later
    public static final int CLIENT_MULTI_STATEMENTS             = 0x00010000;
    public static final int CLIENT_MULTI_RESULTS                = 0x00020000;
    public static final int CLIENT_PS_MULTI_RESULTS             = 0x00040000;
    public static final int CLIENT_PLUGIN_AUTH             		= 0x00080000;
    public static final int CLIENT_CONNECT_ATTRS				= 0x00100000;
    public static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA				= 0x00200000;
    public static final int CLIENT_SSL_VERIFY_SERVER_CERT       = 0x40000000;
    public static final int CLIENT_REMEMBER_OPTIONS             = 0x80000000;
    
    public static final int CLIENT_ALL_FLAGS = (CLIENT_LONG_PASSWORD | 
            CLIENT_FOUND_ROWS | 
            CLIENT_LONG_FLAG | 
            CLIENT_CONNECT_WITH_DB | 
            CLIENT_NO_SCHEMA | 
            CLIENT_COMPRESS | 
            CLIENT_ODBC | 
            CLIENT_LOCAL_FILES | 
            CLIENT_IGNORE_SPACE | 
            CLIENT_PROTOCOL_41 | 
            CLIENT_INTERACTIVE | 
            CLIENT_SSL | 
            CLIENT_IGNORE_SIGPIPE | 
            CLIENT_TRANSACTIONS | 
            CLIENT_RESERVED | 
            CLIENT_SECURE_CONNECTION | 
            CLIENT_MULTI_STATEMENTS | 
            CLIENT_MULTI_RESULTS | 
            CLIENT_SSL_VERIFY_SERVER_CERT | 
            CLIENT_REMEMBER_OPTIONS);
    public static final int CLIENT_BASIC_FLAGS  = (((CLIENT_ALL_FLAGS & ~CLIENT_SSL) & ~CLIENT_COMPRESS) & ~CLIENT_SSL_VERIFY_SERVER_CERT); 
}
