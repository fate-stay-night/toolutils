package xyz.vimtool.device;

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
public class DevClient extends Thread {

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
    private Pro protocol;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-HH-dd hh-MM-ss");

    DevClient() {
    }

    /**
     * 构造函数
     *
     * @param socket socket
     * @param msn 按摩垫编号
     */
    DevClient(Socket socket, Integer msn) {
        this.socket = socket;
        protocol = new Pro();
        protocol.setType((byte) -1);
        protocol.setParam1(msn);
        protocol.setParam2((byte)0);
        protocol.setReserve((short) 0);
    }

    @Override
    public void run() {
        Thread tReceive = new Thread(new DevClient.ReceiveThread());
        Thread tKeep = new Thread(new DevClient.KeepThread());
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
        @Override
        public void run() {

            try {
                //第一次发送心跳，time字段设为物联网卡号
                boolean icnIgnore = true;
                while (true) {
                    if (!icnIgnore) {
                        if (-1 == protocol.getType()) {
                            protocol.setType((byte) 0);
                            protocol.setParam1(0);
                        }

                        int param1;
                        if (0 == protocol.getType() && (param1 = protocol.getParam1()) > 0) {
                            param1 = param1 - 10;
                            protocol.setParam1(param1 > 0 ? param1 : 0);
                        }
                    }

                    System.out.println(simpleDateFormat.format(new Date()) + ": "+ protocol);

                    outputStream.write(Pro.toBytes(protocol));
                    outputStream.flush();

                    if (icnIgnore) {
                        protocol.setParam1(0);
                        icnIgnore = false;
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
        @Override
        public void run() {
            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(100);

                    //响应服务端发送数据请求
                    if (inputStream.available() > 0) {
                        byte[] bytes = new byte[12];
                        inputStream.read(bytes);
                        Pro receiveProtocol = Pro.toProtocol(bytes);

                        System.out.println("接收启动、调整数据: " + receiveProtocol);

                        //如果为启动请求，则返回启动应答数据
                        if (1 == receiveProtocol.getType()) {
                            //构建启动应答数据,设置类型及力度大小
                            protocol.setType((byte) 2);
                            protocol.setParam1(receiveProtocol.getParam1());
                            protocol.setParam2((byte) 0);

                            System.out.println("发送启动响应数据: " + protocol);

                            outputStream.write(Pro.toBytes(protocol));
                            outputStream.flush();

                            protocol.setType((byte) 0);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
