package org.pistonmc.stickypiston.entity;

import org.pistonmc.entity.Entity;
import org.pistonmc.entity.builder.Builder;
import org.pistonmc.entity.builder.BuilderArguments;
import org.pistonmc.entity.builder.BuilderRegistry;
import org.pistonmc.entity.builder.RegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class DefaultBuilderRegistry implements BuilderRegistry {

    private List<RegistryEntry> entries;

    public DefaultBuilderRegistry() {
        this.entries = new ArrayList<>();
    }

    @Override
    public <E extends Entity> E build(Class<E> type, BuilderArguments arguments) {
        Builder<E> builder = get(type);
        return builder.build(arguments);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> Builder<E> get(Class<E> type) {
        for(RegistryEntry entry : entries) {
            if(entry.getEntityType().equals(type)) {
                return (Builder<E>) entry.getBuilder();
            }
        }

        return null;
    }

    @Override
    public <E extends Entity> void register(Class<E> cls, Builder<E> builder) {
        entries.add(new RegistryEntry<>(cls, builder));
    }

}
