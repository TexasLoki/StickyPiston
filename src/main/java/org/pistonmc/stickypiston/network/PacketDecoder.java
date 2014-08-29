package org.pistonmc.stickypiston.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.pistonmc.protocol.PlayerConnection;
import org.pistonmc.protocol.packet.UnreadPacket;
import org.pistonmc.protocol.stream.PacketInputStream;
import org.pistonmc.util.EncryptionUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<DecoderState> {

    private PlayerConnection connection;

    public PacketDecoder(PlayerConnection connection) {
        state(DecoderState.LENGTH);
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            in.markReaderIndex();

            state(DecoderState.LENGTH);
            if (!in.isReadable()) {
                return;
            }

            DataInputStream streamInput = new DataInputStream(new ByteBufInputStream(in));
            InputStream stream = connection.isSecured() ? EncryptionUtils.decryptInputStream(streamInput, connection.getSecretKey()) : streamInput;

            PacketInputStream input = new PacketInputStream(stream);
            int length = input.readVarInt();

            if (in.readableBytes() < length) {
                state(DecoderState.COLLECTING);
                in.resetReaderIndex();
                return;
            }

            byte[] bytes = input.readBytes(length);
            state(DecoderState.COLLECTED);
            input = new PacketInputStream(new DataInputStream(new ByteArrayInputStream(bytes)));
            out.add(new UnreadPacket(length, input));
        } catch (EOFException ex) {
            // display a message saying the player has disconnected
            out.add(new UnreadPacket(-1, null));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected DecoderState state(DecoderState newState) {
        DecoderState oldState = super.state(newState);
        // Logging.getLogger().info("Changed state from " + oldState + " to " + newState);
        return oldState;
    }

}
