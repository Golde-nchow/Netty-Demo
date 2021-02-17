package netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;

/**
 * @author by chow
 * @Description WebSocket处理器
 * @date 2021/2/17 下午5:19
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class.getName());

    /**
     * 三次握手类
     */
    private WebSocketServerHandshaker handshaker;

    /**
     * 一旦收到消息，就调用该方法
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            // 传统的HTTP连接
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            // WebSocket连接
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 若读操作完成，则发送到管道
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 处理 HTTP 传统请求
     */
    private void handleHttpRequest(ChannelHandlerContext context, FullHttpRequest request) {
        // 如果HTTP解码失败，返回异常
        // 若是 WebSocket 请求，则 header 会带上
        // Upgrade:websocket；
        // Connection:upgrade；
        // Sec-WebSocket-Key:xxx
        // Sec-WebSocket-protocol:xxx
        // Sec-WebSocket-Version:xxx
        if (!request.decoderResult().isSuccess() || (!"websocket".contentEquals(request.headers().get("Upgrade")))) {
            sendHttpResponse(context, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构造握手响应返回
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory =
                new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = webSocketServerHandshakerFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        } else {
            handshaker.handshake(context.channel(), request);
        }

    }

    /**
     * 处理 WebSocket 连接
     */
    private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        // 判断是否关闭链路的指令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            handshaker.close(context.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
            return;
        }

        // 判断是否是ping消息
        if (webSocketFrame instanceof PingWebSocketFrame) {
            context.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }

        // 仅支持文本消息
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s is not supported", webSocketFrame.getClass().getName()));
        }

        // 返回应答消息
        String requestMsg = ((TextWebSocketFrame) webSocketFrame).text();
        logger.fine(String.format("%s 收到 %s", context.channel(), requestMsg));

        context.channel().write(new TextWebSocketFrame("收到消息：" + requestMsg));
    }

    /**
     * 设置HTTP返回参数
     */
    private static void sendHttpResponse(ChannelHandlerContext context,
                                         FullHttpRequest request,
                                         FullHttpResponse response) {
        // 返回给客户端
        if (response.status().code() != 200) {
            ByteBuf writeBuf = Unpooled.copiedBuffer(response.status().toString(), StandardCharsets.UTF_8);
            response.content().writeBytes(writeBuf);
            writeBuf.release();
            setContentLength(response, response.content().readableBytes());
        }

        // 如果是非 Keep-Alive，关闭连接
        ChannelFuture future = context.channel().writeAndFlush(response);
        if (!isKeepAlive(request) || response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
