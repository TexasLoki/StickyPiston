package org.pistonmc.stickypiston.network.packet;

public enum State {

    HANDSHAKE(-1),
    PLAY(0),
    STATUS(1),
    LOGIN(2);

    private int id;

    State(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
