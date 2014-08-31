package org.pistonmc.stickypiston;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.tag.AbstractTag;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagCompound;
import joptsimple.OptionSet;
import org.pistonmc.Piston;
import org.pistonmc.Server;
import org.pistonmc.commands.CommandRegistry;
import org.pistonmc.commands.DefaultCommandRegistry;
import org.pistonmc.configuration.ConfigurationSection;
import org.pistonmc.configuration.file.Config;
import org.pistonmc.entity.Entity;
import org.pistonmc.entity.builder.BuilderRegistry;
import org.pistonmc.entity.builder.DefaultBuilderRegistry;
import org.pistonmc.event.DefaultEventManager;
import org.pistonmc.event.EventManager;
import org.pistonmc.logging.Logger;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.JavaPlugin;
import org.pistonmc.plugin.JavaPluginManager;
import org.pistonmc.plugin.protocol.ProtocolManager;
import org.pistonmc.stickypiston.entity.StickyEntity.StickyEntityBuilder;
import org.pistonmc.stickypiston.exception.ExceptionHandler;
import org.pistonmc.stickypiston.network.NetworkServer;
import org.pistonmc.stickypiston.world.StickyWorldManager;
import org.pistonmc.util.ClassUtils;
import org.pistonmc.util.reflection.SimpleMethod;
import org.pistonmc.util.reflection.SimpleObject;
import org.pistonmc.world.Dimension;
import org.pistonmc.world.WorldBuilder;
import org.pistonmc.world.WorldManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class StickyServer implements Server {

    private long start;

    private Logger logger;
    private Config config;
    private EventManager events;
    private ProtocolManager protocols;
    private JavaPluginManager plugins;
    private WorldManager worlds;
    private CommandRegistry commands;
    private BuilderRegistry builders;
    private NetworkServer network;

    public StickyServer(OptionSet options, Config config) {
        new SimpleObject(Piston.class).field("server").set(this);
        this.start = System.currentTimeMillis();
        this.logger = Logging.getLogger().setFormat((SimpleDateFormat) options.valueOf("d")).setDebug((boolean) options.valueOf("debug"));
        this.config = config;
        this.events = new DefaultEventManager(logger);
        this.protocols = new ProtocolManager(Logging.getLogger("Protocol", logger), (File) options.valueOf("protocols-folder"));
        this.plugins = new JavaPluginManager(logger, (File) options.valueOf("plugins-folder"));

        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        this.protocols.reload(false);
        this.plugins.reload(false);

        String worldContainer = config.getString("settings.world-container");
        File container = worldContainer == null || worldContainer.equalsIgnoreCase("default") ? new File("") : new File(worldContainer);
        this.worlds = new StickyWorldManager(container);
        Map<String, ConfigurationSection> sections = config.getValues("worlds", ConfigurationSection.class);
        for(Entry<String, ConfigurationSection> entry : sections.entrySet()) {
            long start = System.currentTimeMillis();
            String name = entry.getKey();
            ConfigurationSection value = entry.getValue();

            String directory = value.getString("directory", name);
            File folder = new File(container, directory);

            String dimensionName = value.getString("dimension");
            Dimension dimension = Dimension.valueOf(dimensionName.toUpperCase());

            if(dimension == null) {
                getLogger().warning("Could not load \"" + name + "\" because \"" + dimensionName + "\" is not a valid dimension");
                continue;
            }

            WorldBuilder builder = new WorldBuilder(name);
            builder.folder(folder).dimension(dimension);
            builder.create();

            long finish = System.currentTimeMillis();
            double seconds = (double) (finish - start) / 1000;
            getLogger().info("Loaded \"" + name + "\" (" + seconds + "s)");
        }

        this.protocols.enable();
        this.plugins.enable();

        this.commands = new DefaultCommandRegistry();
        this.builders = new DefaultBuilderRegistry();

        InetSocketAddress address = new InetSocketAddress((String) options.valueOf("bind-ip"), (Integer) options.valueOf("p"));
        this.network = new NetworkServer(address);
    }

    protected void init() {
        network.start();
        builders.register(Entity.class, new StickyEntityBuilder());

        long finish = System.currentTimeMillis();
        double seconds = (double) (finish - start) / 1000;
        logger.info("Done (" + seconds + "s)! For help, type \"help\" or \"?\"");
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return config;
    }

    public EventManager getEventManager() {
        return events;
    }

    public ProtocolManager getProtocolManager() {
        return protocols;
    }

    public JavaPluginManager getPluginManager() {
        return plugins;
    }

    public WorldManager getWorldManager() {
        return worlds;
    }

    public void shutdown() {
        for (JavaPlugin plugin : plugins.getPlugins()) {
            plugin.setEnabled(false);
        }
    }

    public CommandRegistry getCommandRegistry() {
        return commands;
    }

    public BuilderRegistry getBuilderRegistry() {
        return builders;
    }

}
