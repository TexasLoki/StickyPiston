package org.pistonmc.stickypiston;

import joptsimple.OptionSet;
import org.pistonmc.Piston;
import org.pistonmc.Server;
import org.pistonmc.logging.Logger;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.protocol.ProtocolManager;
import org.pistonmc.plugin.PluginManager;
import org.pistonmc.util.reflection.SimpleObject;

import java.io.File;

public class StickyServer implements Server {

    private Logger logger;
    private ProtocolManager protocols;
    private PluginManager plugins;

    public StickyServer(OptionSet options) {
        new SimpleObject(null, Piston.class).field("server").set(this);
        this.logger = Logging.getLogger();
        this.protocols = new ProtocolManager(Logging.getLogger("Protocol", logger), (File) options.valueOf("protocols-folder"));
        this.plugins = new PluginManager(logger, (File) options.valueOf("plugins-folder"), "plugin.json");

        this.protocols.reload(false);
        this.plugins.reload(false);
    }

    public Logger getLogger() {
        return logger;
    }

    public ProtocolManager getProtocolManager() {
        return protocols;
    }

    public PluginManager getPluginManager() {
        return plugins;
    }

    @Override
    public void shutdown() {
        // shutdown the server
    }

}
