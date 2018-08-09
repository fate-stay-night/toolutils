package xyz.vimtool.mat;

import xyz.vimtool.socket.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 按摩垫Socket客户端线程
 *
 * @author zhangzheng
 * @version 1.0
 * @date 2018-3-1
 * @since jdk1.8
 */
public class MatClient extends Thread {

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

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-HH-dd hh-MM-ss");

    MatClient() {
    }

    /**
     * 构造函数
     *
     * @param socket socket
     * @param msn 按摩垫编号
     */
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
        protocol.setIntensity((byte) 0);
        protocol.setInduce((byte) 0);
        protocol.setReserve((short) 0);
        protocol.setTail("MH");
    }

    /**
     * 构造函数
     *
     * @param socket socket
     * @param msn 按摩垫编号
     * @param icn 物联网卡号
     */
    MatClient(Socket socket, Long msn, Integer icn) {
        this.socket = socket;
        protocol = new Protocol();
        protocol.setHead("LG");
        protocol.setVersion((byte) 2);
        protocol.setLength((byte) 25);
        protocol.setType((byte) 0);
        protocol.setMsn(msn);
        protocol.setTime(icn);
        protocol.setLoad(false);
        protocol.setRssi((byte) 19);
        protocol.setIntensity((byte) 0);
        protocol.setInduce((byte) 0);
        protocol.setReserve((short) 0);
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
                //第一次发送心跳，time字段设为物联网卡号
                boolean icnIgnore = true;
                while (true) {
                    if (String.valueOf(protocol.getMsn()).startsWith("2")) {
                        if (!icnIgnore) {
                            //如果按摩垫在使用中，则每次心跳减少运行时间10s
                            if (protocol.getTime() > 0) {
                                if (protocol.getTime() - 10 > 0) {
                                    protocol.setTime(protocol.getTime() - 10);
                                } else {
                                    protocol.setTime(0);
                                    protocol.setIntensity((byte) 0);
                                }
                            }
                        }

                        System.out.println(simpleDateFormat.format(new Date()) + ": "+ protocol);

                        outputStream.write(protocol.toBytes());
                        outputStream.flush();

                        if (icnIgnore) {
                            protocol.setTime(0);
                            icnIgnore = false;
                        }
                    } else {
                        //如果按摩垫在使用中，则每次心跳减少运行时间10s
                        if (protocol.getTime() > 0) {
                            if (protocol.getTime() - 10 > 0) {
                                protocol.setTime(protocol.getTime() - 10);
                            } else {
                                protocol.setTime(0);
                                protocol.setIntensity((byte) 0);
                            }
                        }

                        System.out.println("发送心跳数据: " + protocol);

                        outputStream.write(protocol.toBytes());
                        outputStream.flush();
                    }

                    //间隔10s发送心跳
                    TimeUnit.SECONDS.sleep(10);
                }
            } catch (Exception e) {
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
                    TimeUnit.MILLISECONDS.sleep(100);

                    //响应服务端发送数据请求
                    if (inputStream.available() > 0) {
                        byte[] bytes = new byte[Protocol.LENGTH];
                        inputStream.read(bytes);
                        Protocol receiveProtocol = new Protocol(bytes);

                        System.out.println("接收启动、调整数据: " + receiveProtocol);

                        //如果为启动请求，则返回启动应答数据
                        if (Protocol.TYPE_START == receiveProtocol.getType()) {
                            //构建启动应答数据,设置类型及力度大小
                            protocol.setTime(receiveProtocol.getTime());
                            protocol.setIntensity(receiveProtocol.getIntensity());

                            System.out.println("发送启动响应数据: " + protocol);

                            outputStream.write(protocol.toBytes());
                            outputStream.flush();
                        }

                        //如果为调整力度请求，则返回调整力度应答数据
                        if (Protocol.TYPE_ADJUST_INTENSITY == receiveProtocol.getType()) {
                            //构建启动应答数据,设置类型及力度大小
                            protocol.setIntensity(receiveProtocol.getIntensity());

                            System.out.println("发送调整力度响应数据: " + protocol);

                            outputStream.write(protocol.toBytes());
                            outputStream.flush();
                        }

                        //如果为调整体验请求，则返回调整体验应答数据
                        if (Protocol.TYPE_ADJUST_INDUCE == receiveProtocol.getType()) {
                            //构建启动应答数据,设置类型及体验
                            protocol.setInduce(receiveProtocol.getInduce());

                            System.out.println("发送调整体验响应数据: " + protocol);

                            outputStream.write(protocol.toBytes());
                            outputStream.flush();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
