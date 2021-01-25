package fake.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author by chow
 * @Description 伪异步时间服务器
 * @date 2021/1/25 下午11:04
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器运行在端口：" + port);
            Socket socket;
            TimeServerHandlerExecutePool executePool = new TimeServerHandlerExecutePool(50, 10000);

            while (true) {
                socket = serverSocket.accept();
                executePool.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (serverSocket != null) {
                System.out.println("服务器已关闭");
                serverSocket.close();
            }
        }
    }

}
