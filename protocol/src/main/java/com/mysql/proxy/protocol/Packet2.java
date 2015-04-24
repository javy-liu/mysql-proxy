package com.mysql.proxy.protocol;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;

import lombok.ExtensionMethod;

import com.mysql.proxy.protocol.extensions.BasicTypesExtension;

/**
 * The Class Packet. If a MySQL client or server wants to send data, it: Splits
 * the data into packets of size (2^24–1) = 16777215 bytes Prepends to each
 * chunk a packet header
 */
@ExtensionMethod({ BasicTypesExtension.class })
public class Packet2
{
	private static final int _HEADER_SIZE= 4;
	//2^24 -1
	private static final int _MAX_PACKET_SIZE = 16777215;
	
	/** 내부 데이터 버퍼. */
	private byte[] byteBuffer;
	
	/** 패킷 Size */
	private int length;
	
	/** Packet byte중 payload부분의 Size */
	private int payloadLength = 0;

	/**
	 * Instantiates a new packet.
	 * 
	 * @param packetBytes
	 *            the buffer
	 */
	public Packet2(byte[] packetBytes)
	{
		this.byteBuffer = packetBytes;
		this.payloadLength = _getPayloadLength();
		this.length = this.payloadLength + _HEADER_SIZE;
		if(this.length < packetBytes.length)
		{
			throw new IllegalArgumentException("packetBytes over");
		}
	}

	public Packet2(int capacity)
	{
		this.byteBuffer = new byte[capacity];
		this.length = _HEADER_SIZE;
	}

	/**
	 * 현재의 내부 버퍼 용량을 돌려줍니다
	 *
	 * @return 현재 내부 버퍼 용량
	 */
	public int capacity()
	{
		return this.byteBuffer.length;
	}
	
	public void reset()
	{
		this.length = _HEADER_SIZE;
		this.payloadLength = 0;
	}
	
	/**
	 * Head Packet을 포함하는 전체 유효 패킷길이
	 *
	 * @return the int
	 */
	public int length()
	{
		return this.length;
	}
	
	public int getPayloadLength()
	{
		return this.payloadLength;
	}
	
	private int _getPayloadLength()
	{	
		return this.byteBuffer[0] & 0xFF | ((this.byteBuffer[1] & 0xFF) << 8)
				| ((this.byteBuffer[2] & 0xFF) << 16);
	}
	
	private void setPayloadLength(int length)
	{
		byte[] b = buildFixedLengthInteger(length, 3);
		System.arraycopy(b, 0, this.byteBuffer, 0, 3);
		this.payloadLength = length;
		this.length = length + _HEADER_SIZE;
	}
	
	public void addPayloadLength(int length)
	{
		setPayloadLength(getPayloadLength()+length);
	}

	public short getSequenceId()
	{
		return (short) (this.byteBuffer[3] & 0xFF);
		// return this.byteBuffer.toUByte(3);
	}
	
	public void setSequenceId(short sequenceId)
	{
		if (sequenceId > 255)
			throw new IllegalArgumentException("sequenceId > 255");
		this.byteBuffer[3] = (byte) sequenceId;
	}

	public int getType()
	{
		return this.byteBuffer[4];
	}

	public byte[] getPayloadBytes()
	{
		byte[] payloadByte = new byte[this.getPayloadLength()];
		System.arraycopy(this.byteBuffer, _HEADER_SIZE, payloadByte, 0, payloadByte.length);
		return payloadByte;
	}

	/*public Protocol getPayload()
	{
		return null;
	}*/

	//append byte
	
	public final void appendByte(byte b) throws SQLException
	{
		ensureCapacity(1);
		this.byteBuffer[this.length] = b;
		addPayloadLength(1);
		//setPayloadLength(getPayloadLength() + 1);
	}

	public final void appendBytes(byte[] bytes) throws SQLException
	{
		ensureCapacity(bytes.length);
		System.arraycopy(bytes, 0, this.byteBuffer, this.length , bytes.length);
		//setPayloadLength(getPayloadLength() + bytes.length);
		addPayloadLength(bytes.length);
	}

	public void appendFillNullByte() throws SQLException
	{
		this.appendFillByte((byte)0x00);
	}
	
	public void appendFillNullBytes(int lenght) throws SQLException
	{
		this.appendFillBytes((byte)0x00, length);
	}
	
	public void appendFillByte(byte value) throws SQLException
	{
		this.appendByte(value);
	}
	
	public void appendFillBytes(byte value, int length) throws SQLException
	{
		this.appendBytes(Packet2.buildFillBytes(value, length));
	}
	/**
	 * To fixed length integer byte.
	 * 
	 * @param value
	 *            the value
	 * @param length
	 *            the length
	 * @throws SQLException 
	 */
	public void appendFixedLengthInteger(long value, int length) throws SQLException
	{
		this.appendBytes(Packet2.buildFixedLengthInteger(value, length));
	}

	public void appendLengthEncodedInteger(long value) throws SQLException
	{
		this.appendBytes(Packet2.buildLengthEncodedInteger(value));
	}
	
	public void appendFixedLengthString(String value, int length) throws SQLException
	{
		this.appendBytes(Packet2.buildFixedLengthString(value, length));
	}
	
	public void appendRestOfPacketString(String value) throws SQLException
	{
		this.appendBytes(Packet2.buildRestOfPacketString(value));
	}
	
	public void appendNullTerminatedString(String value) throws SQLException
	{
		this.appendBytes(Packet2.buildNullTerminatedString(value));
	}
	
	public void replaceBytes(byte[] bytes, int offset, int beforeSize) throws SQLException
	{
		if(bytes.length == beforeSize)
		{
			System.arraycopy(bytes, 0, this.byteBuffer, offset, beforeSize);
		}else if(bytes.length > beforeSize)
		{
			ensureCapacity(bytes.length - beforeSize);
			byte[] temp = this.getBytes(offset+beforeSize, this.getPayloadLength()-(offset+beforeSize));
			System.arraycopy(bytes, 0, this.byteBuffer, offset, bytes.length);
			System.arraycopy(temp, 0, this.byteBuffer, offset + bytes.length, temp.length);
		}else if(bytes.length < beforeSize)
		{
			byte[] temp = this.getBytes(offset+beforeSize, this.getPayloadLength()-(offset+beforeSize));
			System.arraycopy(bytes, 0, this.byteBuffer, offset, bytes.length);
			System.arraycopy(temp, 0, this.byteBuffer, offset + bytes.length, temp.length);
		}
	}

	//read
	public long readFixedLengthInteger(int offset, int length)
	{
		return Packet2.readFixedLengthInteger(this.byteBuffer, offset + _HEADER_SIZE, length);
	}
	
	public long readUInt(int offset, int length)
	{
		return Packet2.readUInt(this.byteBuffer, offset + _HEADER_SIZE, length);
	}
	
	public long readUInt(int offset)
	{
		return Packet2.readUInt(this.byteBuffer, offset + _HEADER_SIZE);
	}
	
	public int readUShort(int offset)
	{
		return Packet2.readUShort(this.byteBuffer, offset + _HEADER_SIZE);
	}
	
	public int readUByte(int offset)
	{
		return Packet2.readUByte(this.byteBuffer, offset + _HEADER_SIZE);
	}
	
	public final String readString(int offset, CharacterSet encoding) throws UnsupportedEncodingException
	{
		int i = offset + _HEADER_SIZE;
		int len = 0;
		int maxLen = this.length();

		while ((i < maxLen) && (this.byteBuffer[i] != 0))
		{
			++len;
			++i;
		}
		String str = new String(this.byteBuffer, i, len, encoding.getJavaEncodingName());
		return str;
	}
	
	public final String readString(int offset)
	{
		int i = offset + _HEADER_SIZE;
		int len = 0;
		int maxLen = this.length();

		while ((i < maxLen) && (this.byteBuffer[i] != 0))
		{
			++len;
			++i;
		}

		String s = new String(this.byteBuffer, i, len);

		return s;
	}

	//----------------------------------------------------------------------
	// static method, build  packet byte
	// http://dev.mysql.com/doc/internals/en/integer.html
	// http://dev.mysql.com/doc/internals/en/string.html
	//----------------------------------------------------------------------
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
	 * To length encoded integer byte.
	 * 
	 * @param value
	 *            the value
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
	 * To fixed length string byte.
	 * 
	 * @param str
	 *            the str
	 * @param size
	 *            the size
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
	 * To rest(EOF) of packet string byte.
	 * 
	 * @param str
	 *            the str
	 * @return the byte[]
	 */
	public static byte[] buildRestOfPacketString(String str)
	{
		return buildFixedLengthString(str, str.length());
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
	
	//----------------------------------------------------------------------
	// static method, read packet byte to Literal data type
	// http://dev.mysql.com/doc/internals/en/integer.html
	// http://dev.mysql.com/doc/internals/en/string.html
	//----------------------------------------------------------------------
	/**
	 * To fixed length integer.
	 * 
	 * @param _bytes
	 *            the _bytes
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the long
	 */
	public static long readFixedLengthInteger(byte[] _bytes, int offset,
			int length)
	{
		return readUInt(_bytes, offset, length);
	}

	/**
	 * To length encoded integer.
	 * 
	 * @param _bytes
	 *            the _bytes
	 * @return the long
	 */
	public static long readLengthEncodedInteger(byte[] _bytes)
	{
		int size = 0;
		int offset = 0;
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
		return readUInt(_bytes, size);
	}

	/**
	 * To u int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the long
	 */
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

	/**
	 * Gets the unsigned int.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned int
	 */
	public static long readUInt(byte[] _byte)
	{
		return readUInt(_byte, 0, 4);
	}

	/**
	 * Read u short.
	 *
	 * @param _byte the _byte
	 * @param offset the offset
	 * @return the int
	 */
	public static int readUShort(byte[] _byte, int offset)
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
	public static int readUShort(byte[] _byte)
	{
		// 2byte
		return readUShort(_byte, 0);
	}

	/**
	 * Gets the unsigned byte.
	 * 
	 * @param _byte
	 *            the _byte
	 * @return the unsigned byte
	 */
	public static short readUByte(byte[] _byte, int offset)
	{
		// 1byte
		return (short) (_byte[offset] & 0xFF);
	}
	
	public static short readUByte(byte[] _byte)
	{
		// 1byte
		return Packet2.readUByte(_byte, 0);
	}

	public final void ensureCapacity(int minimumCapacity) throws SQLException
	{
		int newCapacity = this.length + minimumCapacity;
		if(!hasEnsureCapacity(minimumCapacity))
		{
			throw new SQLException("maxium packet size");
		}else if (newCapacity > capacity())
		{
			//이전의 용량에 2배를 한다
			newCapacity = (int) (this.byteBuffer.length * 2);
			//요구 용량이 현재 용량의 2배 보다 많은 경우
			if(newCapacity < minimumCapacity)
			{
				newCapacity = this.byteBuffer.length + (int) (minimumCapacity * 1.5); 
			}
			//overflow
			if(newCapacity < 0)
				newCapacity = _MAX_PACKET_SIZE;
			
			byte[] newBytes = new byte[newCapacity];
			System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
			this.byteBuffer = newBytes;
		}
	}
	
	public final boolean hasEnsureCapacity(int minimumCapacity)
	{
		int newCapacity = this.length + minimumCapacity;
		if(newCapacity > _MAX_PACKET_SIZE)
		{
			return false;
		}
		return true;
	}

	//byte read
	public byte[] getByteBuffer()
	{
		return this.byteBuffer;
	}
	
	public byte[] getBytes()
	{
		return this.getBytes(this.length);
	}

	public byte[] getBytes(int len)
	{
		return this.getBytes(0, this.length);
	}

	public byte[] getBytes(int offset, int len)
	{
		if((offset + len) > this.length)
			throw new IllegalArgumentException("offset +  len > this.length");
		
		return Arrays.copyOfRange(this.byteBuffer, offset, len);
	}
}
