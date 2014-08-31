package org.pistonmc.stickypiston.world;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagList;
import org.pistonmc.Piston;
import org.pistonmc.entity.Entity;
import org.pistonmc.util.ClassUtils;
import org.pistonmc.util.OtherUtils;
import org.pistonmc.util.reflection.SimpleMethod;
import org.pistonmc.util.reflection.SimpleObject;
import org.pistonmc.world.Block;
import org.pistonmc.world.Chunk;
import org.pistonmc.world.World;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

public class StickyWorld implements World {

    private File folder;
    private String name;

    private File region;
    private Map<File, RegionFile> regions;
    private Map<ChunkLocation, Chunk> chunks;

    public StickyWorld(File folder, String name) throws Exception {
        this.folder = folder;
        this.name = name;
        this.region = new File(folder, "region");
        this.regions = new HashMap<>();
        this.chunks = new HashMap<>();

        NbtInputStream input = new NbtInputStream(new GZIPInputStream(new FileInputStream(new File(folder, "level.dat"))));
        ITag tag = input.readTag();
        display(tag);
    }

    public File getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }

    public List<Entity> getEntities() {
        return null;
    }

    public <T extends Entity> List<T> getEntities(Class<T> cls) {
        return null;
    }

    public Chunk getChunk(int x, int z) {
        for(Entry<ChunkLocation, Chunk> entry : chunks.entrySet()) {
            if(entry.getKey().getX() == x && entry.getKey().getZ() == z) {
                return entry.getValue();
            }
        }

        InputStream stream = getChunkDataInputStream(x, z);
        NbtInputStream input = new NbtInputStream(stream);

        try {
            ITag tag = input.readTag();
            if(tag == null) {
                return generate(x, z);
            }

            display(tag);
            return null;
        } catch (IOException ex) {
            Piston.getLogger().log("Could not read Chunk @ " + x + ", z: ", ex);
            return null;
        }
    }

    public Chunk generate(int x, int z) {
        return null;
    }

    public RegionFile getRegion(int x, int z) {
        File file = new File(region, "r." + (x >> 5) + "." + (z >> 5) + ".mca");

        RegionFile region = regions.get(file);

        if (region == null) {
            region = new RegionFile(file);
            regions.put(file, region);
        }

        return region;
    }

    public Block getBlock(int x, int y, int z) {
        return null;
    }

    private void display(ITag nbt) {
        StringBuilder builder = new StringBuilder();
        List<String> parents = new ArrayList<>();
        ITag parent = nbt.getParent();
        while(parent != null) {
            parents.add(name(parent) + " - ");
            parent = parent.getParent();
        }

        parents = OtherUtils.reverse(parents);
        for(String name : parents) {
            builder.append(name);
        }

        builder.append(name(nbt));
        String name = builder.toString();

        Piston.getLogger().debug(name + ": " + ClassUtils.build(nbt, true));
        if(nbt instanceof TagCompound) {
            TagCompound compound = (TagCompound) nbt;
            for(ITag tag : compound.getTags().values()) {
                display(tag);
            }

            return;
        }

        if(nbt instanceof TagList) {
            TagList list = (TagList) nbt;
            for(ITag tag : list.getTags()) {
                display(tag);
            }

            return;
        }

        SimpleObject object = new SimpleObject(nbt);
        SimpleMethod method = object.method("getValue");
        if(method != null) {
            Piston.getLogger().debug(name + ": " + method.value());
        }
    }

    private String name(ITag tag) {
        return tag.getName() == null || tag.getName().equals("") ? "Unknown" : tag.getName();
    }

    private DataInputStream getChunkDataInputStream(int x, int z) {
        RegionFile r = getRegion(x, z);
        return r.getChunkDataInputStream(x & 31, z & 31);
    }

    private DataOutputStream getChunkDataOutputStream(int x, int z) {
        RegionFile r = getRegion(x, z);
        return r.getChunkDataOutputStream(x & 31, z & 31);
    }

}
