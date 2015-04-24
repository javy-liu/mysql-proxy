package com.github.jmpjct.mysql.proto;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public class Auth_Challenge extends Packet {
    public long protocolVersion = 0x0a;
    public String serverVersion = "";
    public long connectionId = 0;
    public String challenge1 = "";
    public long capabilityFlags = Flags.CLIENT_PROTOCOL_41;
    public long characterSet = 0;
    public long statusFlags = 0;
    public String challenge2 = "";

    public void setCapabilityFlag(long flag) {
        this.capabilityFlags |= flag;
    }
    
    public void removeCapabilityFlag(long flag) {
        this.capabilityFlags &= ~flag;
    }
    
    public void toggleCapabilityFlag(long flag) {
        this.capabilityFlags ^= flag;
    }
    
    public boolean hasCapabilityFlag(long flag) {
        return ((this.capabilityFlags & flag) == flag);
    }
    
    public void setStatusFlag(long flag) {
        this.statusFlags |= flag;
    }
    
    public void removeStatusFlag(long flag) {
        this.statusFlags &= ~flag;
    }
    
    public void toggleStatusFlag(long flag) {
        this.statusFlags ^= flag;
    }
    
    public boolean hasStatusFlag(long flag) {
        return ((this.statusFlags & flag) == flag);
    }
    
    public ArrayList<byte[]> getPayload() {
        ArrayList<byte[]> payload = new ArrayList<byte[]>();
        
        payload.add( Proto.build_fixed_int(1, this.protocolVersion));
        payload.add( Proto.build_null_str(this.serverVersion));
        payload.add( Proto.build_fixed_int(4, this.connectionId));
        payload.add( Proto.build_fixed_str(8, this.challenge1));
        payload.add( Proto.build_filler(1));
        payload.add( Proto.build_fixed_int(2, this.capabilityFlags));
        payload.add( Proto.build_fixed_int(1, this.characterSet));
        payload.add( Proto.build_fixed_int(2, this.statusFlags));
        payload.add( Proto.build_fixed_str(13, ""));
        
        if (this.hasCapabilityFlag(Flags.CLIENT_SECURE_CONNECTION)) {
            payload.add( Proto.build_fixed_str(12, this.challenge2));
            payload.add( Proto.build_filler(1));
        }
        
        return payload;
    }
    
    public static Auth_Challenge loadFromPacket(byte[] packet) {
        Auth_Challenge obj = new Auth_Challenge();
        Proto proto = new Proto(packet, 3);
        
        obj.sequenceId = proto.get_fixed_int(1);
        obj.protocolVersion = proto.get_fixed_int(1);
        obj.serverVersion = proto.get_null_str();
        obj.connectionId = proto.get_fixed_int(4);
        obj.challenge1 = proto.get_fixed_str(8);
        proto.get_filler(1);
        obj.capabilityFlags = proto.get_fixed_int(2);
        obj.characterSet = proto.get_fixed_int(1);
        obj.statusFlags = proto.get_fixed_int(2);
        proto.get_filler(13);
        
        if (obj.hasCapabilityFlag(Flags.CLIENT_SECURE_CONNECTION)) {
            obj.challenge2 = proto.get_fixed_str(12);
            proto.get_filler(1);
        }
        
        return obj;
    }
}
