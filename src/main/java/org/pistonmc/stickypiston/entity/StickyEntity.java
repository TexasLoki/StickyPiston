package org.pistonmc.stickypiston.entity;

import org.pistonmc.entity.Entity;
import org.pistonmc.entity.builder.Builder;
import org.pistonmc.entity.builder.BuilderArguments;
import org.pistonmc.world.Location;
import org.pistonmc.world.World;

import java.util.UUID;

public class StickyEntity implements Entity {

    private String name;
    private int entityId;
    private UUID uniqueId;

    private Location location;
    private boolean valid;

    public StickyEntity(String name, int entityId, UUID uniqueId) {
        this.name = name;
        this.entityId = entityId;
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Location getLocation() {
        return location;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public boolean isValid() {
        return valid;
    }

    public static class StickyEntityBuilder extends Builder<StickyEntity> {

        @Override
        public StickyEntity build(BuilderArguments arguments) {
            String name = arguments.get(0, String.class);
            int entityId = arguments.get(1, Integer.class);
            UUID uniqueId = arguments.get(2, UUID.class);

            return new StickyEntity(name, entityId, uniqueId);
        }

    }

}
