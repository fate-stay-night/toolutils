package xyz.vimtool.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * socket长连接server端
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-5
 */
public class LongConnectServer {

    public static final int LISTEN_PORT = 5987;

    public void listenRequest() {
        ServerSocket serverSocket = null;
        ExecutorService threadExecutor = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(LISTEN_PORT);
            System.out.println("Server listening requests...");
            while (true) {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    threadExecutor.execute(new RequestThread(socket));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (threadExecutor != null)
                threadExecutor.shutdown();
            if (serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 处理Client端的Request执行
     */
    class RequestThread implements Runnable {
        private Socket clientSocket;

        public RequestThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            System.out.printf("有%s連線進來!\n", clientSocket.getRemoteSocketAddress());
            DataInputStream input = null;
            DataOutputStream output = null;

            try {
                input = new DataInputStream(this.clientSocket.getInputStream());
                output = new DataOutputStream(this.clientSocket.getOutputStream());
                int i = 0;
                while (true) {
                    byte[] b = new byte[1024];
                    int r = input.read(b);

                    if (r > -1) {
                        String s = new String(b);
                        if (s.matches(".*heart beat.*")) {
                            System.out.println(s);
                        } else {
                            System.out.println(s + "[" + Thread.currentThread().getName() +"]");
                            output.write(("Server return" + i).getBytes());
                            output.flush();
                            i++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    if (this.clientSocket != null && !this.clientSocket.isClosed())
                        this.clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        LongConnectServer server = new LongConnectServer();
        server.listenRequest();
    }
}
