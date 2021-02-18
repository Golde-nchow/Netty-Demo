package com.netty.protocol.server;

import com.netty.protocol.codec.NettyMessageDecoder;
import com.netty.protocol.codec.NettyMessageEncoder;
import com.netty.protocol.constant.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author by chow
 * @Description 私有栈服务端
 * @date 2021/2/18 上午11:03
 */
public class NettyServer {

    public void bind() throws Exception {
        // 配置服务端的NIO线程组
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();
        // 配置启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // Netty消息解码器，用于客户端接收响应消息
                        pipeline.addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                        // 编码器，用于向客户端发送消息
                        pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
                        // 读超时处理器，50s
                        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        // 服务端登陆处理器
                        pipeline.addLast("LoginAuthHandler", new LoginAuthResponseHandler());
                        // 服务端心跳处理器
                        pipeline.addLast("HeartBeatHandler", new HeartBeatResponseHandler());
                    }
                });
        System.out.println("Netty服务端 ==> " + NettyConstant.REMOTE_IP + ":" + NettyConstant.PORT);
        Channel channel = serverBootstrap.bind(NettyConstant.REMOTE_IP, NettyConstant.PORT).sync().channel();
        channel.closeFuture().sync();
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }

}
