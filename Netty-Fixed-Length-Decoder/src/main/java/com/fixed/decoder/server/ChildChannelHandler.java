package com.fixed.decoder.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author by chow
 * @Description 子线程组处理器
 * @date 2021/2/17 上午2:37
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 添加固定长度分隔
        // 一个中文字占3个长度，10个中文，若小于或大于30，会出现部分乱码
        // 可以用 wireShark 查看具体情况
        // 如果要动态判断那就真的是麻烦
        socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(30));
        // 添加解码器
        socketChannel.pipeline().addLast(new StringDecoder());
        // 添加处理器
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }
}
