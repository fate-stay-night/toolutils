package xyz.vimtool.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-12
 */
public class NettyClient {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        Bootstrap client = new Bootstrap();
        client.group(bossGroup);
        client.channel(NioSocketChannel.class);
        client.option(ChannelOption.SO_KEEPALIVE, true);
        // 配置入站、出战handler
        ChannelInitializer<NioSocketChannel> childHandler = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline pipeline = nioSocketChannel.pipeline();
                pipeline.addLast(new NettyClientOutboundHandler());
                pipeline.addLast(new NettyClientInboundHandler());
            }
        };
        client.handler(childHandler);
        client.connect("127.0.0.1", 8888).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("客户端绑定成功");
            } else {
                System.out.println("客户端绑定失败");
            }
        });
    }
}
