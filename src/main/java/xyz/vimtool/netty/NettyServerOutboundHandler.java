package xyz.vimtool.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

/**
 * 出站控制器
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-04-12
 */
public class NettyServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String message = (String) msg;
        ctx.write(NettyUtils.getSendByteBuf(message));
        TimeUnit.SECONDS.sleep(1);
        ctx.flush();
    }
}
