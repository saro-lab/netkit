package notuse.me.saro.netkit;

import java.lang.reflect.Method;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.saro.commons.function.ThrowableBiConsumer;
import me.saro.commons.function.ThrowableConsumer;
import me.saro.commons.function.ThrowablePredicate;
import notuse.me.saro.netkit.reader.NetkitReader;

/**
 * Netkit Server Accepter
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitServerAccepter {
    
    final NetkitServer server;
    ThrowableBiConsumer<NetkitConnection, Throwable> throwableConsumer = (c, t) -> {};
    ThrowablePredicate<AsynchronousSocketChannel> filter = e -> true;
    @Getter ThrowableConsumer<NetkitConnection> eof = e -> {};
    
    NetkitServerAccepter(NetkitServer netkitServer) {
        this.server = netkitServer;
    }

    public NetkitServerAccepter filter(ThrowablePredicate<AsynchronousSocketChannel> filter) {
        this.filter = filter;
        return this;
    }
    
    public NetkitServerAccepter error(ThrowableBiConsumer<NetkitConnection, Throwable> throwableConsumer) {
        this.throwableConsumer = throwableConsumer;
        return this;
    }
    
    public NetkitServerAccepter eof(ThrowableConsumer<NetkitConnection> eof) {
        this.eof = eof;
        return this;
    }
    
    public <T extends NetkitReader> T reader(T t) {
        
        final var assc = server.getAsynchronousServerSocketChannel();
        Method accept, bind;
        try {
            Class<?> clazz = t.getClass();
            accept = clazz.getDeclaredMethod("accept", NetkitConnection.class);
            accept.setAccessible(true);
            bind = clazz.getDeclaredMethod("bind", NetkitServer.class, NetkitServerAccepter.class);
            bind.setAccessible(true);
            bind.invoke(t, server, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // accept
        assc.accept(new NetkitConnection(), new CompletionHandler<AsynchronousSocketChannel, NetkitConnection>() {
            
            // get accept
            @Override public void completed(AsynchronousSocketChannel channel, NetkitConnection netkitConnection) {
                
                // establish next accept
                assc.accept(new NetkitConnection(), this);
                
                try {
                    server.addNetkitConnection(netkitConnection, channel);
                    accept.invoke(t, netkitConnection);
                } catch (Exception e) {
                    error(netkitConnection, e);
                    server.removeNetkitConnection(netkitConnection.id);
                    try (channel) {
                    } catch (Exception x) {
                    }
                }
            }

            // error
            @Override public void failed(Throwable throwable, NetkitConnection netkitConnection) {
                error(netkitConnection, throwable);
                server.removeNetkitConnection(netkitConnection.id);
            }
        });
        
        return t;
    }
    
    /**
     * catch error
     * @param throwable
     */
    private void error(NetkitConnection netkitConnection, Throwable throwable) {
        try {
            if (throwableConsumer != null) {
                throwableConsumer.accept(netkitConnection != null ? netkitConnection : new NetkitConnection(), throwable);
            } else {
                log.error(throwable);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
