package com.netty.protocol.client;

import com.netty.protocol.codec.NettyMessageDecoder;
import com.netty.protocol.codec.NettyMessageEncoder;
import com.netty.protocol.constant.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author by chow
 * @Description 私有栈客户端
 * @date 2021/2/18 上午1:47
 */
public class NettyClient {

    /**
     * 线程池
     */
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    /**
     * 线程组
     */
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // Netty消息解码器，用于客户端接收响应消息
                            pipeline.addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            // 编码器，用于向服务端发送消息
                            pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
                            // 读超时处理器，50s
                            pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                            // 登陆处理器
                            pipeline.addLast("LoginAuthHandler", new LoginAuthReqHandler());
                            // 心跳处理器
                            pipeline.addLast("HeartBeatHandler", new HeartBeatRequestHandler());
                        }
                    });
            // 发起异步连接操作
            Channel channel = bootstrap.connect(
                    new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)
            ).sync().channel();
            channel.closeFuture().sync();
        } finally {
            // 释放完资源后，清空资源，再次发起重连操作
            // 如果不是发生异常，是不会走到这一步的
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect(NettyConstant.PORT, NettyConstant.REMOTE_IP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTE_IP);
    }
}
