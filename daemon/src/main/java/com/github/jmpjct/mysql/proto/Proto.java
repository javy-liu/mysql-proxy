package com.github.jmpjct.mysql.proto;

import org.apache.log4j.Logger;

public class Proto {
    public byte[] packet = null;
    public int offset = 0;
    
    public Proto(byte[] packet) {
        this.packet = packet;
    }
    
    public Proto(byte[] packet, int offset) {
        this.packet = packet;
        this.offset = offset;
    }
    
    public static byte[] build_fixed_int(int size, long value) {
        byte[] packet = new byte[size];
        
        if (size == 8) {
            packet[0] = (byte) ((value >>  0) & 0xFF);
            packet[1] = (byte) ((value >>  8) & 0xFF);
            packet[2] = (byte) ((value >> 16) & 0xFF);
            packet[3] = (byte) ((value >> 24) & 0xFF);
            packet[4] = (byte) ((value >> 32) & 0xFF);
            packet[5] = (byte) ((value >> 40) & 0xFF);
            packet[6] = (byte) ((value >> 48) & 0xFF);
            packet[7] = (byte) ((value >> 56) & 0xFF);
        }
        else if (size == 4) {
            packet[0] = (byte) ((value >>  0) & 0xFF);
            packet[1] = (byte) ((value >>  8) & 0xFF);
            packet[2] = (byte) ((value >> 16) & 0xFF);
            packet[3] = (byte) ((value >> 24) & 0xFF);
        }
        else if (size == 3) {
            packet[0] = (byte) ((value >>  0) & 0xFF);
            packet[1] = (byte) ((value >>  8) & 0xFF);
            packet[2] = (byte) ((value >> 16) & 0xFF);
        }
        else if (size == 2) {
            packet[0] = (byte) ((value >>  0) & 0xFF);
            packet[1] = (byte) ((value >>  8) & 0xFF);
        }
        else if (size == 1) {
            packet[0] = (byte) ((value >>  0) & 0xFF);
        }
        else {
            Logger.getLogger("MySQL.Proto").fatal("Encoding int["+size+"] "+value+" failed!");
            return null;
        }
        return packet;
    }
    
    public static byte[] build_lenenc_int(long value) {
        byte[] packet = null;
        
        if (value < 251) {
            packet = new byte[1];
            packet[0] = (byte) ((value >>  0) & 0xFF);
        }
        else if (value < (2^16 - 1)) {
            packet = new byte[3];
            packet[0] = (byte) 0xFC;
            packet[1] = (byte) ((value >>  0) & 0xFF);
            packet[2] = (byte) ((value >>  8) & 0xFF);
        }
        else if (value < (2^24 - 1)) {
            packet = new byte[4];
            packet[0] = (byte) 0xFD;
            packet[1] = (byte) ((value >>  0) & 0xFF);
            packet[2] = (byte) ((value >>  8) & 0xFF);
            packet[3] = (byte) ((value >> 16) & 0xFF);
        }
        else {
            packet = new byte[9];
            packet[0] = (byte) 0xFE;
            packet[1] = (byte) ((value >>  0) & 0xFF);
            packet[2] = (byte) ((value >>  8) & 0xFF);
            packet[3] = (byte) ((value >> 16) & 0xFF);
            packet[4] = (byte) ((value >> 24) & 0xFF);
            packet[5] = (byte) ((value >> 32) & 0xFF);
            packet[6] = (byte) ((value >> 40) & 0xFF);
            packet[7] = (byte) ((value >> 48) & 0xFF);
            packet[8] = (byte) ((value >> 56) & 0xFF);
        }
        
        return packet;
    }
    
    public static byte[] build_lenenc_str(String str) {
        if (str.equals("")) {
            byte[] packet = new byte[1];
            packet[0] = 0x00;
            return packet;
        }
        
        byte[] size = Proto.build_lenenc_int(str.length());
        byte[] strByte = Proto.build_fixed_str(str.length(), str);
        byte[] packet = new byte[size.length + strByte.length];
        System.arraycopy(size, 0, packet, 0, size.length);
        System.arraycopy(strByte, 0, packet, size.length, strByte.length);
        return packet;
    }
    
    public static byte[] build_null_str(String str) {
        return Proto.build_fixed_str(str.length() + 1, str);
    }
    
    public static byte[] build_fixed_str(int size, String str) {
        byte[] packet = new byte[size];
        byte[] strByte = str.getBytes();
        if (strByte.length < packet.length)
            size = strByte.length;
        System.arraycopy(strByte, 0, packet, 0, size);
        return packet;
    }
    
    public static byte[] build_eop_str(String str) {
        return Proto.build_fixed_str(str.length(), str);
    }
    
    public static byte[] build_filler(int len) {
        return Proto.build_filler(len, (byte)0x00);
    }
    
    public static byte[] build_filler(int len, int filler_value) {
        return Proto.build_filler(len, (byte)filler_value);
    }
    
    public static byte[] build_filler(int len, byte filler_value) {
        byte[] filler = new byte[len];
        for (int i = 0; i < len; i++)
            filler[i] = filler_value;
        return filler;
    }
    
    public static byte[] build_byte(byte value) {
        byte[] field = new byte[1];
        field[0] = value;
        return field;
    }
    
    public static char int2char(byte i) {
        return (char)i;
    }
    
    public static byte char2int(char i) {
        return (byte)i;
    }
    
    public long get_fixed_int(int size) {
        byte[] bytes = null;
        
        if ( this.packet.length < (size + this.offset))
            return -1;
        
        bytes = new byte[size];
        System.arraycopy(packet, offset, bytes, 0, size);
        this.offset += size;
        return this.get_fixed_int(bytes);
    }
    
    public static long get_fixed_int(byte[] bytes) {
        long value = 0;
        
        for (int i = bytes.length-1; i > 0; i--) {
            value |= bytes[i] & 0xFF;
            value <<= 8;
        }
        value |= bytes[0] & 0xFF;
                  
        return value;
    }
    
    public void get_filler(int size) {
        this.offset += size;
    }
    
    public long get_lenenc_int() {
        int size = 0;
        
        // 1 byte int
        if (this.packet[offset] < 251) {
            size = 1;
        }
        // 2 byte int
        else if (this.packet[offset] == 252) {
            this.offset += 1;
            size = 2;
        }
        // 3 byte int
        else if (this.packet[offset] == 253) {
            this.offset += 1;
            size = 3;
        }
        // 8 byte int
        else if (this.packet[offset] == 254) {
            this.offset += 1;
            size = 8;
        }
        
        if (size == 0) {
            Logger.getLogger("MySQL.Proto").fatal("Decoding int at offset "+offset+" failed!");
            return -1;
        }
        
        return this.get_fixed_int(size);
    }
    
    public String get_fixed_str(int len) {
        int start = this.offset;
        int end = this.offset+len;
        StringBuilder str = new StringBuilder(end);
        
        for (int i = start; i < end; i++) {
            str.append(Proto.int2char(packet[i]));
            this.offset += 1;
        }
        
        return str.toString();
    }
    
    public String get_null_str() {
        int start = this.offset;
        int end = this.packet.length;
        StringBuilder str = new StringBuilder(end);
        
        for (int i = start; i < end; i++) {
            if (packet[i] == 0x00) {
                this.offset += 1;
                break;
            }
            str.append(Proto.int2char(packet[i]));
            this.offset += 1;
        }
        
        return str.toString();
    }
    
    public String get_eop_str() {
        int start = this.offset;
        int end = this.packet.length;
        StringBuilder str = new StringBuilder(end);
        
        for (int i = start; i < end; i++) {
            if (packet[i] == 0x00 && i == packet.length-1) {
                this.offset += 1;
                break;
            }
            str.append(Proto.int2char(packet[i]));
            this.offset += 1;
        }
        
        return str.toString();
    }
    
    public String get_lenenc_str() {
        int size = (int)this.get_lenenc_int();
        int start = this.offset;
        int end = this.offset + size;
        StringBuilder str = new StringBuilder(end);
        
        for (int i = start; i < end; i++) {
            str.append(Proto.int2char(packet[i]));
            this.offset += 1;
        }
        
        return str.toString();
    }
}
