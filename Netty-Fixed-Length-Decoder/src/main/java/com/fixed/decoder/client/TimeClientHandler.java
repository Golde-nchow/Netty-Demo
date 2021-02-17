package com.fixed.decoder.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * @author by chow
 * @Description 客户端处理器
 * @date 2021/2/17 上午11:21
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("一二三四五六七八九十".getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String responseMsg;

        if (msg instanceof String) {
            // String 不可转换为 ByteBuf
            responseMsg = (String) msg;
        } else {
            // ByteBuf 比 java.nio.ByteBuffer更強大，可以直接获取可读的字节
            ByteBuf buf = (ByteBuf) msg;
            byte[] request = new byte[buf.readableBytes()];
            buf.readBytes(request);
            responseMsg = new String(request, StandardCharsets.UTF_8);
        }

        System.out.println("接收消息：" + responseMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("捕获异常：" + cause.getMessage());
        ctx.close();
    }
}
