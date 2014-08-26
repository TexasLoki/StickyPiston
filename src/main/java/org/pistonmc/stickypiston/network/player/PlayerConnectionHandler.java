package org.pistonmc.stickypiston.network.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.Piston;
import org.pistonmc.exception.protocol.packet.PacketException;
import org.pistonmc.plugin.protocol.Protocol;
import org.pistonmc.protocol.PlayerConnection;
import org.pistonmc.protocol.packet.HandshakePacket;
import org.pistonmc.protocol.packet.OutgoingPacket;
import org.pistonmc.protocol.packet.Packet;
import org.pistonmc.protocol.packet.ProtocolState;
import org.pistonmc.protocol.packet.UnreadPacket;
import org.pistonmc.protocol.stream.PacketOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.pistonmc.protocol.packet.ProtocolState.*;

public class PlayerConnectionHandler extends ChannelHandlerAdapter implements PlayerConnection {

    private ProtocolState state = HANDSHAKE;
    private Protocol protocol;
    private ChannelHandlerContext context;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnreadPacket unread = (UnreadPacket) msg;

        Packet packet;
        if(state == HANDSHAKE) {
            if(context == null) {
                context = ctx;
            }

            HandshakePacket handshake = new HandshakePacket();
            packet = handshake;
            packet.read(unread);

            protocol = Piston.getServer().getProtocolManager().find(handshake.getVersion(), this);
            state = handshake.getState();
            return;
        }

        packet = protocol.create(state, unread.getId());
        packet.read(unread);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void sendPacket(OutgoingPacket packet) throws PacketException, IOException {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        PacketOutputStream out = new PacketOutputStream(array);
        out.writeVarInt(packet.getId());
        packet.write(out);

        byte[] contents = array.toByteArray();
        array = new ByteArrayOutputStream();
        out = new PacketOutputStream(array);
        out.writeVarInt(contents.length);
        out.writeByteArray(contents);

        contents = array.toByteArray();
        ByteBuf buffer = context.alloc().buffer(contents.length);
        buffer.writeBytes(contents);

        context.writeAndFlush(buffer);
    }

}
