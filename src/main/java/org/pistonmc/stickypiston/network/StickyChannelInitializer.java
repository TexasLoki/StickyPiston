package org.pistonmc.stickypiston.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.pistonmc.protocol.PlayerConnection;
import org.pistonmc.stickypiston.network.player.PlayerConnectionHandler;

public class StickyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        PlayerConnection connection = new PlayerConnectionHandler();
        PacketDecoder decoder = new PacketDecoder();
        ch.pipeline().addLast(new PacketDecoder(), new PlayerConnectionHandler());
    }

}
