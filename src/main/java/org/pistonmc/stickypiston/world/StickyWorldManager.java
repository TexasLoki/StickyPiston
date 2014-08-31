package org.pistonmc.stickypiston.world;

import org.pistonmc.Piston;
import org.pistonmc.logging.Logger;
import org.pistonmc.world.World;
import org.pistonmc.world.WorldBuilder;
import org.pistonmc.world.WorldManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StickyWorldManager implements WorldManager {

    private File container;
    private List<World> worlds;

    public StickyWorldManager(File container) {
        this.container = container;
        this.worlds = new ArrayList<>();
    }

    public File getContainer() {
        return container;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public World create(WorldBuilder builder) {
        String name = builder.getName();
        File folder = builder.getFolder();

        try {
            return new StickyWorld(folder, name);
        } catch (Exception ex) {
            getLogger().log("Could not load \"" + name + "\": ", ex);
            return null;
        }
    }

    private static Logger getLogger() {
        return Piston.getLogger();
    }

}
