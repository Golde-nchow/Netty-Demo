package netty.simple.client;

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
    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] msgToSend = "我们之间的秘密".getBytes();
        firstMessage = Unpooled.buffer(msgToSend.length);
        firstMessage.writeBytes(msgToSend);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] responseMsg = new byte[buf.readableBytes()];
        buf.readBytes(responseMsg);
        String body = new String(responseMsg, StandardCharsets.UTF_8);
        System.out.println("接收时间：" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("捕获异常：" + cause.getMessage());
        ctx.close();
    }
}
