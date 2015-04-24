package com.mysql.proxy.protocol;

import java.util.BitSet;

public class EnumBitSet<T extends Enum<T> & IEnumBitSet<T>> extends BitSet
{
	public void and(T t)
	{
		this.and(t.toBitSet());
	}
	
	public void or(T t)
	{
		this.or(t.toBitSet());
	}
}
