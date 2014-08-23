package org.pistonmc.stickypiston.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginManager<T extends JavaPlugin> {

    private List<T> plugins;

    public PluginManager() {
        plugins = new ArrayList<>();
    }

    public void load(File plugins) {

    }

    public List<T> getPlugins() {
        return plugins;
    }

    protected T construct(Class<T> cls) throws Exception {
        return cls.getDeclaredConstructor().newInstance();
    }

}
