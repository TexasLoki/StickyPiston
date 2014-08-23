package org.pistonmc.stickypiston.exception.plugin;

import org.pistonmc.stickypiston.plugin.PluginDescription;

public class PluginException extends Exception {

    private String name;
    private String version;

    public PluginException(String name, String version, String message) {
        super(message);
        this.name = name;
        this.version = version;
    }

    public PluginException(PluginDescription description, String message) {
        this(description.getName(), description.getVersion(), message);
    }

    public PluginException(String name, String version) {
        super();
        this.name = name;
        this.version = version;
    }

    public PluginException(PluginDescription description) {
        this(description.getName(), description.getVersion());
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

}
