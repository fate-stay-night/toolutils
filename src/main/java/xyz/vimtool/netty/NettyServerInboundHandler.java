package xyz.vimtool.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalTime;

/**
 * 入站控制器
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-12
 */
public class NettyServerInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String received = NettyUtils.getMessage(buf);
        System.out.println("服务端收到客户端发来的消息:" + received);
        ctx.writeAndFlush("服务端发送消息" + LocalTime.now());
    }
}
