package com.fixed.decoder.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author by chow
 * @Description 服务器
 * @date 2021/2/17 上午2:12
 */
public class TimeServer {

    public void bind(int port) throws InterruptedException {
        // 服务端线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 配置服务端启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            // 綁定端口
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 等待对方关闭连接
            future.channel().closeFuture().sync();
        } finally {
            // 优雅地释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        System.out.println("服务端监听端口：" + port);
        new TimeServer().bind(port);
    }

}
