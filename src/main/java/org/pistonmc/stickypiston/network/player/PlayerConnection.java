package org.pistonmc.stickypiston.network.player;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.plugin.protocol.Protocol;
import org.pistonmc.stickypiston.network.packet.State;
import org.pistonmc.stickypiston.network.packet.UnreadPacket;

import static org.pistonmc.stickypiston.network.packet.State.*;

public class PlayerConnection extends ChannelHandlerAdapter {

    private State state = HANDSHAKE;
    private Protocol protocol;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnreadPacket packet = (UnreadPacket) msg;
        if(state == HANDSHAKE) {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
