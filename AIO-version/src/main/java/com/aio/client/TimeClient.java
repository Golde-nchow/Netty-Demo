package com.aio.client;

/**
 * @author by chow
 * @Description NIO时间客户端
 * @date 2021/1/27 下午11:44
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8081;
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-TimeClient-001").start();
    }

}
