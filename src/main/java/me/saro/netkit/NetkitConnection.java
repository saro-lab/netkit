package me.saro.netkit;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Netkit Connection
 * @author      PARK Yong Seo
 * @since       0.0
 */
@AllArgsConstructor
public class NetkitConnection {
    @Getter final AsynchronousSocketChannel channel;
    @Getter final ByteBuffer buffer;
    ByteArrayOutputStream baos;
}
