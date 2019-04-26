package xyz.vimtool.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-08
 */
public class SocketClient {

    public static void start() {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            //设置为非阻塞模式
            socketChannel.configureBlocking(false);

            //连接服务端socket
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));

            //获得通道管理器;为该通道注册SelectionKey.OP_CONNECT事件
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            System.out.println("客户端启动");

            //轮询访问selector
            while (true) {
                //选择注册过的io操作的事件(第一次为SelectionKey.OP_CONNECT)
                selector.select();
                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = ite.next();
                    //删除已选的key，防止重复处理
                    ite.remove();
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();

                        //如果正在连接，则完成连接
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }

                        channel.configureBlocking(false);
                        //向服务器发送消息
                        ByteBuffer buffer = ByteBuffer.wrap("send message to server.".getBytes());
                        channel.write(buffer);

                        //连接成功后，注册接收服务器消息的事件
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println("客户端连接成功");
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);

                        // 将调用缓冲区的界限设置为当前位置，并将当前位置重置为0
                        buffer.flip();
                        // 从当前位置读到界限位置；读取缓冲区内容
                        String message = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
                        System.out.println(Thread.currentThread() + " 客户端接收到消息 : " + message);
                        buffer.clear();

                        //返回数据给客户端
                        buffer = buffer.put((message).getBytes("UTF-8"));
                        //读取模式
                        buffer.flip();
                        socketChannel.write(buffer);
                        //注册selector 继续读取数据
                        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Executor executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            executor.execute(SocketClient::start);
        }
    }
}
