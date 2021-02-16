package com.nio;

/**
 * @author by chow
 * @Description NIO时间客户端
 * @date 2021/1/27 下午11:44
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8081;
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }

}
