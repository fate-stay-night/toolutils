package xyz.vimtool.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-12
 */
public class NettyServer {

    public static void main(String[] args) {
        // 线程池；bossGroup 线程池则只是在 Bind 某个端口后，
        // 获得其中一个线程作为 MainReactor，
        // 专门处理端口的 Accept 事件，每个端口对应一个 Boss 线程；
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        // 线程池；workerGroup 线程池会被各个 SubReactor 和 Worker 线程充分利用
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap();
        // 组装NioEventLoopGroup
        server.group(bossGroup, workerGroup);
        // 设置channel类型为NIO(协议)
        server.channel(NioServerSocketChannel.class);
        // 设置连接配置参数
        server.option(ChannelOption.SO_BACKLOG, 1024);
        // 不延迟，消息立即发送
//        server.option(ChannelOption.TCP_NODELAY, true);
        // 长连接
        server.childOption(ChannelOption.SO_KEEPALIVE, true);
        // 配置入站、出战handler
        ChannelInitializer<NioSocketChannel> childHandler = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline pipeline = nioSocketChannel.pipeline();

                pipeline.addLast(new NettyServerOutboundHandler());
                pipeline.addLast(new NettyServerInboundHandler());
            }
        };
        server.childHandler(childHandler);

        server.bind(8888).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("服务端绑定成功");
            } else {
                System.out.println("服务端绑定失败");
            }
        });
    }
}
