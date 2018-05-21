package xyz.vimtool.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            try {
                this.clientSocket.setOOBInline(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(2);
                    if (isClosed(clientSocket)) {
                        System.out.println("############################");
                        clientSocket.close();
                        break;
                    }
//                    } else {
//                        OutputStream out = clientSocket.getOutputStream();
//                        out.write("server send".getBytes());
//                        System.out.println("server send");
//                        out.flush();
//                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            try (DataInputStream input = new DataInputStream(this.clientSocket.getInputStream());
//                 DataOutputStream output = new DataOutputStream(this.clientSocket.getOutputStream())) {
//                int i = 0;
//                while (true) {
//                    TimeUnit.SECONDS.sleep(1L);
//                    byte[] b = new byte[1024];
//                    int r = input.read(b);
//
//                    if (r > -1) {
//                        String s = new String(b);
//                        System.out.println(s);
//                        if (s.matches(".*heart beat.*")) {
//                            System.out.println(s);
//                        } else {
//                            System.out.println(s + "[" + Thread.currentThread().getName() +"]");
//                            output.write(("Server return" + i).getBytes());
//                            output.flush();
//                            i++;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (this.clientSocket != null && !this.clientSocket.isClosed()) {
//                        this.clientSocket.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    /**
     * 发送紧急数据，判断socket是否断连
     */
    private boolean isClosed(Socket socket) {
        try {
            socket.sendUrgentData(97);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
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
