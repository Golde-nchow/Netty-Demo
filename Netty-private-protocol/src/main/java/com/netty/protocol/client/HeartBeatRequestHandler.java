package com.netty.protocol.client;

import com.netty.protocol.constant.MessageType;
import com.netty.protocol.model.Header;
import com.netty.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author by chow
 * @Description 心跳检测-客户端
 * @date 2021/2/18 上午12:59
 */
public class HeartBeatRequestHandler extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            // 握手成功，则发送心跳消息
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatRequestHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            // 若是心跳的消息，直接打印日志即可
            System.out.println("客户端收到心跳响应消息：--> " + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 心跳检测定时任务
     */
    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext context;

        public HeartBeatTask(ChannelHandlerContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            NettyMessage heartBeatMessage = buildHeartBeat();
            System.out.println("客户端发送心跳检测：-->" + heartBeatMessage);
            context.writeAndFlush(heartBeatMessage);
        }
    }

    /**
     * 构建心跳消息
     */
    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQ.value());
        message.setHeader(header);
        return message;
    }

    /**
     * 若发生异常，终止心跳的发送
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
