package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

/**
 * @author by chow
 * @Description 多路复用时间服务器
 * @date 2021/1/26 下午11:29
 */
public class MultiplexerTimeServer implements Runnable {

    /**
     * 用于轮询的复用器
     */
    private Selector selector;

    /**
     * 通道
     */
    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            // 初始化多路复用器 Selector、SelectorSocketChannel
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            // 将 SelectorSocketChannel 设置为异步非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 初始化完成后，将 ServerSocketChannel 注册到 Selector 中，并监听 SeverSocketChannel 的 OP_ACCEPT 位
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器端口：" + port);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 设置停止位
     */
    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // 循环遍历 selectors，休眠时间为1s，selector 每隔一秒被唤醒
                selector.select(1000);
                // 当有处于就绪的 Channel 时，selector 将返回该 Channel 的 SelectionKey 集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                SelectionKey key;
                while (keyIterator.hasNext()) {
                    key = keyIterator.next();
                    keyIterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        key.cancel();
                        if (key.channel() != null) {
                            key.channel().close();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy年MM月dd日 HH时mm分ss秒")
                .withZone(ZoneOffset.ofHours(8));

        if (key.isValid()) {
            // 处理客户端请求
            if (key.isAcceptable()) {
                // 接收请求，并创建通道
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // 监听读事件
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                // 若为可读
                SocketChannel sc = (SocketChannel) key.channel();
                // 先分配1m大小的缓冲区，然后使用 read 方法不断地读取请求码流
                // 当读到的字节数 > 0，说明读到了数据；字节数 = 0，没有读到数据；字节数 < 0，链路已经关闭
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(byteBuffer);
                if (readBytes > 0) {
                    // 对消息进行解码
                    // 使用 flip 将缓冲区的 position 设置为 0，也就是置为开始位置
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    // 将缓冲区的数据读到 bytes 数组中.
                    byteBuffer.get(bytes);
                    // 创建字符串消息体
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("接收到的数据：" + body);
                    String currentTime = "查询订单".equals(body) ? LocalDateTime.now().format(dateTimeFormatter) : "错误的订单";
                    // 发送消息给客户端
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    // 释放资源
                    key.cancel();
                    sc.close();
                }
            }
        }

    }

    /**
     * 发送应答消息给客户端
     * @param channel 通道
     * @param response 应答消息
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        // 由于 SocketChannel 时异步非阻塞模式的，所以不保证一次性把字节发送完，会出现写半包的情况.
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
