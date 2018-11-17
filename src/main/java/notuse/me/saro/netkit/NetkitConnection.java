package notuse.me.saro.netkit;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
    
    public int write(byte[] array, int offset, int length) throws InterruptedException, ExecutionException {
        return channel.write(ByteBuffer.wrap(array, offset, length)).get();
    }
    
    public int write(byte[] array) throws InterruptedException, ExecutionException {
        return channel.write(ByteBuffer.wrap(array)).get();
    }
}
