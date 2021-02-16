package com.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author by chow
 * @Description 处理接受客户端连接完成的处理器
 * @date 2021/2/16 下午11:27
 */
public class AcceptCompetitionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    /**
     * 连接完成后的操作
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        // 连接完成后，再调用监听方法，来处理其他客户端的连接操作
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        // 读操作的缓冲区仍然是1MB，读取异步处理器的内容.
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    /**
     * 连接失败的操作
     */
    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
