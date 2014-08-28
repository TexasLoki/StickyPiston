package org.pistonmc.stickypiston.entity;

import org.pistonmc.entity.Entity;

import java.util.UUID;

public class StickyEntity implements Entity {

    private String name;
    private int entityId;
    private UUID uniqueId;

    public String getName() {
        return name;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

}
