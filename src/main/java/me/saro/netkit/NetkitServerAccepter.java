package me.saro.netkit;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Map;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.saro.commons.function.ThrowableConsumer;

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

    public NetkitServerAccepter prev(Function<String, String> prev) {
        return this;
    }
    
    public NetkitServerAccepter next(Function<String, String> prev) {
        return this;
    }
    
    public NetkitServerAccepter error(ThrowableConsumer<Throwable> throwableConsumer) {
        return this;
    }
    
    public <T extends NetkitReader> T read(T t) {
        return t;
    }
}
