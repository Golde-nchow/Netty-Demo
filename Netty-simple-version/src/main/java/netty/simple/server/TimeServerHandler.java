package netty.simple.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author by chow
 * @Description 服务端处理器
 * @date 2021/2/17 上午2:21
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy年MM月dd日 HH时mm分ss秒")
                .withZone(ZoneOffset.ofHours(8));

        // ByteBuf 比 java.nio.ByteBuffer更強大，可以直接获取可读的字节
        ByteBuf buf = (ByteBuf) msg;
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        String responseBody = new String(request, StandardCharsets.UTF_8);
        System.out.println("接收到的消息：" + responseBody);
        String currentTime = "我们之间的秘密".equals(responseBody) ? LocalDateTime.now().format(dateTimeFormatter) : "错误的消息";
        ByteBuf responseBuf = Unpooled.copiedBuffer(currentTime.getBytes());
        // write 只是把数据写到发送缓冲区中
        ctx.write(responseBuf);
    }

    /**
     * 通过 flush，把发送缓冲区内的数据发送给管道
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
