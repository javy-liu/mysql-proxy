package com.mysql.proxy.protocol;

import com.github.jmpjct.mysql.proto.Flags;

public class HandshakeResponse extends Protocol
{
	//4byte capability flags, CLIENT_PROTOCOL_41 always set
	public long capabilityFlags = ClientFlags.CLIENT_PROTOCOL_41;
	//4byte max-packet size
    public long maxPacketSize = 0;
    //1byte character set
    public int characterSet = 0;
    public String username = "";
    public String authResponse = "";
    public String schema = "";
    
	public HandshakeResponse(Packet packet)
	{
		super(packet);
	}
	
	public long getCapabilityFlags()
	{
		//new Packet(this.getPacket());
		return ClientFlags.CLIENT_PROTOCOL_41;
	}
	
	@Override
	protected void parsePacket()
	{
		Packet packet = super.toPacket();
    	if(packet.getPayloadLength() > 0)
    	{
    		this.capabilityFlags = packet.readFixedLengthInteger(4);
    		this.maxPacketSize = packet.readFixedLengthInteger(4);
    		this.characterSet = packet.readByte();
    		//23byte reserved (all [0])
    		packet.skipByte(23);
    		if((this.capabilityFlags & Flags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) > 0)
    		{
    			
    		}
    		this.username = packet.readNullTerminatedString();
    	}
	} 
	
	@Override
    public Packet toPacket()
	{
		Packet packet =  super.toPacket();
		packet.reset();
		packet.appendFixedLengthInteger(capabilityFlags, 4);
		packet.appendFixedLengthInteger(maxPacketSize, 4);
		packet.appendFixedLengthInteger(characterSet, 1);
		return packet;
	}
}
