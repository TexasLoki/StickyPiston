package org.pistonmc.stickypiston.network.player;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.stickypiston.network.protocol.Protocol;

public class PlayerConnection extends ChannelHandlerAdapter {

    private Protocol protocol;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

}
