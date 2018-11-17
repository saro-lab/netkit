package me.saro.netkit;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
    @Getter Map<Long, NetkitConnection> connections = new ConcurrentHashMap<>();
    final Thread gcThread = new Thread(this::garbageCollection);

    /**
     * bind server
     * @param port
     * @param byteBufferUnitSize
     * @param asynchronousChannelGroup
     * @return
     * @throws IOException
     */
    public static NetkitServer bind(int port, int byteBufferUnitSize, AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        NetkitServer server = new NetkitServer();
        server.setAsynchronousChannelGroup(asynchronousChannelGroup);
        server.setByteBufferUnitSize(byteBufferUnitSize);
        server.setPort(port);
        server.run();
        return server;
    }

    /**
     * bind server
     * @param port
     * @param byteBufferUnitSize
     * @return
     * @throws IOException
     */
    public static NetkitServer bind(int port, int byteBufferUnitSize) throws IOException {
        return bind(port, byteBufferUnitSize, null);
    }

    /**
     * bind server
     * @param port
     * @return
     * @throws IOException
     */
    public static NetkitServer bind(int port) throws IOException {
        return bind(port, 8192, null);
    }

    /**
     * using static bind
     */
    private NetkitServer() {
    }
    
    /**
     * accept
     * @return
     */
    public <T extends NetkitConnection> NetkitServerAccepter accept() {
        return new NetkitServerAccepter(this);
    }
    
    /**
     * run server
     * @throws IOException
     */
    private void run() throws IOException {
        
        welcome();
        validator();
        
        asynchronousServerSocketChannel = asynchronousChannelGroup != null 
                ? AsynchronousServerSocketChannel.open(asynchronousChannelGroup) 
                : AsynchronousServerSocketChannel.open();
        
        log.info("open the asynchronous server socket channel");
        
        asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        
        log.info("bind port " + port);
        
        gcThread.start();
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
        if (byteBufferUnitSize >= 32) {
            log.info("each of a connection byte buffer size is " + byteBufferUnitSize + " bytes");
        } else {
            throw new IllegalArgumentException("byteBufferUnitSize min value is 32");
        }
        
        // asynchronous channel group
        log.info("using asynchronous channel group : " + (asynchronousChannelGroup != null));
    }
    
    /**
     * add connection
     * @param conn
     * @param channel
     * @throws IOException
     */
    void addNetkitConnection(NetkitConnection conn, AsynchronousSocketChannel channel) throws IOException {
       for (int i = 0 ; i < 10000 ; i++) {
            long id = (long)(Math.random() * Long.MAX_VALUE);
            if (connections.putIfAbsent(id, conn).getId() == -1L) {
                conn.id = id;
                conn.channel = channel;
                return;
            }
        }
        throw new IOException("fail new connection");
    }
    
    /**
     * Garbage Collection
     */
    private void garbageCollection() {
        log.info("start netkit gc");
        int error = 0;
        while (connections != null) {
            try {
                Thread.sleep(60000);
                log.info("execute netkit gc");
                connections.forEach((k, v) -> {
                    try {
                        if (Optional.of(v).map(e -> e.isOpen()).orElse(false)) {
                            return;
                        }
                    } catch (Exception e) {
                    }
                    connections.remove(k);
                });
                error = 0;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (error++ > 1000) {
                    throw new RuntimeException("server gc thread error!!!");
                }
            }
        }
    }
    
    /**
     * close
     */
    @Override
    public void close() throws IOException {
//        var connections = this.connections;
//        this.connections = null;
//        connections.values().forEach(ThrowableConsumer.runtime(e -> e.getChannel().close()));
//        connections.clear();
        try (AsynchronousServerSocketChannel close = this.asynchronousServerSocketChannel) {
        }
    }
}
