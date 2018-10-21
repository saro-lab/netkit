package me.saro.netkit;

import java.nio.ByteBuffer;

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
}
