package org.pistonmc.stickypiston.entity;

import org.pistonmc.entity.Entity;
import org.pistonmc.entity.builder.Builder;
import org.pistonmc.entity.builder.BuilderArguments;
import org.pistonmc.entity.builder.BuilderRegistry;

public class DefaultBuilderRegistry implements BuilderRegistry {

    @Override
    public <E extends Entity> E build(Class<E> type, BuilderArguments arguments) {
        return null;
    }

    @Override
    public <E extends Entity> Builder<E> get(Class<E> type) {
        return null;
    }

    @Override
    public <E extends Entity> void register(Builder<E> builder) {

    }

}
