package xyz.vimtool.socket;

import xyz.vimtool.commons.BytesUtils;
import xyz.vimtool.commons.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * socket长连接client
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-5
 */
public class LongConnectClient extends Thread {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 5987;

    private static byte[] bytes;

    private Socket socket;

    private OutputStream outStr = null;

    private InputStream inStr = null;

    LongConnectClient(Socket socket) {
        this.socket = socket;
        try {
            this.socket.setOOBInline(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
                    System.out.println("发送心跳数据包");
                    outStr.write(bytes);
                    outStr.flush();
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                    byte[] b = new byte[1];
                    if (inStr.read() > -1) {
                        inStr.read(b);
                        System.out.println(b.toString());
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
        protocol.setMsn(1201708000036L);
        protocol.setTime(0);
        protocol.setLoad(false);
        protocol.setRssi((byte) 31);
        protocol.setIntensity((byte) 0);
        protocol.setIntensity((byte) 0);
        protocol.setReserve((short) 0);
        protocol.setTail("MH");
        bytes = protocol.toBytes();
        LongConnectClient longConnectClient = new LongConnectClient(new Socket(HOST, PORT));
        longConnectClient.start();
    }
}
