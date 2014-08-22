package org.pistonmc.stickypiston.network.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.protocol.Client;

public abstract class Protocol extends ChannelHandlerAdapter {

    private Client client;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(client == null) {

        }
    }

    public Client getClient() {
        return client;
    }

}
