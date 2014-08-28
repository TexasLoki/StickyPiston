package org.pistonmc.stickypiston.network.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.pistonmc.Piston;
import org.pistonmc.event.packet.ReceivedPacketEvent;
import org.pistonmc.event.packet.SendPacketEvent;
import org.pistonmc.event.packet.SentPacketEvent;
import org.pistonmc.exception.protocol.packet.PacketException;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.protocol.Protocol;
import org.pistonmc.protocol.PlayerConnection;
import org.pistonmc.protocol.packet.*;
import org.pistonmc.protocol.stream.PacketOutputStream;
import org.pistonmc.util.OtherUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.pistonmc.protocol.packet.ProtocolState.HANDSHAKE;

public class PlayerConnectionHandler extends ChannelHandlerAdapter implements PlayerConnection {

    private ProtocolState state = HANDSHAKE;
    private Protocol protocol;
    private ChannelHandlerContext context;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }

        UnreadPacket unread = (UnreadPacket) msg;
        if (unread.getLength() == -1) {
            return;
        }

        IncomingPacket packet;
        if (state == HANDSHAKE) {
            if (context == null) {
                context = ctx;
            }

            HandshakePacket handshake = new HandshakePacket();
            packet = handshake;
            packet.read(unread);

            Logging.getLogger().info("Read " + packet);
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

    public void sendPacket(OutgoingPacket packet) throws PacketException, IOException {
        SendPacketEvent event = new SendPacketEvent(packet);
        Piston.getEventManager().call(event);
        if (event.isCancelled()) {
            return;
        }

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        PacketOutputStream data = new PacketOutputStream(array);
        data.writeVarInt(packet.getId()); // write the packet id
        packet.write(data); // write the packet data
        sendPacket(array);

        Piston.getEventManager().call(new SentPacketEvent(packet));
    }

    public void sendPacket(ByteArrayOutputStream dataBuf) throws IOException {
        ByteArrayOutputStream sendBuf = new ByteArrayOutputStream();
        PacketOutputStream send = new PacketOutputStream(sendBuf); // create a new final byte array

        send.writeVarInt(dataBuf.toByteArray().length); // write the length of the buffer
        for (byte b : dataBuf.toByteArray()) {
            send.write(b); // write the array of bytes to the final byte array
        }

        ByteBuf buffer = context.alloc().buffer(sendBuf.toByteArray().length);
        buffer.writeBytes(sendBuf.toByteArray());
        context.writeAndFlush(buffer); // write the final array to the data output stream then flush the output and send it on it's way

        List<Byte> list = OtherUtils.asList(sendBuf.toByteArray());
        Logging.getLogger().debug("Sending " + list.size() + " bytes: " + list);
    }

    @Override
    public void close() {
        context.close();
    }

}
