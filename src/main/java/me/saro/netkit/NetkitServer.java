package me.saro.netkit;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Netkit Server
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitServer implements Closeable {

    @Getter @Setter(value=AccessLevel.PACKAGE) int port = -1;
    @Getter @Setter(value=AccessLevel.PACKAGE) int byteBufferUnitSize;
    @Getter @Setter(value=AccessLevel.PACKAGE) AsynchronousChannelGroup asynchronousChannelGroup;
    @Getter @Setter(value=AccessLevel.PACKAGE) AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    @Getter final Map<String, NetkitConnection> connections = new ConcurrentHashMap<>();

    public static NetkitServer run(int port, int byteBufferUnitSize, AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        NetkitServer server = new NetkitServer();
        server.setAsynchronousChannelGroup(asynchronousChannelGroup);
        server.setByteBufferUnitSize(byteBufferUnitSize);
        server.setPort(port);
        server.run();
        return server;
    }

    public static NetkitServer run(int port, int byteBufferUnitSize) throws IOException {
        return run(port, byteBufferUnitSize, null);
    }

    public static NetkitServer run(int port) throws IOException {
        return run(port, 8192, null);
    }

    NetkitServer() {
    }

    void validator() {
        // each of a connection byte buffer size
        if (byteBufferUnitSize >= 32) {
            log.info("each of a connection byte buffer size is " + byteBufferUnitSize + " bytes");
        } else {
            throw new IllegalArgumentException("byteBufferUnitSize min value is 32");
        }
        
        // asynchronous channel group
        log.info("using asynchronous channel group : " + (asynchronousChannelGroup != null));
    }

    void run() throws IOException {

        System.out.println(
                    "________________________________________________________________\n\n"
                +   " S  A  R  O\n\n"
                +   " N E T K I T    S E R V E R\n\n"
                +   " https://github.com/saro-lab/netkit\n"
                + "________________________________________________________________\n");

        validator();
        
        asynchronousServerSocketChannel = asynchronousChannelGroup != null 
                ? AsynchronousServerSocketChannel.open(asynchronousChannelGroup) 
                : AsynchronousServerSocketChannel.open();
        
        log.info("open the asynchronous server socket channel");
        
        asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        
        log.info("bind port " + port);
    }
    
    public <T extends NetkitConnection> NetkitReader accept(NetkitAccept<T> accept) {
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @SneakyThrows
            public void completed(final AsynchronousSocketChannel channel, Void v) {
                if (!asynchronousServerSocketChannel.isOpen()) {
                    return;
                }
            }
            public void failed(Throwable error, Void v) {
                throw new RuntimeException(error);
            }
        });
    }
    
    public static interface NetkitAccept<T> {
        
    }
    
    @Override
    public void close() throws IOException {
        connections.forEach((k , v) -> {

        });
        try (AsynchronousServerSocketChannel close = this.asynchronousServerSocketChannel) {
        }
    }

}
