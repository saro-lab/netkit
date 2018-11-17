package notuse.me.saro.netkit.reader;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

import lombok.extern.log4j.Log4j2;
import me.saro.commons.function.ThrowableConsumer;
import me.saro.commons.function.ThrowableTriConsumer;
import notuse.me.saro.netkit.Netkit;
import notuse.me.saro.netkit.NetkitConnection;
import notuse.me.saro.netkit.NetkitServer;
import notuse.me.saro.netkit.NetkitServerAccepter;

/**
 * Netkit Reader
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitBytesReader extends NetkitReader {
    
    NetkitServer server;
    NetkitServerAccepter accepter;
    final ThrowableTriConsumer<Integer, byte[], NetkitConnection> read;
    final BiConsumer<NetkitConnection, Throwable> error;
    
    public NetkitBytesReader(ThrowableTriConsumer<Integer, byte[], NetkitConnection> read, BiConsumer<NetkitConnection, Throwable> error) {
        this.read = read;
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
                    System.out.println("사이즈 : " + size);
                    if (size > -1) {
                        if (size > 0) {
                            byteBuffer.flip();
                            byte[] bytes = Netkit.safeArray(byteBuffer);
                            read.accept(size, bytes, connection);
                        }
                        channel.read(byteBuffer, connection, this);
                    }
                    try {
                        accepter.getEof().accept(connection);
                        server.removeNetkitConnection(connection.getId());
                    } catch (Exception e) {
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
