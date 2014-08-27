package org.pistonmc.stickypiston;

import joptsimple.OptionSet;
import org.pistonmc.Piston;
import org.pistonmc.Server;
import org.pistonmc.event.DefaultEventManager;
import org.pistonmc.event.EventManager;
import org.pistonmc.logging.Logger;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.JavaPlugin;
import org.pistonmc.plugin.JavaPluginManager;
import org.pistonmc.plugin.protocol.ProtocolManager;
import org.pistonmc.stickypiston.network.NetworkServer;
import org.pistonmc.util.reflection.SimpleObject;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;

public class StickyServer implements Server {

    private Logger logger;
    private ProtocolManager protocols;
    private JavaPluginManager plugins;
    private EventManager events;
    private NetworkServer network;

    public StickyServer(OptionSet options) {
        new SimpleObject(null, Piston.class).field("server").set(this);
        this.logger = Logging.getLogger().setFormat((SimpleDateFormat) options.valueOf("d")).setDebug((boolean) options.valueOf("debug"));
        this.protocols = new ProtocolManager(Logging.getLogger("Protocol", logger), (File) options.valueOf("protocols-folder"));
        this.plugins = new JavaPluginManager(logger, (File) options.valueOf("plugins-folder"));
        this.events = new DefaultEventManager(logger);

        this.protocols.reload(false);
        this.protocols.enable();
        this.plugins.reload(false);
        this.plugins.enable();

        InetSocketAddress address = new InetSocketAddress((String) options.valueOf("bind-ip"), (Integer) options.valueOf("p"));
        this.network = new NetworkServer(address);
    }

    protected void init() {
        this.network.start();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ProtocolManager getProtocolManager() {
        return protocols;
    }

    @Override
    public JavaPluginManager getPluginManager() {
        return plugins;
    }

    @Override
    public EventManager getEventManager() {
        return events;
    }

    @Override
    public void shutdown() {
        for(JavaPlugin plugin : plugins.getPlugins()) {
            plugin.setEnabled(false);
        }
    }

}
