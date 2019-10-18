package xyz.vimtool.device;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * server
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @since   jdk1.8
 * @date    2018/9/5
 */
public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8078);
        Socket socket = serverSocket.accept();
        socket.setSoTimeout(1000);

        while (true) {
            int available = socket.getInputStream().available();
            System.out.println(available);

            TimeUnit.SECONDS.sleep(2);
        }
    }
}
