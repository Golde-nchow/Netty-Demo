package netty.simple.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author by chow
 * @Description 子线程组处理器
 * @date 2021/2/17 上午2:37
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }
}
