package me.saro.netkit;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NetkitServer implements Closeable {
    
    int port;
    int bufferSize;
    AsynchronousServerSocketChannel socketChannel;
    List<NetkitSocketChannel> channels = Collections.synchronizedList(new LinkedList<>());
    
    private NetkitServer() {
    }
    
    public static NetkitServer bind(int port, int bufferSize) throws IOException {
        NetkitServer server = new NetkitServer();
        server.port = port;
        server.bufferSize = bufferSize;
        
        server.welcome();
        server.validator();
        
        server.socketChannel = AsynchronousServerSocketChannel.open();
        log.info("open the asynchronous server socket channel");
        
        server.socketChannel.bind(new InetSocketAddress(port));
        log.info("bind port " + port);
        
        return server;
    }
    
    public static NetkitServer bind(int port) throws IOException {
        return bind(port, 8192);
    }
    
    NetkitSocketChannel addChannels(AsynchronousSocketChannel channel) throws IOException {
        var ch = new NetkitSocketChannel(channel);
        channels.add(ch);
        return ch;
    }
    
    void removeChannel(NetkitSocketChannel channel) {
        channel.close();
        channels.remove(channel);
    }
    
    void collectChannels() {
        channels.parallelStream().filter(e -> !e.isOpen()).forEach(channels::remove);
    }
    
    /**
     * welcome
     */
    private void welcome() {
        System.out.println(
                "________________________________________________________________\n\n"
            +   " S  A  R  O\n\n"
            +   " N E T K I T    S E R V E R\n\n"
            +   " https://github.com/saro-lab/netkit\n"
            + "________________________________________________________________\n");
    }

    /**
     * validator
     */
    private void validator() {
        // each of a connection byte buffer size
        if (bufferSize >= 32) {
            log.info("each of a connection byte buffer size is " + bufferSize + " bytes");
        } else {
            throw new IllegalArgumentException("byteBufferUnitSize min value is 32");
        }
    }

    @Override
    public void close() throws IOException {
        try {
            socketChannel.close();
        } catch(Exception e) {
        }
        
        try {
            socketChannel.close();
        } catch(Exception e) {
        }
    }
}
