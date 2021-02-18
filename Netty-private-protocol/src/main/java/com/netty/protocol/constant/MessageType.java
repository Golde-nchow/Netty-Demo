package com.netty.protocol.constant;

/**
 * @author by chow
 * @Description 消息类型
 * @date 2021/2/17 下午8:13
 */
public enum MessageType {

    /**
     * 业务请求
     */
    SERVICE_REQ((byte) 0),

    /**
     * 业务响应
     */
    SERVICE_RESP((byte) 1),

    /**
     * 业务类型：既是请求又是响应
     */
    ONE_WAY((byte) 2),

    /**
     * 登陆请求
     */
    LOGIN_REQ((byte) 3),

    /**
     * 登陆响应
     */
    LOGIN_RESP((byte) 4),

    /**
     * 心跳请求
     */
    HEARTBEAT_REQ((byte) 5),

    /**
     * 心跳响应
     */
    HEARTBEAT_RESP((byte) 6);

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

}
