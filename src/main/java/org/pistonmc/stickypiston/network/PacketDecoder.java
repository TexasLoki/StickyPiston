package org.pistonmc.stickypiston.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.pistonmc.stickypiston.network.packet.PacketInputStream;
import org.pistonmc.stickypiston.network.packet.UnreadPacket;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<DecoderState> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            in.markReaderIndex();

            PacketInputStream input = new PacketInputStream(new ByteBufInputStream(in));
            int length = input.readVarInt();

            if(in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            byte[] bytes = input.readBytes(length);
            input = new PacketInputStream(new ByteArrayInputStream(bytes));
            out.add(new UnreadPacket(length, input));
        } catch(EOFException ex) {
            // display a message saying the player has disconnected
            out.add(new UnreadPacket(-1, null));
        }
    }

}
