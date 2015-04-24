package com.mysql.proxy.protocol.extensions;

import java.io.UnsupportedEncodingException;

import lombok.ExtensionMethod;

import com.mysql.proxy.extensions.UnsignedByteExtension;
import com.mysql.proxy.protocol.CharacterSet;

/**
 * 
 * @author n2501
 * static method, build  packet byte
 * http://dev.mysql.com/doc/internals/en/integer.html
 * http://dev.mysql.com/doc/internals/en/string.html
 */
@ExtensionMethod({ UnsignedByteExtension.class })
public class BasicTypesExtension
{
	/**
	 * To fixed length integer byte.
	 *
	 * @param value the value
	 * @param length the length
	 * @return the byte[]
	 */
	/*public static byte[] buildFixedLengthInteger(Long value, int length)
	{
		if (length <= 0)
			throw new IllegalArgumentException("length");

		byte[] packet = new byte[length];
		for (int i = 0; i < length; i++)
		{
			packet[i] = (byte) ((value >> ((i) * 8)) & 0xFF);
		}
		return packet;
	}*/
	
	public static byte[] buildFixedLengthInteger(long value, int length)
	{
		if (length <= 0)
			throw new IllegalArgumentException("length");

		byte[] packet = new byte[length];
		for (int i = 0; i < length; i++)
		{
			packet[i] = (byte) ((value >> ((i) * 8)) & 0xFF);
		}
		return packet;
	}

	/**
	 * To fixed length integer.
	 *
	 * @param _bytes the _bytes
	 * @param offset the offset
	 * @param length the length
	 * @return the long
	 */
	public static long toFixedLengthInteger(byte[] _bytes, int offset,	int length)
	{
		return toUInt(_bytes, offset, length);
	}

	/*
	 * public static byte[] toFixedByte(int value, int length) { return
	 * toFixedByte(value, length); }
	 */

	/**
	 * To length encoded integer byte.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] buildLengthEncodedInteger(long value)
	{
		byte[] packet;
		if (value < 252)
		{
			packet = new byte[1];
			packet[0] = (byte) ((value >> 0) & 0xFF);
			return packet;
		} else if (value < (2 ^ 16 - 1))
		{
			packet = new byte[3];
			packet[0] = (byte) 0xFC;
		} else if (value < (2 ^ 24 - 1))
		{
			packet = new byte[4];
			packet[0] = (byte) 0xFD;
		} else
		{
			packet = new byte[9];
			packet[0] = (byte) 0xFE;
		}

		int length = packet.length;
		for (int i = 1; i < length; i++)
		{
			packet[i] = (byte) ((value >> ((i - 1) * 8)) & 0xFF);
		}
		return packet;
	}

	/**
	 * To length encoded integer.
	 *
	 * @param _bytes the _bytes
	 * @return the long
	 */
	public static long toLengthEncodedInteger(byte[] _bytes, int offset)
	{
		int size = 0;
		if (_bytes[offset] < 251)
		{
			// 1 byte int
			size = 1;
		} else if (_bytes[offset] == 252)
		{
			// 2 byte int
			size = 2;
		} else if (_bytes[offset] == 253)
		{
			// 3 byte int
			size = 3;
		} else if (_bytes[offset] == 254)
		{
			// 8 byte int
			size = 8;
		}
		return toUInt(_bytes, size);
	}

	// http://dev.mysql.com/doc/internals/en/string.html
	/**
	 * To fixed length string byte.
	 *
	 * @param str the str
	 * @param size the size
	 * @return the byte[]
	 */
	public static byte[] buildFixedLengthString(String str, int size)
	{
		byte[] packet = new byte[size];
		byte[] strByte = str.getBytes();
		if (strByte.length < packet.length)
			size = strByte.length;
		System.arraycopy(strByte, 0, packet, 0, size);
		return packet;
	}

	/**
	 * To rest(EOF) of packet string byte.
	 *
	 * @param str the str
	 * @return the byte[]
	 */
	public static byte[] buildRestOfPacketString(String str)
	{
		return buildFixedLengthString(str, str.length());
	}

	/**
	 * To nul terminated string byte.
	 *
	 * @param str the str
	 * @return the byte[]
	 */
	public static byte[] buildNulTerminatedString(String str)
	{
		return buildFixedLengthString(str, str.length() + 1);
	}

	public static byte toByte(byte[] _byte, int offset)
	{
		// 1byte
		return _byte[offset];
	}
	
	/**
	 * To u int.
	 *
	 * @param _byte the _byte
	 * @param offset the offset
	 * @param length the length
	 * @return the long
	 */
	public static long toUInt(byte[] _byte, int offset, int length)
	{
		return _byte.toUInt(offset, length);
	}

	public static long toUInt(byte[] _byte, int offset)
	{
		return toUInt(_byte, offset, 4);
	}

	/**
	 * Gets the unsigned int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned int
	 */
	public static long toUInt(byte[] _byte)
	{
		return toUInt(_byte, 0, 4);
	}

	
	public static int toUShort(byte[] _byte, int offset)
	{
		// 2byte
		return _byte[offset] & 0xFF | ((_byte[offset + 1] & 0xFF) << 8);
	}
	
	/**
	 * Gets the unsigned short.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned short
	 */
	public static int toUShort(byte[] _byte)
	{
		// 2byte
		return toUShort(_byte, 0);
	}

	/**
	 * Gets the unsigned byte.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned byte
	 */
	public static short toUByte(byte[] _byte, int offset)
	{
		// 1byte
		return (short) (_byte[offset] & 0xFF);
	}
	
	public static byte[] buildFixedLengthString(String str, int size, CharacterSet encoding) throws UnsupportedEncodingException
	{
		//euckr.
		byte[] packet = new byte[size];
		byte[] strByte = str.getBytes(encoding.getJavaEncodingName());
		if (strByte.length < packet.length)
		{
			size = strByte.length;
		}
		System.arraycopy(strByte, 0, packet, 0, size);
		return packet;
	}

	/**
	 * To nul terminated string byte.
	 * 
	 * @param str
	 *            the str
	 * @return the byte[]
	 */
	public static byte[] buildNullTerminatedString(String str)
	{
		return buildFixedLengthString(str, str.length() + 1);
	}
	
	public static byte[] buildFillBytes(byte value, int length)
	{
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++)
			bytes[i] = value;
		return bytes;
	}
	
	/*//----------------------------------------------------------------------
	// static method, read packet byte to Literal data type
	// http://dev.mysql.com/doc/internals/en/integer.html
	// http://dev.mysql.com/doc/internals/en/string.html
	//----------------------------------------------------------------------
	*//**
	 * To fixed length integer.
	 * 
	 * @param _bytes
	 *            the _bytes
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the long
	 *//*
	public static long readFixedLengthInteger(byte[] _bytes, int offset,
			int length)
	{
		return readUInt(_bytes, offset, length);
	}

	*//**
	 * To length encoded integer.
	 * 
	 * @param _bytes
	 *            the _bytes
	 * @return the long
	 *//*
	public static long readLengthEncodedInteger(byte[] _bytes, int offset)
	{
		int size=0;
		if (_bytes[offset] < 251)
		{
			// 1 byte int
			size = 1;
		} else if (_bytes[offset] == 252)
		{
			// 2 byte int
			size = 2;
		} else if (_bytes[offset] == 253)
		{
			// 3 byte int
			size = 3;
		} else if (_bytes[offset] == 254)
		{
			// 8 byte int
			size = 8;
		}
		return BasicTypesExtension.readUInt(_bytes, size);
	}

	*//**
	 * To u int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the long
	 *//*
	public static long readUInt(byte[] _byte, int offset, int length)
	{
		long rtn = 0L;
		for (int i = 0; i < length; i++)
		{
			rtn |= (_byte[offset + i] & 0xFF) << ((i) * 8);
		}
		return rtn;
	}

	public static long readUInt(byte[] _byte, int offset)
	{
		return readUInt(_byte, offset, 4);
	}

	*//**
	 * Gets the unsigned int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned int
	 *//*
	public static long readUInt(byte[] _byte)
	{
		return readUInt(_byte, 0, 4);
	}

	*//**
	 * Read u short.
	 *
	 * @param _byte the _byte
	 * @param offset the offset
	 * @return the int
	 *//*
	public static int readUShort(byte[] _byte, int offset)
	{
		// 2byte
		return _byte[offset] & 0xFF | ((_byte[offset + 1] & 0xFF) << 8);
	}

	*//**
	 * Gets the unsigned short.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned short
	 *//*
	public static int readUShort(byte[] _byte)
	{
		// 2byte
		return readUShort(_byte, 0);
	}

	*//**
	 * Gets the unsigned byte.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned byte
	 *//*
	public static short readUByte(byte[] _byte, int offset)
	{
		// 1byte
		return (short) (_byte[offset] & 0xFF);
	}
	
	public static short readUByte(byte[] _byte)
	{
		// 1byte
		return BasicTypesExtension.readUByte(_byte, 0);
	}
	
	public static byte readByte(byte[] _byte, int offset)
	{
		// 1byte
		return _byte[offset];
	}*/
	
	// http://www.javadom.com/tutorial/serialize/htonl.html
	/*
	 * static short swap(short x) { return (short)((x << 8) | ((x >> 8) &
	 * 0xff)); }
	 * 
	 * static char swap(char x) { return (char)((x << 8) | ((x >> 8) & 0xff)); }
	 * 
	 * static int swap(int x) { return (int)((swap((short)x) << 16) |
	 * (swap((short)(x >> 16)) & 0xffff)); }
	 * 
	 * static long swap(long x) { return (long)(((long)swap((int)(x)) << 32) |
	 * ((long)swap((int)(x >> 32)) & 0xffffffffL)); }
	 * 
	 * static float swap(float x) { return
	 * Float.intBitsToFloat(swap(Float.floatToRawIntBits(x))); }
	 * 
	 * static double swap(double x) { return
	 * Double.longBitsToDouble(swap(Double.doubleToRawLongBits(x))); }
	 */
}
