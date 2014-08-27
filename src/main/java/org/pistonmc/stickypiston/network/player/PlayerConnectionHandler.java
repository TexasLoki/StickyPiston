package org.pistonmc.stickypiston.network.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.Piston;
import org.pistonmc.event.packet.ReceivedPacketEvent;
import org.pistonmc.event.packet.SendPacketEvent;
import org.pistonmc.event.packet.SentPacketEvent;
import org.pistonmc.exception.protocol.packet.PacketException;
import org.pistonmc.plugin.protocol.Protocol;
import org.pistonmc.protocol.PlayerConnection;
import org.pistonmc.protocol.packet.HandshakePacket;
import org.pistonmc.protocol.packet.IncomingPacket;
import org.pistonmc.protocol.packet.OutgoingPacket;
import org.pistonmc.protocol.packet.ProtocolState;
import org.pistonmc.protocol.packet.UnreadPacket;
import org.pistonmc.protocol.stream.PacketOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.pistonmc.protocol.packet.ProtocolState.HANDSHAKE;

public class PlayerConnectionHandler extends ChannelHandlerAdapter implements PlayerConnection {

    private ProtocolState state = HANDSHAKE;
    private Protocol protocol;
    private ChannelHandlerContext context;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null) {
            return;
        }

        UnreadPacket unread = (UnreadPacket) msg;
        if(unread.getLength() == -1) {
            return;
        }

        IncomingPacket packet;
        if(state == HANDSHAKE) {
            if(context == null) {
                context = ctx;
            }

            HandshakePacket handshake = new HandshakePacket();
            packet = handshake;
            packet.read(unread);

            protocol = Piston.getServer().getProtocolManager().find(handshake.getVersion(), this);
            state = handshake.getState();
        } else {
            packet = protocol.create(state, unread.getId());
            packet.read(unread);
        }

        Piston.getEventManager().call(new ReceivedPacketEvent(packet));
        protocol.handle(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void sendPacket(OutgoingPacket packet) throws PacketException, IOException {
        SendPacketEvent event = new SendPacketEvent(packet);
        Piston.getEventManager().call(event);
        if(event.isCancelled()) {
            return;
        }

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

        Piston.getEventManager().call(new SentPacketEvent(packet));
    }

    @Override
    public void close() {
        context.close();
    }

}
