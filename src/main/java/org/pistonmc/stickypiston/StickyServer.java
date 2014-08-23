package org.pistonmc.stickypiston;

import joptsimple.OptionSet;
import org.pistonmc.Piston;
import org.pistonmc.Server;
import org.pistonmc.logging.Logger;
import org.pistonmc.logging.Logging;
import org.pistonmc.stickypiston.network.protocol.Protocol;
import org.pistonmc.stickypiston.network.protocol.ProtocolManager;
import org.pistonmc.stickypiston.plugin.PluginManager;
import org.pistonmc.util.reflection.SimpleObject;

import java.io.File;
import java.util.Map;

public class StickyServer implements Server {

    private Logger logger;
    private ProtocolManager protocols;
    private PluginManager plugins;

    public StickyServer(OptionSet options) {
        new SimpleObject(null, Piston.class).field("server").set(this);
        this.logger = Logging.getLogger();
        this.protocols = new ProtocolManager(Logging.getLogger("Protocol", logger), (File) options.valueOf("protocols-folder"));
        this.plugins = new PluginManager(logger, (File) options.valueOf("plugins-folder"), "plugin.json");
    }

    @Override
    public void shutdown() {
        // shutdown the server
    }

}
