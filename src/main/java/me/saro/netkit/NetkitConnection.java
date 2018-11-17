package me.saro.netkit;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Netkit Connection
 * @author      PARK Yong Seo
 * @since       0.0
 */
@NoArgsConstructor
public class NetkitConnection {
    @Getter long id = -1L;
    @Getter AsynchronousSocketChannel channel;
    
    public boolean isOpen() {
        return id != -1 && Optional.of(channel).map(e -> e.isOpen()).orElse(false);
    }
}
