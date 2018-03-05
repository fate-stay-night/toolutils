package xyz.vimtool.mat;

import xyz.vimtool.socket.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * 按摩垫Socket客户端线程
 *
 * @author zhangzheng
 * @version 1.0
 * @since jdk1.8
 * @date 2018-3-1
 */
public class MatClient extends Thread {

    /**
     * 随机数，用于随机产生响应启动失败及发送心跳数据失败
     */
    private static Random random = new Random(47);

    /**
     * 客户端socket
     */
    private Socket socket;

    /**
     * 输出流
     */
    private OutputStream outputStream;

    /**
     * 输入流
     */
    private InputStream inputStream;

    /**
     * 传输的数据
     */
    private Protocol protocol;

    MatClient(Socket socket) {
        this.socket = socket;
        protocol = new Protocol();
        protocol.setHead("LG");
        protocol.setVersion((byte) 1);
        protocol.setLength((byte) 25);
        protocol.setType((byte) 0);
        protocol.setMsn(1201801150001l);
        protocol.setTime(0);
        protocol.setLoad(false);
        protocol.setRssi((byte) 19);
        protocol.setReserve(0);
        protocol.setTail("MH");
    }

    MatClient(Socket socket, Long msn) {
        this.socket = socket;
        protocol = new Protocol();
        protocol.setHead("LG");
        protocol.setVersion((byte) 1);
        protocol.setLength((byte) 25);
        protocol.setType((byte) 0);
        protocol.setMsn(msn);
        protocol.setTime(0);
        protocol.setLoad(false);
        protocol.setRssi((byte) 19);
        protocol.setReserve(0);
        protocol.setTail("MH");
    }

    @Override
    public void run() {
        Thread tReceive = new Thread(new MatClient.ReceiveThread());
        Thread tKeep = new Thread(new MatClient.KeepThread());
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tKeep.start();
        tReceive.start();
    }

    /**
     * 发送心跳包，保持长连接线程
     */
    private class KeepThread implements Runnable {
        public void run() {
            try {
                while (true) {

                    //间隔10s发送心跳
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //1/10的概率按摩垫发送心跳失败
                    if (random.nextInt(10) > 0) {
                        //如果按摩垫在使用中，则每次心跳减少运行时间10s
                        if (protocol.getTime() > 0) {
                            protocol.setTime(protocol.getTime() - 10 > 0 ? protocol.getTime() - 10 : 0);
                        }

//                        System.out.println("发送心跳数据: " + protocol);

                        outputStream.write(protocol.toBytes());
                        outputStream.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 接收传输数据线程
     */
    private class ReceiveThread implements Runnable {
        public void run() {
            try {
                while (true) {
                    //间隔10s发送心跳
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //响应服务端发送数据请求
                    if (inputStream.available() > 0) {
                        byte[] bytes = new byte[Protocol.LENGTH];
                        inputStream.read(bytes);
                        Protocol receiveProtocol = new Protocol(bytes);

                        System.out.println("接收启动数据: " + receiveProtocol);

                        //如果为启动请求，则返回启动应答数据，1/5的概率按摩垫启动应答失败
                        if (Protocol.TYPE_START == receiveProtocol.getType() && random.nextInt(10) > 1) {
                            protocol.setTime(receiveProtocol.getTime());

                            //构建启动应答数据
                            Protocol responseProtocol = new Protocol(protocol.toBytes());
                            responseProtocol.setType(Protocol.TYPE_START_RESPONSE);

                            System.out.println("发送启动响应数据: " + responseProtocol);

                            outputStream.write(responseProtocol.toBytes());
                            outputStream.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
