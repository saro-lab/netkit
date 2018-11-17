package me.saro.netkit;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Map;
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
    @Getter final Map<String, NetkitConnection> connections = new ConcurrentHashMap<>();

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
    NetkitServer() {
    }
    
    /**
     * accept
     * @return
     */
    public <T extends NetkitConnection> NetkitServerAccepter accept() {
        NetkitServerAccepter accept = new NetkitServerAccepter();
        
        accept.setByteBufferUnitSize(byteBufferUnitSize);
        accept.setAsynchronousServerSocketChannel(asynchronousServerSocketChannel);
        accept.setConnections(connections);
        
        return accept;
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
     * close
     */
    @Override
    public void close() throws IOException {
        connections.entrySet().parallelStream().forEach(e -> {
            try (e.getValue().channel) {
            } catch (Exception ex) {
            }
            connections.remove(e.getKey());
        });
        try (AsynchronousServerSocketChannel close = this.asynchronousServerSocketChannel) {
        }
    }
}
