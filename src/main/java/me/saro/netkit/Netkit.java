package me.saro.netkit;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Netkit
 * @author      PARK Yong Seo
 * @since       0.0
 */
public class Netkit {
    
    /**
     * this class is only supported static methods 
     */
    private Netkit() {
    }
    
    /**
     * safe allocateDirect
     * @return
     */
    public static byte[] safeArray(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return buffer.array();
        }
        
        byte[] ba = new byte[buffer.remaining()];
        buffer.get(ba);
        buffer.compact();
        return ba;
    }
    
    @Deprecated // NEED TEST
    public static byte[] safeCopyArray(ByteBuffer buffer, byte[] bytes) {
        return safeCopyArray(buffer, bytes, 0, buffer.remaining());
    }
    
    @Deprecated // NEED TEST
    public static byte[] safeCopyArray(ByteBuffer buffer, byte[] bytes, int offset, int length) {
        if (buffer.hasArray()) {
            return Arrays.copyOf(buffer.array(), length);
        }
        
        buffer.get(bytes, offset, length);
        buffer.compact();
        return bytes;
    }
    
    
}
