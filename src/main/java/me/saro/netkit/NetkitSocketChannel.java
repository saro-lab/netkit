package me.saro.netkit;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class NetkitSocketChannel implements Closeable {
    
    final AsynchronousSocketChannel channel;
    final int port;
    
    NetkitSocketChannel(AsynchronousSocketChannel channel) throws IOException {
        this.channel = channel;
        port = ((InetSocketAddress)(channel.getRemoteAddress())).getPort();
    }
    
    @Override
    public int hashCode() {
        return port;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof NetkitSocketChannel && obj.hashCode() == hashCode();
    }
    
    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void close() {
        try (channel) {
        } catch (Exception e) {
        }
    }
}
