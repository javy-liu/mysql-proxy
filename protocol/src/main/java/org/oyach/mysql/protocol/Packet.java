package org.oyach.mysql.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 握手数据包
 *
 * 客户端请求的时候，服务器返回数据
 *
 * @author oyach
 * @since 0.0.1
 */
public abstract class Packet {
    private long sequenceId;

    public abstract ArrayList<byte[]> getPayload();


    /**
     * 读取握手数据包
     *
     * 握手包格式
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] read_packet(InputStream in) throws IOException {
        int b = 0;
        int size = 0;
        byte[] packet = new byte[3];

        // Read size (3)
        int offset = 0;
        int target = 3;
        do {
            b = in.read(packet, offset, (target - offset));
            if (b == -1) {
                throw new IOException();
            }
            offset += b;
        } while (offset != target);

        size = Packet.getSize(packet);

        byte[] packet_tmp = new byte[size+4];
        System.arraycopy(packet, 0, packet_tmp, 0, 3);
        packet = packet_tmp;
        packet_tmp = null;

        target = packet.length;
        do {
            b = in.read(packet, offset, (target - offset));
            if (b == -1) {
                throw new IOException();
            }
            offset += b;
        } while (offset != target);

        return packet;

    }


    public static int getSize(byte[] packet) {
        int size = (int) new Proto(packet).get_fixed_int(3);
        return size;
    }

}
