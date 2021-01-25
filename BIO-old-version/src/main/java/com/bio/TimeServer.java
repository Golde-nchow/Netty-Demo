package com.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description BIO时间服务器
 * @date 2021/1/24 下午10:55
 * @author by chow
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        // 创建服务器套接字
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("时间服务器运行在端口：" + port);
            Socket socket;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } finally {
            if (server != null) {
                System.out.println("时间服务器关闭");
                server.close();
            }
        }
    }

}
