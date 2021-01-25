package com.bio;

import java.io.*;
import java.net.Socket;

/**
 * @author by chow
 * @Description 时间客户端
 * @date 2021/1/24 下午11:48
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        // 初始化套接字、输入输出流
        try (Socket socket = new Socket("127.0.0.1", port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // 开始写
            out.println("查询订单");
            System.out.println("发送成功");
            String readStr = in.readLine();
            System.out.println("收到响应：" + readStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
