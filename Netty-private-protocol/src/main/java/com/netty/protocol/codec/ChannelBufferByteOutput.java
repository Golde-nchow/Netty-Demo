package com.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * @author by chow
 * @Description 管道缓冲区输出
 * @date 2021/2/17 下午10:22
 */
@Data
public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        buffer.writeBytes(bytes, off, len);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
}
