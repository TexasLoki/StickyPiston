package org.pistonmc.stickypiston;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pistonmc.Piston;
import org.pistonmc.configuration.file.Config;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.protocol.Protocol;
import org.pistonmc.util.reflection.SimpleObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.pistonmc.util.OtherUtils.newList;

public class PistonStart {

    private static Map<String, Protocol> protocols;

    static {
        protocols = new HashMap<>();
    }

    public static void main(String[] args) {
        File file = new File(PistonStart.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        new SimpleObject(Piston.class).field("jar").set(file);

        Config config = Config.loadFromRoot("server.yml");
        config.save();

        OptionParser parser = new OptionParser();
        parser.acceptsAll(newList("h", "help"), "Show this help dialog.").forHelp();
        parser.acceptsAll(newList("debug"), "Whether to show debug messages")
            .withRequiredArg().ofType(Boolean.class).defaultsTo(false).describedAs("Debug messages");
        parser.acceptsAll(newList("log-append"), "Whether to append to the log file")
            .withRequiredArg().ofType(Boolean.class).defaultsTo(config.getBoolean("settings.debug")).describedAs("Log append");
        parser.acceptsAll(newList("o", "offline"), "Whether to enable offline mode")
            .withRequiredArg().ofType(Boolean.class).defaultsTo(config.getBoolean("settings.offline")).describedAs("Offline mode");
        parser.acceptsAll(newList("d", "date-format"), "Format of the date to display in the console")
            .withRequiredArg().ofType(SimpleDateFormat.class).defaultsTo(new SimpleDateFormat("HH:mm:ss")).describedAs("Log date format");
        parser.acceptsAll(newList("plugins-folder"), "The location for the plugins folder")
            .withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugins folder");
        parser.acceptsAll(newList("protocols-folder"), "The location for the protocols folder")
            .withRequiredArg().ofType(File.class).defaultsTo(new File("protocols")).describedAs("Protocols folder");
        parser.acceptsAll(newList("bind-ip"), "The address to bind the server to")
            .withRequiredArg().ofType(String.class).defaultsTo(config.getString("host")).describedAs("Bind address");
        parser.acceptsAll(newList("p", "port"), "The port to bind the server to")
            .withRequiredArg().ofType(Integer.class).defaultsTo(config.getInteger("port")).describedAs("Bind port");
        parser.acceptsAll(newList("max", "max-players"), "The maximum amount of players")
            .withRequiredArg().ofType(Integer.class).defaultsTo(config.getInteger("settings.max-players")).describedAs("Maximum player count");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch(OptionException ex) {
            ex.printStackTrace();
            return;
        }

        if(options.has("h")) {
            try {
                parser.printHelpOn(System.out);
            } catch(IOException ex) {
                Logging.getLogger().log("Could not display help: ", ex);
            }

            return;
        }

        new StickyServer(options).init();
    }

}
