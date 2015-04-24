package com.github.jmpjct.mysql.proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class Auth_Challenge extends Packet {
    public long protocolVersion = 0x0a;
    public String serverVersion = "";
    public long connectionId = 0;
    //auth_plugin_data_part_1 
    public String challenge1 = "";
    //test result 1111011111111111 (63487)
    public long capabilityFlags = Flags.CLIENT_PROTOCOL_41;
    //33 utf8_general_ci
    //http://dev.mysql.com/doc/refman/5.1/en/charset.html
    //http://dev.mysql.com/doc/internals/en/character-set.html#packet-Protocol::CharacterSet
    public long characterSet = 33;
    //in test SERVER_STATUS_AUTOCOMMIT =2
    public long statusFlags = 0;
    public long capabilityFlags2 = Flags.CLIENT_PROTOCOL_41;
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
        //auth-plugin-data-part-1
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
        int point = obj.serverVersion.indexOf(46);

        /*int serverMajorVersion = 0;
        int serverMinorVersion = 0;
        int serverSubMinorVersion = 0;
        if (point != -1) 
        {
          try {
            int n = Integer.parseInt(obj.serverVersion.substring(0, point));
            serverMajorVersion = n;
          } catch (NumberFormatException NFE1)
          {
          }
          String remaining = obj.serverVersion.substring(point + 1, obj.serverVersion.length());
          point = remaining.indexOf(46);

          if (point != -1) 
          {
            try {
              int n = Integer.parseInt(remaining.substring(0, point));
              serverMinorVersion = n;
            }catch (NumberFormatException nfe)
            {
            }
            remaining = remaining.substring(point + 1, remaining.length());

            int pos = 0;

            while ((pos < remaining.length()) && 
              (remaining.charAt(pos) >= '0')) { if (remaining.charAt(pos) > '9')
              {
                break;
              }

              ++pos;
            }
            try
            {
              int n = Integer.parseInt(remaining.substring(0, pos));
              serverSubMinorVersion = n;
            } catch (NumberFormatException nfe)
            {
            }
          }
        }*/
//        if (versionMeetsMinimum(4, 0, 8)) {
//          this.maxThreeBytes = 16777215;
//          this.useNewLargePackets = true;
//        } else {
//          this.maxThreeBytes = 16581375;
//          this.useNewLargePackets = false;
//        }
//        this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
//        this.colDecimalNeedsBump = (!(versionMeetsMinimum(3, 23, 15)));
//        this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
        
        obj.connectionId = proto.get_fixed_int(4);
        //auth_plugin_data_part_1 (string.fix_len) -- [len=8] first 8 bytes of the auth-plugin data
        obj.challenge1 = proto.get_fixed_str(8);
        proto.get_filler(1);
//      if (buf.getPosition() < buf.getBufLength()) {
//            this.serverCapabilities = buf.readInt();
//      }
        if(proto.offset < proto.packet.length)
        {
        	obj.capabilityFlags = proto.get_fixed_int(2);
        }
        //if (versionMeetsMinimum(4, 1, 1)) {
        obj.characterSet = proto.get_fixed_int(1);
        obj.statusFlags = proto.get_fixed_int(2);
        int capabilities  = (int)proto.get_fixed_int(2);
        System.out.println("capabilities "+capabilities + " "+obj.capabilityFlags);
        int short1 = getUShort(new byte[] {-1,-9});
        System.out.println("litt "+short1);
        
        long auth_plugin_data_len  = proto.get_fixed_int(1);
        if( (capabilities  & Flags.CLIENT_PLUGIN_AUTH) > 0)
        {
        	System.out.println(auth_plugin_data_len );
        }
        //proto.get_filler(13);
        proto.get_filler(10);
        
        if( (capabilities  & Flags.CLIENT_SECURE_CONNECTION) > 0)
        {
        	System.out.println(auth_plugin_data_len );
        }
		
        if (obj.hasCapabilityFlag(Flags.CLIENT_SECURE_CONNECTION)) {
            obj.challenge2 = proto.get_fixed_str(12);
            
            String a = proto.get_eop_str();
            //_mysql_native_password
            System.out.println(a);
            proto.get_filler(1);
        }
        
        return obj;
    }
    
    public static int getUShort(byte[] _byte)
	{
		//2byte
		return (int) (_byte[0] & 0xFF) | ((_byte[1] & 0xFF)<<8);
	}
}
