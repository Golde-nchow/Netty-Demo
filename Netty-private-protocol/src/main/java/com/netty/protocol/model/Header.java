package com.netty.protocol.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by chow
 * @Description 私有栈头部信息
 * @date 2021/2/17 下午8:05
 */
@Data
public class Header {

    private int crcCode = 0xabef0101;

    /**
     * 消息长度
     */
    private int length;

    /**
     * 会话ID
     */
    private long sessionId;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 消息优先级
     */
    private byte priority;

    /**
     * 附件
     */
    private Map<String, Object> attachment = new HashMap<>();

    @Override
    public String toString() {
        return "Header [crcCode=" + crcCode + ", length=" + length
                + ", sessionID=" + sessionId + ", type=" + type + ", priority="
                + priority + ", attachment=" + attachment + "]";
    }
}
