package com.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author by chow
 * @Description 读操作完成处理器
 * @date 2021/2/16 下午11:39
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel asynchronousSocketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy年MM月dd日 HH时mm分ss秒")
                .withZone(ZoneOffset.ofHours(8));

        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String response = new String(body, StandardCharsets.UTF_8);
        System.out.println("服务器收到消息：" + response);
        String currentTime = "接收的消息".equals(response) ? LocalDateTime.now().format(dateTimeFormatter) : "错误的消息";
        System.out.println("接收时间：" + currentTime);
        doWrite(currentTime);
    }

    private void doWrite(String response) {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            asynchronousSocketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // 如果没有发送完成，则继续发送
                    if (writeBuffer.hasRemaining()) {
                        asynchronousSocketChannel.write(attachment, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        asynchronousSocketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
