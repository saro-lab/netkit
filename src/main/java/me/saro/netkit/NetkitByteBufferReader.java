package me.saro.netkit;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Netkit Reader
 * @author      PARK Yong Seo
 * @since       0.0
 */
@Log4j2
public class NetkitByteBufferReader {
    public void accept() {
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
}
