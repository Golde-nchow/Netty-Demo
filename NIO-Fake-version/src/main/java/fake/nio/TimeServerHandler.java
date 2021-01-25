package fake.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author by chow
 * @Description 时间服务器数据处理器
 * @date 2021/1/24 下午11:21
 */
public class TimeServerHandler implements Runnable {

    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy年MM月dd日 HH时mm分ss秒")
                .withZone(ZoneOffset.ofHours(8));
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String currentTime;
            String body;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("时间服务器收到的消息：" + body);
                currentTime = "查询订单".equals(body) ? LocalDateTime.now().format(dateTimeFormatter) : "错误的订单";
                out.println(currentTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
