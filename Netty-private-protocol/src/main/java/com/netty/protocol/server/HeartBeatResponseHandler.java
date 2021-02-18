package com.netty.protocol.server;

import com.netty.protocol.constant.MessageType;
import com.netty.protocol.model.Header;
import com.netty.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author by chow
 * @Description 服务端心跳检测处理器
 * @date 2021/2/18 上午1:14
 */
public class HeartBeatResponseHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            // 若是心跳的消息，直接打印日志即可
            System.out.println("服务端收到心跳消息：==> " + message);
            NettyMessage heartBeatResponse = buildHeartBeat();
            System.out.println("发送服务端的心跳响应消息 ==> " + heartBeatResponse);
            ctx.writeAndFlush(heartBeatResponse);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 构建心跳消息
     */
    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }
}
