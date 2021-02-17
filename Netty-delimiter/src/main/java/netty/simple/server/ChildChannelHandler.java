package netty.simple.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author by chow
 * @Description 子线程组处理器
 * @date 2021/2/17 上午2:37
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ByteBuf delimiter = Unpooled.copiedBuffer("^.".getBytes());
        // 添加分隔字符
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
        // 添加解码器
        socketChannel.pipeline().addLast(new StringDecoder());
        // 添加处理器
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }
}
