package com.mysql.proxy.protocol;

import lombok.Getter;

public abstract class Protocol
{
	
	private Packet packet;
	
	public Protocol(Packet packet)
	{
		this.packet =  packet;
	}
	
	public static Protocol loadPacket(Packet packet)
	{
		return null;
	}
	
	public Packet toPacket()
	{
		return packet;
	}
	
	protected abstract void parsePacket();
}
