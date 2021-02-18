package com.netty.protocol.model;

import lombok.Data;

/**
 * @author by chow
 * @Description 私有栈消息体
 * @date 2021/2/17 下午8:07
 */
@Data
public class NettyMessage {

    /**
     * 自定义头部信息
     */
    private Header header;

    /**
     * 消息内容
     */
    private Object body;

    @Override
    public String toString() {
        return "NettyMessage [header=" + header + "]";
    }
}
