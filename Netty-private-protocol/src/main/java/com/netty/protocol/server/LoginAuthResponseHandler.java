package com.netty.protocol.server;

import com.netty.protocol.constant.MessageType;
import com.netty.protocol.model.Header;
import com.netty.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by chow
 * @Description 登陆响应处理器
 * @date 2021/2/17 下午11:42
 */
public class LoginAuthResponseHandler extends ChannelHandlerAdapter {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    private String[] whiteList = {"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 如果是握手请求消息
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String remoteNode = ctx.channel().remoteAddress().toString();
            NettyMessage loginResponse;
            // 判断重复登陆
            if (nodeCheck.containsKey(remoteNode)) {
                loginResponse = buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String remoteIp = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String ip : whiteList) {
                    if (ip.equals(remoteIp)) {
                        isOk = true;
                        break;
                    }
                }
                loginResponse = isOk ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (isOk) {
                    nodeCheck.put(remoteNode, true);
                }
            }
            System.out.println("登陆响应消息：" + loginResponse);
            ctx.writeAndFlush(loginResponse);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 构建响应消息
     */
    private NettyMessage buildResponse(byte result) {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(result);
        return nettyMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 删除缓存
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
