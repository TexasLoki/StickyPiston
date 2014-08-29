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
import org.pistonmc.stickypiston.auth.AuthenticationHandler;
import org.pistonmc.stickypiston.auth.BungeeAuthenticationHandler;
import org.pistonmc.stickypiston.auth.YggdrasilAuthenticationHandler;
import org.pistonmc.util.EncryptionUtils;
import org.pistonmc.util.OtherUtils;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static org.pistonmc.protocol.packet.ProtocolState.HANDSHAKE;

public class PlayerConnectionHandler extends ChannelHandlerAdapter implements PlayerConnection {

    private ProtocolState state = HANDSHAKE;
    private Protocol protocol;
    private ChannelHandlerContext context;
    private SecretKey key;
    private boolean secured;

    private AuthenticationHandler authenticationHandler;

    private InetSocketAddress playerIP;

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

            /*if (Piston.getConfig().getBoolean("bungeecord")) {
                String[] split = handshake.getAddress().split("\00");
                handshake.setAddress(split[0]);
                playerIP = new InetSocketAddress(split[1], handshake.getPort());

                authenticationHandler = new BungeeAuthenticationHandler();
                boolean success = ((BungeeAuthenticationHandler) authenticationHandler).auth(handshake.getAddress().split("\00"));
                if (!success) {
                    // TODO: Kick player here
                    //protocol.sendPacket(new PacketLoginOutDisconnect("If you wish to use IP forwarding, please enable it in your BungeeCord config.", false));
                    ctx.close();
                }
            }*/

            if (authenticationHandler == null) authenticationHandler = new YggdrasilAuthenticationHandler();

            Logging.getLogger().info("Read " + packet);
            protocol = Piston.getServer().getProtocolManager().find(handshake.getVersion(), this);
            state = handshake.getState();
        } else {
            packet = protocol.create(state, unread.getId());
            packet.read(unread);
        }

        Piston.getEventManager().call(new ReceivedPacketEvent(packet));
        protocol.handle(packet, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void secure(SecretKey key) {
        this.key = key;
        this.secured = true;
    }

    @Override
    public void unsecure() {
        this.key = null;
        this.secured = false;
    }

    @Override
    public boolean isSecured() {
        return secured && key != null;
    }

    @Override
    public SecretKey getSecretKey() {
        return key;
    }

    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void sendPacket(OutgoingPacket packet) throws PacketException, IOException {
        SendPacketEvent event = new SendPacketEvent(packet);
        Piston.getEventManager().call(event);
        if (event.isCancelled()) {
            return;
        }

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        OutputStream stream = isSecured() ? EncryptionUtils.encryptOutputStream(array, key) : array;
        PacketOutputStream data = new PacketOutputStream(stream);
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
