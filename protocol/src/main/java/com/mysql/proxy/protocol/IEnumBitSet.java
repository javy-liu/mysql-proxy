package com.mysql.proxy.protocol;


public interface IEnumBitSet<T extends Enum<T> & IEnumBitSet<T>>
{
	public int toInt();
	
	public EnumBitSet<T> toBitSet();
}
