package org.pistonmc.stickypiston.world;

public class ChunkLocation {

    private int x;
    private int z;

    public ChunkLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

}
