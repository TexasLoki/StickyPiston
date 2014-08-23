package org.pistonmc.stickypiston.plugin;

import org.pistonmc.logging.Logger;

import java.io.File;
import java.net.URLClassLoader;

public class JavaPlugin {

    private URLClassLoader loader;
    private File file;

    private boolean enabled = false;
    private PluginDescription description;
    private Logger logger;

    private File dataFolder;

    public PluginDescription getDescription() {
        return description;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        boolean update = this.enabled != enabled;
        if(!update) {
            getLogger().info(getDescription().getName() + " is already " + (enabled ? "enabled" : "disabled"));
            return;
        }

        if(enabled) {
            getLogger().info("Enabling " + getDescription().getName() + " v" + getDescription().getVersion());
            try {
                onEnable();
                this.enabled = true;
            } catch(Exception ex) {
                ex.printStackTrace();
                logger.warning("An error occurred while enabling " + getDescription().getName() + ", is it up to date?");
            }
        } else {
            getLogger().info("Disabling " + getDescription().getName() + " v" + getDescription().getVersion());
            try {
                this.enabled = false;
                onDisable();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
