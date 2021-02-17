package com.fixed.decoder.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author by chow
 * @Description 客户端
 * @date 2021/2/17 上午11:17
 */
public class TimeClient {

     public void connect(int port, String host) throws InterruptedException {
         // 客户端线程组
         NioEventLoopGroup group = new NioEventLoopGroup();
         try {
             // 启动类
             Bootstrap bootstrap = new Bootstrap();
             bootstrap.group(group).channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             // 设置固定长度解码器
                             // 一个中文字占3个长度，10个中文，若小于或大于30，会出现部分乱码
                             // 可以用 wireShark 查看具体情况
                             // 如果要动态判断那就真的是麻烦
                             ch.pipeline().addLast(new FixedLengthFrameDecoder(30));
                             // 设置解码器
                             ch.pipeline().addLast(new StringDecoder());
                             // 设置处理器
                             ch.pipeline().addLast(new TimeClientHandler());
                         }
                     });
             // 发起异步连接
             ChannelFuture future = bootstrap.connect(host, port).sync();
             // 等待对方关闭连接
             future.channel().closeFuture().sync();

         } finally {
             group.shutdownGracefully();
         }
     }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimeClient().connect(port, "127.0.0.1");
    }

}
