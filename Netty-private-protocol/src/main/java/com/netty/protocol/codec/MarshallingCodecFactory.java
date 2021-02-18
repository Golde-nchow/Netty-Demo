package com.netty.protocol.codec;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * @author by chow
 * @Description Marshalling编码工厂
 * @date 2021/2/17 下午10:03
 */
public final class MarshallingCodecFactory {

    /**
     * 返回序列化类
     */
    public static Marshaller buildMarshalling() throws IOException {
        // serial 表明创建的是 Java 序列化工厂
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        // 配置类
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return marshallerFactory.createMarshaller(configuration);
    }

    /**
     * 返回反序列化类
     */
    public static Unmarshaller buildUnmarshaller() throws IOException {
        // serial 表明创建的是 Java 序列化工厂
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        // 配置类
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return marshallerFactory.createUnmarshaller(configuration);
    }

}
