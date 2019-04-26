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
public class NettyClientInboundHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当连接建立的时候向服务端发送消息 ，channelActive 事件当连接建立的时候会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("客户端首次发送消息" + LocalTime.now());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        String received = NettyUtils.getMessage(buf);
        System.out.println("客户端收到服务端发来的消息:" + received);

        ctx.writeAndFlush("客户端再次发送消息" + LocalTime.now());
    }
}
