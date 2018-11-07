package me.saro.netkit;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.saro.commons.function.ThrowableConsumer;
import me.saro.commons.function.ThrowablePredicate;

/**
 * Netkit Server Accepter
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitServerAccepter {
    NetkitServerAccepter() {
    }
    
    @Getter @Setter(value=AccessLevel.PACKAGE) int byteBufferUnitSize;
    @Getter @Setter(value=AccessLevel.PACKAGE) AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    @Getter @Setter(value=AccessLevel.PACKAGE) Map<String, NetkitConnection> connections;
    
    ThrowableConsumer<Throwable> throwableConsumer;
    ThrowablePredicate<AsynchronousSocketChannel> filter;

    public NetkitServerAccepter filter(ThrowablePredicate<AsynchronousSocketChannel> filter) {
        this.filter = filter;
        return this;
    }
    
    public NetkitServerAccepter error(ThrowableConsumer<Throwable> throwableConsumer) {
        this.throwableConsumer = throwableConsumer;
        return this;
    }
    
    public <T extends NetkitReader> T read(T t) {
        
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            
            @Override public void completed(AsynchronousSocketChannel channel, Void v) {
                try {
                    filter
                } catch (Exception e) {
                    try {
                        channel.close();
                    } catch (Exception e2)
                }
            }

            @Override public void failed(Throwable throwable, Void v) {
                error(throwable);
            }
        });
        
        return t;
    }
    
    private void error(Throwable throwable) {
        try {
            if (throwableConsumer != null) {
                throwableConsumer.accept(throwable);
            } else {
                log.error(throwable);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
