package com.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * @author by chow
 * @Description Marshalling解码类
 * @date 2021/2/17 下午10:04
 */
public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnmarshaller();
    }

    protected Object decode(ByteBuf in) throws Exception {
        int size = in.readInt();
        ByteBuf readBuf = in.slice(in.readerIndex(), size);
        ByteInput input = new ChannelBufferByteInput(readBuf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            // 设置 readerIndex 到消息末尾
            in.readerIndex(in.readerIndex() + size);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }
}
