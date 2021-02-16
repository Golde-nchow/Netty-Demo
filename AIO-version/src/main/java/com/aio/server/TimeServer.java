package com.aio.server;

/**
 * @author by chow
 * @Description NIO版本的时间服务器
 * @date 2021/1/26 下午11:25
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8081;
        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(asyncTimeServerHandler, "AIO-ASYNC-HANDLER-001").start();
    }

}
