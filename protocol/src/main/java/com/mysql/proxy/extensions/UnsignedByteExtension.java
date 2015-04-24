package com.mysql.proxy.extensions;

import java.math.BigInteger;

/**
 * The Class byte의 signed , unsigend 를 포함하는 형변환을 위한 확장 메서드.
 * Little endian
 * http://www.javadom.com/tutorial/serialize/htonl.html
 */
public class UnsignedByteExtension
{

	/**
	 * 8byte read Gets the unsigned long.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned BigInteger
	 */
	public static BigInteger toULong(byte[] _byte, int offset)
	{
		//8byte
		return  BigInteger.valueOf((_byte[offset++] & 0xFF) | ((_byte[offset++] & 0xFF)<<8) | ((_byte[offset++] & 0xFF)<<16) |((_byte[offset++] & 0xFF)<<24)
				| ((_byte[offset++] & 0xFF)<<32 ) | ((_byte[offset++] & 0xFF)<<40) | ((_byte[offset++] & 0xFF)<<48) |((_byte[offset] & 0xFF)<<56));
	}
	
	/**
	 * length byte read Gets the unsigned int.
	 * 
	 * @param in
	 *            the in
	 * @param length
	 *            the length
	 * @return the unsigned int
	 */
	public static long toUInt(byte[] _byte, int offset, int length)
	{
		long rtn = 0L;
		for(int i=offset; i < length; i++)
		{
			rtn |= (_byte[i] & 0xFF) << ((i)*8);
		}
		return rtn;
	}
	
	/**
	 * 4byte read Gets the unsigned int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned int
	 */
	public static long toUInt(byte[] _byte, int offset) 
	{
		return toUInt(_byte, offset, 4);
		//return toUInt(_byte, _byte.length);
	}
	
	/**
	 * 2byte read Gets the unsigned short.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned short
	 */
	public static int toUShort(byte[] _byte, int offset)
	{
		//2byte
		return (int) (_byte[offset++] & 0xFF) | ((_byte[offset] & 0xFF)<<8);
	}
	
	/**
	 * 1byte read Gets the unsigned byte.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned byte
	 */
	public static short toUByte(byte[] _byte, int offset)
	{
		//1byte
		return (short) (_byte[offset] & 0xFF);
	}
}
