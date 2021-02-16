package com.nio;

/**
 * @author by chow
 * @Description NIO版本的时间服务器
 * @date 2021/1/26 下午11:25
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8081;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-1").start();
    }

}
