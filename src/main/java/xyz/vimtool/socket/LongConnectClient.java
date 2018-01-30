package xyz.vimtool.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * socket长连接client
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-5
 */
public class LongConnectClient extends Thread {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 8088;

    private static byte[] bytes;

    private Socket socket;

    private OutputStream outStr = null;

    private InputStream inStr = null;

    LongConnectClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Thread tReceive = new Thread(new ReceiveThread());
        Thread tKeep = new Thread(new KeepThread());
        try {
            outStr = socket.getOutputStream();
            inStr = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tKeep.start();
        tReceive.start();
    }

    //保持长连接线程
    private class KeepThread implements Runnable {
        public void run() {
            try {
                System.out.println("=====================开始发送心跳包==============");
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("发送心跳数据包");
                    for (byte b : bytes) {
                        System.out.println(b);
                    }
                    outStr.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //接收传输数据线程
    private class ReceiveThread implements Runnable {
        public void run() {
            try {
                System.out.println("==============开始接收数据===============");
                while (true) {
                    byte[] b = new byte[1024];
                    int r = inStr.read(b);
                    if(r > -1){
                        String str = new String(b);
                        System.out.println(str + "[" + Thread.currentThread().getName() + "]");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Protocol protocol = new Protocol();
        protocol.setHead("LG");
        protocol.setVersion((byte) 1);
        protocol.setLength((byte) 25);
        protocol.setType((byte) 0);
        protocol.setMsn(1201708000036l);
        protocol.setTime(0);
        protocol.setLoad(false);
        protocol.setRssi((byte) 31);
        protocol.setReserve(0);
        protocol.setTail("MH");
        bytes = protocol.toBytes();
//        for (int i = 0; i < 10; i++) {
            LongConnectClient longConnectClient = new LongConnectClient(new Socket(HOST, PORT));
            longConnectClient.start();
//        }
    }
}
