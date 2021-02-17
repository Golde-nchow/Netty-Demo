package com.fixed.decoder.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * @author by chow
 * @Description 服务端处理器
 * @date 2021/2/17 上午2:21
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String responseBody;

        if (msg instanceof String) {
            // String 不可转换为 ByteBuf
            responseBody = (String) msg;
        } else {
            // ByteBuf 比 java.nio.ByteBuffer更強大，可以直接获取可读的字节
            ByteBuf buf = (ByteBuf) msg;
            byte[] request = new byte[buf.readableBytes()];
            buf.readBytes(request);
            responseBody = new String(request, StandardCharsets.UTF_8);
        }


        // 对响应消息进行处理，让客户端进行分隔
        System.out.println("接收到的消息：" + responseBody);

        // write 只是把数据写到发送缓冲区中
        ByteBuf responseBuf = Unpooled.copiedBuffer(responseBody.getBytes());
        ctx.writeAndFlush(responseBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
