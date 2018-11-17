package me.saro.netkit.reader;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

import lombok.extern.log4j.Log4j2;
import me.saro.commons.function.ThrowableConsumer;
import me.saro.commons.function.ThrowableTriConsumer;
import me.saro.netkit.NetkitConnection;
import me.saro.netkit.NetkitServer;
import me.saro.netkit.NetkitServerAccepter;

/**
 * Netkit Reader
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitByteBufferReader extends NetkitReader {
    
    NetkitServer server;
    NetkitServerAccepter accepter;
    final ThrowableTriConsumer<Integer, ByteBuffer, NetkitConnection> read;
    final ThrowableConsumer<NetkitConnection> eof;
    final BiConsumer<NetkitConnection, Throwable> error;
    
    public NetkitByteBufferReader(ThrowableTriConsumer<Integer, ByteBuffer, NetkitConnection> read, ThrowableConsumer<NetkitConnection> eof, BiConsumer<NetkitConnection, Throwable> error) {
        this.read = read;
        this.eof = eof;
        this.error = error;
    }
    
    @Override
    protected void bind(NetkitServer server, NetkitServerAccepter accepter) {
        this.server = server;
        this.accepter = accepter;
    }

    @Override
    protected void accept(NetkitConnection connection) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(server.getByteBufferUnitSize());
        var channel = connection.getChannel();
        channel.read(byteBuffer, connection, new CompletionHandler<Integer, NetkitConnection>() {
            @Override
            public void completed(Integer size, NetkitConnection connection) {
                try {
                    if (size > -1) {
                        read.accept(size, byteBuffer, connection);
                        channel.read(byteBuffer, connection, this);
                    }
                    try {
                        eof.accept(connection);
                    } catch (Exception e) {
//                        try {
//                            connection.getChannel().close();
//                        } catch (Exception e2) {
//                            
//                        }
                    }
                } catch (Exception e) {
                    failed(e, connection);
                    channel.read(byteBuffer, connection, this);
                }
            }
            @Override
            public void failed(Throwable exc, NetkitConnection connection) {
                try {
                    error.accept(connection, exc);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }
}
