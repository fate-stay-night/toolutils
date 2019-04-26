package xyz.vimtool.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-08
 */
public class SocketServer {

    private volatile byte flag = 1;

    private int bufferSize = 1024;

    private String localCharset = "UTF-8";

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public void start() {

        //创建serverSocketChannel，监听8888端口
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(8888));
            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //为serverChannel注册selector
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("服务端开始工作：");

            while (flag == 1) {
                selector.select();
                System.out.println("开始处理请求 ： ");
                //获取selectionKeys并处理
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    //连接请求
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }

                    //读请求
                    if (key.isReadable()) {
                        executor.execute(() -> handleRead(key));
//                        handleRead(key);
                    }
                    //处理完后移除当前使用的key
                    keyIterator.remove();
                }
                System.out.println("完成请求处理。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAccept(SelectionKey selectionKey) {
        try {
            //获取channel
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            //非阻塞
            socketChannel.configureBlocking(false);
            //注册selector
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));

            System.out.println("建立请求......");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRead(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            if (socketChannel.read(buffer) == -1) {
                //没读到内容关闭
                socketChannel.shutdownOutput();
                socketChannel.shutdownInput();
                socketChannel.close();
                System.out.println("连接断开......");
            } else {
                //将channel改为读取状态
                buffer.flip();
                //按照编码读取数据
                String receivedStr = Charset.forName(localCharset).newDecoder().decode(buffer).toString();
                System.out.println(Thread.currentThread() + " 服务端接收到消息 : " + receivedStr);
                buffer.clear();

                //返回数据给客户端
                buffer = buffer.put((receivedStr).getBytes(localCharset));
                //读取模式
                buffer.flip();
                socketChannel.write(buffer);
                //注册selector 继续读取数据
                socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        server.start();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                server.setFlag((byte) 0);
            }
        });
    }
}
