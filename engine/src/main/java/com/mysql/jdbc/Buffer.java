/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.mysql.jdbc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

class Buffer
{
	static final int MAX_BYTES_TO_DUMP = 512;
	static final int NO_LENGTH_LIMIT = -1;
	static final long NULL_LENGTH = -1L;
	private int bufLength = 0;
	private byte[] byteBuffer;
	private int position = 0;

	protected boolean wasMultiPacket = false;

	Buffer(byte[] buf)
	{
		this.byteBuffer = buf;
		setBufLength(buf.length);
	}

	Buffer(int size)
	{
		this.byteBuffer = new byte[size];
		setBufLength(this.byteBuffer.length);
		this.position = 4;
	}

	final void clear()
	{
		this.position = 4;
	}

	final void dump()
	{
		dump(getBufLength());
	}

	final String dump(int numBytes)
	{
		return StringUtils.dumpAsHex(
				getBytes(0, (numBytes > getBufLength()) ? getBufLength()
						: numBytes),
				(numBytes > getBufLength()) ? getBufLength() : numBytes);
	}

	final String dumpClampedBytes(int numBytes)
	{
		int numBytesToDump = (numBytes < 512) ? numBytes : 512;

		String dumped = StringUtils.dumpAsHex(
				getBytes(0, (numBytesToDump > getBufLength()) ? getBufLength()
						: numBytesToDump),
				(numBytesToDump > getBufLength()) ? getBufLength()
						: numBytesToDump);

		if (numBytesToDump < numBytes)
		{
			return dumped + " ....(packet exceeds max. dump length)";
		}

		return dumped;
	}

	final void dumpHeader()
	{
		for (int i = 0; i < 4; ++i)
		{
			String hexVal = Integer.toHexString(readByte(i) & 0xFF);

			if (hexVal.length() == 1)
			{
				hexVal = "0" + hexVal;
			}

			System.out.print(hexVal + " ");
		}
	}

	final void dumpNBytes(int start, int nBytes)
	{
		StringBuffer asciiBuf = new StringBuffer();

		for (int i = start; (i < start + nBytes) && (i < getBufLength()); ++i)
		{
			String hexVal = Integer.toHexString(readByte(i) & 0xFF);

			if (hexVal.length() == 1)
			{
				hexVal = "0" + hexVal;
			}

			System.out.print(hexVal + " ");

			if ((readByte(i) > 32) && (readByte(i) < 127))
				asciiBuf.append((char) readByte(i));
			else
			{
				asciiBuf.append(".");
			}

			asciiBuf.append(" ");
		}

		System.out.println("    " + asciiBuf.toString());
	}

	final void ensureCapacity(int additionalData) throws SQLException
	{
		if (this.position + additionalData > getBufLength())
			if (this.position + additionalData < this.byteBuffer.length)
			{
				setBufLength(this.byteBuffer.length);
			} else
			{
				int newLength = (int) (this.byteBuffer.length * 1.25D);

				if (newLength < this.byteBuffer.length + additionalData)
				{
					newLength = this.byteBuffer.length
							+ (int) (additionalData * 1.25D);
				}

				if (newLength < this.byteBuffer.length)
				{
					newLength = this.byteBuffer.length + additionalData;
				}

				byte[] newBytes = new byte[newLength];

				System.arraycopy(this.byteBuffer, 0, newBytes, 0,
						this.byteBuffer.length);

				this.byteBuffer = newBytes;
				setBufLength(this.byteBuffer.length);
			}
	}

	public int fastSkipLenString()
	{
		long len = readFieldLength();
		Buffer tmp6_5 = this;
		tmp6_5.position = (int) (tmp6_5.position + len);

		return (int) len;
	}

	public void fastSkipLenByteArray()
	{
		long len = readFieldLength();

		if ((len == -1L) || (len == 0L))
			return;
		Buffer tmp21_20 = this;
		tmp21_20.position = (int) (tmp21_20.position + len);
	}

	protected final byte[] getBufferSource()
	{
		return this.byteBuffer;
	}

	int getBufLength()
	{
		return this.bufLength;
	}

	public byte[] getByteBuffer()
	{
		return this.byteBuffer;
	}

	final byte[] getBytes(int len)
	{
		byte[] b = new byte[len];
		System.arraycopy(this.byteBuffer, this.position, b, 0, len);
		this.position += len;

		return b;
	}

	byte[] getBytes(int offset, int len)
	{
		byte[] dest = new byte[len];
		System.arraycopy(this.byteBuffer, offset, dest, 0, len);

		return dest;
	}

	int getCapacity()
	{
		return this.byteBuffer.length;
	}

	public ByteBuffer getNioBuffer()
	{
		throw new IllegalArgumentException(
				Messages.getString("ByteArrayBuffer.0"));
	}

	public int getPosition()
	{
		return this.position;
	}

	final boolean isLastDataPacket()
	{
		return ((getBufLength() < 9) && ((this.byteBuffer[0] & 0xFF) == 254));
	}

	final long newReadLength()
	{
		int sw = this.byteBuffer[(this.position++)] & 0xFF;

		switch (sw)
		{
		case 251:
			return 0L;
		case 252:
			return readInt();
		case 253:
			return readLongInt();
		case 254:
			return readLongLong();
		}

		return sw;
	}

	final byte readByte()
	{
		return this.byteBuffer[(this.position++)];
	}

	final byte readByte(int readAt)
	{
		return this.byteBuffer[readAt];
	}

	final long readFieldLength()
	{
		int sw = this.byteBuffer[(this.position++)] & 0xFF;

		switch (sw)
		{
		case 251:
			return -1L;
		case 252:
			return readInt();
		case 253:
			return readLongInt();
		case 254:
			return readLongLong();
		}

		return sw;
	}

	final int readInt()
	{
		byte[] b = this.byteBuffer;

		return (b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8);
	}

	final int readIntAsLong()
	{
		byte[] b = this.byteBuffer;

		return (b[(this.position++)] & 0xFF
				| (b[(this.position++)] & 0xFF) << 8
				| (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24);
	}

	final byte[] readLenByteArray(int offset)
	{
		long len = readFieldLength();

		if (len == -1L)
		{
			return null;
		}

		if (len == 0L)
		{
			return Constants.EMPTY_BYTE_ARRAY;
		}

		this.position += offset;

		return getBytes((int) len);
	}

	final long readLength()
	{
		int sw = this.byteBuffer[(this.position++)] & 0xFF;

		switch (sw)
		{
		case 251:
			return 0L;
		case 252:
			return readInt();
		case 253:
			return readLongInt();
		case 254:
			return readLong();
		}

		return sw;
	}

	final long readLong()
	{
		byte[] b = this.byteBuffer;

		return (b[(this.position++)] & 0xFF
				| (b[(this.position++)] & 0xFF) << 8
				| (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24);
	}

	final int readLongInt()
	{
		byte[] b = this.byteBuffer;

		return (b[(this.position++)] & 0xFF
				| (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16);
	}

	final long readLongLong()
	{
		byte[] b = this.byteBuffer;

		return (b[(this.position++)] & 0xFF
				| (b[(this.position++)] & 0xFF) << 8
				| (b[(this.position++)] & 0xFF) << 16
				| (b[(this.position++)] & 0xFF) << 24
				| (b[(this.position++)] & 0xFF) << 32
				| (b[(this.position++)] & 0xFF) << 40
				| (b[(this.position++)] & 0xFF) << 48 | (b[(this.position++)] & 0xFF) << 56);
	}

	final int readnBytes()
	{
		int sw = this.byteBuffer[(this.position++)] & 0xFF;

		switch (sw)
		{
		case 1:
			return (this.byteBuffer[(this.position++)] & 0xFF);
		case 2:
			return readInt();
		case 3:
			return readLongInt();
		case 4:
			return (int) readLong();
		}

		return 255;
	}

	final String readString()
	{
		int i = this.position;
		int len = 0;
		int maxLen = getBufLength();

		while ((i < maxLen) && (this.byteBuffer[i] != 0))
		{
			++len;
			++i;
		}

		String s = new String(this.byteBuffer, this.position, len);
		this.position += len + 1;

		return s;
	}

	final String readString(String encoding) throws SQLException
	{
		int i = this.position;
		int len = 0;
		int maxLen = getBufLength();

		while ((i < maxLen) && (this.byteBuffer[i] != 0))
		{
			++len;
			++i;
		}
		try
		{
			String str = new String(this.byteBuffer, this.position, len,
					encoding);

			return str;
		} catch (UnsupportedEncodingException uEE)
		{
		} finally
		{
			this.position += len + 1;
		}
		return null;
	}

	void setBufLength(int bufLengthToSet)
	{
		this.bufLength = bufLengthToSet;
	}

	public void setByteBuffer(byte[] byteBufferToSet)
	{
		this.byteBuffer = byteBufferToSet;
	}

	public void setPosition(int positionToSet)
	{
		this.position = positionToSet;
	}

	public void setWasMultiPacket(boolean flag)
	{
		this.wasMultiPacket = flag;
	}

	public String toString()
	{
		return dumpClampedBytes(getPosition());
	}

	public String toSuperString()
	{
		return super.toString();
	}

	public boolean wasMultiPacket()
	{
		return this.wasMultiPacket;
	}

	final void writeByte(byte b) throws SQLException
	{
		ensureCapacity(1);

		this.byteBuffer[(this.position++)] = b;
	}

	final void writeBytesNoNull(byte[] bytes) throws SQLException
	{
		int len = bytes.length;
		ensureCapacity(len);
		System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
		this.position += len;
	}

	final void writeBytesNoNull(byte[] bytes, int offset, int length)
			throws SQLException
	{
		ensureCapacity(length);
		System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
		this.position += length;
	}

	final void writeDouble(double d) throws SQLException
	{
		long l = Double.doubleToLongBits(d);
		writeLongLong(l);
	}

	final void writeFieldLength(long length) throws SQLException
	{
		if (length < 251L)
		{
			writeByte((byte) (int) length);
		} else if (length < 65536L)
		{
			ensureCapacity(3);
			writeByte((byte)-4);
			writeInt((int) length);
		} else if (length < 16777216L)
		{
			ensureCapacity(4);
			writeByte((byte)-3);
			writeLongInt((int) length);
		} else
		{
			ensureCapacity(9);
			writeByte((byte)-2);
			writeLongLong(length);
		}
	}

	final void writeFloat(float f) throws SQLException
	{
		ensureCapacity(4);

		int i = Float.floatToIntBits(f);
		byte[] b = this.byteBuffer;
		b[(this.position++)] = (byte) (i & 0xFF);
		b[(this.position++)] = (byte) (i >>> 8);
		b[(this.position++)] = (byte) (i >>> 16);
		b[(this.position++)] = (byte) (i >>> 24);
	}

	final void writeInt(int i) throws SQLException
	{
		ensureCapacity(2);

		byte[] b = this.byteBuffer;
		b[(this.position++)] = (byte) (i & 0xFF);
		b[(this.position++)] = (byte) (i >>> 8);
	}

	final void writeLenBytes(byte[] b) throws SQLException
	{
		int len = b.length;
		ensureCapacity(len + 9);
		writeFieldLength(len);
		System.arraycopy(b, 0, this.byteBuffer, this.position, len);
		this.position += len;
	}

	final void writeLenString(String s, String encoding, String serverEncoding,
			SingleByteCharsetConverter converter, boolean parserKnowsUnicode,
			ConnectionImpl conn) throws UnsupportedEncodingException,
			SQLException
	{
		byte[] b = null;

		if (converter != null)
			b = converter.toBytes(s);
		else
		{
			b = StringUtils.getBytes(s, encoding, serverEncoding,
					parserKnowsUnicode, conn);
		}

		int len = b.length;
		ensureCapacity(len + 9);
		writeFieldLength(len);
		System.arraycopy(b, 0, this.byteBuffer, this.position, len);
		this.position += len;
	}

	final void writeLong(long i) throws SQLException
	{
		ensureCapacity(4);

		byte[] b = this.byteBuffer;
		b[(this.position++)] = (byte) (int) (i & 0xFF);
		b[(this.position++)] = (byte) (int) (i >>> 8);
		b[(this.position++)] = (byte) (int) (i >>> 16);
		b[(this.position++)] = (byte) (int) (i >>> 24);
	}

	final void writeLongInt(int i) throws SQLException
	{
		ensureCapacity(3);
		byte[] b = this.byteBuffer;
		b[(this.position++)] = (byte) (i & 0xFF);
		b[(this.position++)] = (byte) (i >>> 8);
		b[(this.position++)] = (byte) (i >>> 16);
	}

	final void writeLongLong(long i) throws SQLException
	{
		ensureCapacity(8);
		byte[] b = this.byteBuffer;
		b[(this.position++)] = (byte) (int) (i & 0xFF);
		b[(this.position++)] = (byte) (int) (i >>> 8);
		b[(this.position++)] = (byte) (int) (i >>> 16);
		b[(this.position++)] = (byte) (int) (i >>> 24);
		b[(this.position++)] = (byte) (int) (i >>> 32);
		b[(this.position++)] = (byte) (int) (i >>> 40);
		b[(this.position++)] = (byte) (int) (i >>> 48);
		b[(this.position++)] = (byte) (int) (i >>> 56);
	}

	final void writeString(String s) throws SQLException
	{
		ensureCapacity(s.length() * 2 + 1);
		writeStringNoNull(s);
		this.byteBuffer[(this.position++)] = 0;
	}

	final void writeString(String s, String encoding, ConnectionImpl conn)
			throws SQLException
	{
		ensureCapacity(s.length() * 2 + 1);
		try
		{
			writeStringNoNull(s, encoding, encoding, false, conn);
		} catch (UnsupportedEncodingException ue)
		{
			throw new SQLException(ue.toString(), "S1000");
		}

		this.byteBuffer[(this.position++)] = 0;
	}

	final void writeStringNoNull(String s) throws SQLException
	{
		int len = s.length();
		ensureCapacity(len * 2);
		System.arraycopy(s.getBytes(), 0, this.byteBuffer, this.position, len);
		this.position += len;
	}

	final void writeStringNoNull(String s, String encoding,
			String serverEncoding, boolean parserKnowsUnicode,
			ConnectionImpl conn) throws UnsupportedEncodingException,
			SQLException
	{
		byte[] b = StringUtils.getBytes(s, encoding, serverEncoding,
				parserKnowsUnicode, conn);

		int len = b.length;
		ensureCapacity(len);
		System.arraycopy(b, 0, this.byteBuffer, this.position, len);
		this.position += len;
	}
}