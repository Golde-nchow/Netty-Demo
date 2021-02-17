package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author by chow
 * @Description WebSocket服务端
 * @date 2021/2/17 下午5:03
 */
public class WebSocketServer {

    public static void main(String[] args) {
        int port = 8080;
        new WebSocketServer().run(port);
    }

    public void run(int port) {
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    // HTTP编码类
                    pipeline.addLast("http-codec", new HttpServerCodec());
                    // 聚合器
                    // HTTP消息是分开传递的，所以该聚合器可以将信息进行组合
                    pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                    // 使浏览器和服务端支持 WebSocket 通信
                    pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                    // 处理器
                    pipeline.addLast("handler", new WebSocketServerHandler());
                }
            });

            Channel channel = serverBootstrap.bind(port).sync().channel();
            System.out.println("服务端端口：" + port);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

}
