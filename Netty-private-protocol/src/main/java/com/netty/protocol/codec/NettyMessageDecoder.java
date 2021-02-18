package com.netty.protocol.codec;

import com.netty.protocol.model.Header;
import com.netty.protocol.model.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author by chow
 * @Description Netty消息解码类
 * @date 2021/2/17 下午11:01
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionId(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if (size > 0) {
            HashMap<String, Object> attachment = new HashMap<>(size);
             int keySize;
             byte[] keyArray;
             String key;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                key = new String(keyArray, StandardCharsets.UTF_8);
                attachment.put(key, marshallingDecoder.decode(frame));
            }

            header.setAttachment(attachment);
        }

        if (frame.readableBytes() > 4) {
            nettyMessage.setBody(marshallingDecoder.decode(frame));
        }
        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
