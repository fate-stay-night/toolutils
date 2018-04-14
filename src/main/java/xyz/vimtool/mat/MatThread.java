package xyz.vimtool.mat;

import xyz.vimtool.socket.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author zhangzheng
 * @version 1.0
 * @since jdk1.8
 * @date 2018-4-13
 */
public class MatThread {

    private static final String HOST = "120.26.139.183";

    private static final int PORT = 8088;

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static Map<String, Info> infoMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        long msn = 20190001L;

        int icn = 10000;
        executor.execute(new KeepThread());
        executor.execute(new ReceiveThread());

        for (int i = 0; i < 3000; i++) {
            executor.execute(new InfoThread(new Socket(HOST,PORT), msn + i, icn + i));
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}

class Info {

    /**
     * 客户端socket
     */
    private Socket socket;

    /**
     * 传输的数据
     */
    private Protocol protocol;

    Info(Socket socket, Protocol protocol) {
        this.socket = socket;
        this.protocol = protocol;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}

class InfoThread implements Runnable {

    /**
     * 客户端socket
     */
    private Socket socket;

    /**
     * 传输的数据
     */
    private Protocol protocol;

    /**
     * 构造函数
     *
     * @param socket socket
     * @param msn 按摩垫编号
     * @param icn 物联网卡号
     */
    InfoThread(Socket socket, Long msn, Integer icn) {
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
        MatThread.infoMap.put(String.valueOf(protocol.getMsn()), new Info(socket, protocol));
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(protocol.toBytes());
            outputStream.flush();
            System.out.println("发送首次心跳数据: " + protocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 发送心跳包，保持长连接线程
 */
class KeepThread implements Runnable {
    public void run() {
        //第一次发送心跳，time字段设为物联网卡号
        while (true) {
            for (String msn : MatThread.infoMap.keySet()) {
                try {
                    Info info = MatThread.infoMap.get(msn);
                    Protocol protocol = info.getProtocol();
                    OutputStream outputStream = info.getSocket().getOutputStream();
                    if (String.valueOf(protocol.getMsn()).startsWith("2")) {
                        //如果按摩垫在使用中，则每次心跳减少运行时间10s
                        if (protocol.getTime() > 0) {
                            if (protocol.getTime() - 10 > 0) {
                                protocol.setTime(protocol.getTime() - 10);
                            } else {
                                protocol.setTime(0);
                                protocol.setIntensity((byte) 0);
                            }
                        }

//                        System.out.println("发送心跳数据: " + protocol);

                        outputStream.write(protocol.toBytes());
                        outputStream.flush();

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

//                        System.out.println("发送心跳数据: " + protocol);

                        outputStream.write(protocol.toBytes());
                        outputStream.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //间隔10s发送心跳
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 接收传输数据线程
 */
class ReceiveThread implements Runnable {
    public void run() {
        //响应数据
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                MatThread.infoMap.keySet().forEach(msn -> {
                    try {
                        Info info = MatThread.infoMap.get(msn);
                        InputStream inputStream = info.getSocket().getInputStream();
                        if (inputStream.available() > 0) {
                            Protocol protocol = info.getProtocol();
                            OutputStream outputStream = info.getSocket().getOutputStream();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
