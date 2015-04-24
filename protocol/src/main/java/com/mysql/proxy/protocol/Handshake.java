package com.mysql.proxy.protocol;

import java.util.Random;

import com.github.jmpjct.mysql.proto.Flags;

//http://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake
//http://eager2rdbms.blogspot.kr/2008/12/mysql-2.html
/**
 * The Class Handshake.
 *  - Mysql-5.5 /sql/sql_acl.cc:7956 send_server_handshake_packet 
 */
public class Handshake extends Protocol
{
	private byte protocolVersion = 0x0a;
	private String serverVersion = "5.6.14";
	private long connectionId = 0;
    //auth_plugin_data_part_1 
	private String authPluginDataPart1 = "";
	
	/** 
	 * https://kenai.com/nonav/projects/mysql-jvm/sources/source/content/include/mysql_com.h?rev=25
	 * #define CLIENT_ALL_FLAGS  (CLIENT_LONG_PASSWORD | \
                           CLIENT_FOUND_ROWS | \
                           CLIENT_LONG_FLAG | \
                           CLIENT_CONNECT_WITH_DB | \
                           CLIENT_NO_SCHEMA | \
                           CLIENT_COMPRESS | \
                           CLIENT_ODBC | \
                           CLIENT_LOCAL_FILES | \
                           CLIENT_IGNORE_SPACE | \
                           CLIENT_PROTOCOL_41 | \
                           CLIENT_INTERACTIVE | \
                           CLIENT_SSL | \
                           CLIENT_IGNORE_SIGPIPE | \
                           CLIENT_TRANSACTIONS | \
                           CLIENT_RESERVED | \
                           CLIENT_SECURE_CONNECTION | \
                           CLIENT_MULTI_STATEMENTS | \
                           CLIENT_MULTI_RESULTS | \
                           CLIENT_SSL_VERIFY_SERVER_CERT | \
                           CLIENT_REMEMBER_OPTIONS)


	  Switch off the flags that are optional and depending on build flags
	  If any of the optional flags is supported by the build it will be switched
	  on before sending to the client during the connection handshake.

		#define CLIENT_BASIC_FLAGS (((CLIENT_ALL_FLAGS & ~CLIENT_SSL) \
                                               & ~CLIENT_COMPRESS) \
                                               & ~CLIENT_SSL_VERIFY_SERVER_CERT)
	 */
    //test result 1111011111111111 (63487)
	private long capabilityFlags1 = ClientFlags.CLIENT_BASIC_FLAGS;
    //http://dev.mysql.com/doc/refman/5.1/en/charset.html
    //http://dev.mysql.com/doc/internals/en/character-set.html#packet-Protocol::CharacterSet
	//33 utf8_general_ci
	private long characterSet = 33;
    //in test SERVER_STATUS_AUTOCOMMIT =2
	private long statusFlags = 2;
	// int2store(end + 5, mpvio->client_capabilities >> 16);
	private long capabilityFlags2 = this.capabilityFlags1 >> 16;
	private String authPluginDataPart2 = "";
	private String authPluginName = "mysql_native_password";
	
	private Random rand = new Random();
	
	public Handshake(Packet packet)
	{
		super(packet);
	}
	
	public Handshake(long connectionId)
	{
		super(new Packet(80));
		this.connectionId = connectionId;
	}
	
	public void setCapabilityFlag(long flag) 
	{
        this.capabilityFlags1 |= flag;
    }
    
    public void removeCapabilityFlag(long flag) 
    {
        this.capabilityFlags1 &= ~flag;
    }
    
    public void toggleCapabilityFlag(long flag) 
    {
        this.capabilityFlags1 ^= flag;
    }
    
    public boolean hasCapabilityFlag(long flag) 
    {
        return ((this.capabilityFlags1 & flag) == flag);
    }
    
    public void setStatusFlag(long flag) 
    {
        this.statusFlags |= flag;
    }
    
    public void removeStatusFlag(long flag) 
    {
        this.statusFlags &= ~flag;
    }
    
    public void toggleStatusFlag(long flag) 
    {
        this.statusFlags ^= flag;
    }
    
    public long getStatusFlag()
    {
    	return this.statusFlags;
    }
    
    public boolean hasStatusFlag(long flag) 
    {
        return ((this.statusFlags & flag) == flag);
    }
	
    public long getCharacterSet()
    {
    	return characterSet;
    }
    
    private String randomString(int length) 
    {
    	char[] tmp = new char[length];
		for (int i=0; i<length; i++) 
		{
			tmp[i] = (char)(rand.nextInt(93)+33);
		}
		return String.valueOf(tmp);
    }
    
    @Override
    public Packet toPacket()
	{
		Packet packet = super.toPacket();
		packet.reset();
		packet.appendByte(this.protocolVersion);
		packet.appendNullTerminatedString(this.serverVersion);
		packet.appendFixedLengthInteger(this.connectionId, 4);
		packet.appendFixedLengthString(this.authPluginDataPart1, 8);
		packet.appendFillNullByte();
		packet.appendFixedLengthInteger(capabilityFlags1, 2);
		packet.appendFixedLengthInteger(this.characterSet, 1);
		packet.appendFixedLengthInteger(this.statusFlags, 2);
		packet.appendFixedLengthInteger(capabilityFlags2, 2);
		if (this.hasCapabilityFlag(Flags.CLIENT_PLUGIN_AUTH )) 
		{
			packet.appendFixedLengthInteger(this.authPluginDataPart2.length(), 1);
		}else
		{
			packet.appendFixedLengthInteger(0, 1);
		}
		packet.appendFillNullBytes(10);
		
		if (this.hasCapabilityFlag(Flags.CLIENT_SECURE_CONNECTION)) 
		{
		    packet.appendFixedLengthString(this.authPluginDataPart2, this.authPluginDataPart2.length());
		    if (this.hasCapabilityFlag(Flags.CLIENT_PLUGIN_AUTH )) 
			{
		    	 /*if version >= (5.5.7 and < 5.5.10) or (>= 5.6.0 and < 5.6.2) {
		    		 string[EOF]    auth-plugin name
				 } elseif version >= 5.5.10 or >= 5.6.2 {
					 string[NUL]    auth-plugin name
				 }*/
		    	packet.appendRestOfPacketString(this.authPluginName);
			}
		}
        return packet;
	}
    
    @Override
    protected void parsePacket()
    {
    	Packet packet = super.toPacket();
    	if(packet.getPayloadLength() > 0)
    	{
    		this.protocolVersion = packet.readByte(0);
    		this.serverVersion = packet.readNullTerminatedString(1);
    		this.connectionId =  packet.readFixedLengthInteger(2, 4);
    		packet.appendNullTerminatedString(this.serverVersion);
    		packet.appendFixedLengthInteger(this.connectionId, 4);
    		packet.appendFixedLengthString(this.authPluginDataPart1, 8);
    		packet.appendFillNullByte();
    		packet.appendFixedLengthInteger(capabilityFlags1, 2);
    		packet.appendFixedLengthInteger(this.characterSet, 1);
    		packet.appendFixedLengthInteger(this.statusFlags, 2);
    		packet.appendFixedLengthInteger(capabilityFlags2, 2);
    		if (this.hasCapabilityFlag(Flags.CLIENT_PLUGIN_AUTH )) 
    		{
    			packet.appendFixedLengthInteger(this.authPluginDataPart2.length(), 1);
    		}else
    		{
    			packet.appendFixedLengthInteger(0, 1);
    		}
    		packet.appendFillNullBytes(10);
    		
    		if (this.hasCapabilityFlag(Flags.CLIENT_SECURE_CONNECTION)) 
    		{
    		    packet.appendFixedLengthString(this.authPluginDataPart2, this.authPluginDataPart2.length());
    		    if (this.hasCapabilityFlag(Flags.CLIENT_PLUGIN_AUTH )) 
    			{
    		    	 /*if version >= (5.5.7 and < 5.5.10) or (>= 5.6.0 and < 5.6.2) {
    		    		 string[EOF]    auth-plugin name
    				 } elseif version >= 5.5.10 or >= 5.6.2 {
    					 string[NUL]    auth-plugin name
    				 }*/
    		    	packet.appendRestOfPacketString(this.authPluginName);
    			}
    		}
    	}
    }
}
