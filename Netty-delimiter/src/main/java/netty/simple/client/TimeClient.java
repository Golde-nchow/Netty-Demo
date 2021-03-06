package netty.simple.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author by chow
 * @Description 客户端
 * @date 2021/2/17 上午11:17
 */
public class TimeClient {

     public void connect(int port, String host) throws InterruptedException {
         // 客户端线程组
         NioEventLoopGroup group = new NioEventLoopGroup();
         try {
             // 启动类
             Bootstrap bootstrap = new Bootstrap();
             bootstrap.group(group).channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             // 设置分隔符
                             ByteBuf delimiter = Unpooled.copiedBuffer("^.".getBytes());
                             ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                             // 设置解码器
                             ch.pipeline().addLast(new StringDecoder());
                             // 设置处理器
                             ch.pipeline().addLast(new TimeClientHandler());
                         }
                     });
             // 发起异步连接
             ChannelFuture future = bootstrap.connect(host, port).sync();
             // 等待对方关闭连接
             future.channel().closeFuture().sync();

         } finally {
             group.shutdownGracefully();
         }
     }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new TimeClient().connect(port, "127.0.0.1");
    }

}
