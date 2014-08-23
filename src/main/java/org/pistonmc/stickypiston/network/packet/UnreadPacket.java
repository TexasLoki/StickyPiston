package org.pistonmc.stickypiston.network.packet;

import java.io.IOException;

public class UnreadPacket {

    private int length;
    private PacketInputStream stream;
    private int id;

    public UnreadPacket(int length, PacketInputStream stream) throws IOException {
        this.length = length;
        this.stream = stream;
        if(stream != null) {
            this.id = stream.readVarInt();
        }
    }

    public int getLength() {
        return length;
    }

    public PacketInputStream getStream() {
        return stream;
    }

    public int getId() {
        return id;
    }

}
