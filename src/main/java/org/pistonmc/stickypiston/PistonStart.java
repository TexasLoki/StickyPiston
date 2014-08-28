package org.pistonmc.stickypiston;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.protocol.Protocol;

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
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newList("h", "help"), "Show this help dialog.").forHelp();
        parser.acceptsAll(newList("debug"), "Whether to show debug messages").withRequiredArg().ofType(Boolean.class).defaultsTo(false).describedAs("Debug messages");
        parser.acceptsAll(newList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
        parser.acceptsAll(newList("d", "date-format"), "Format of the date to display in the console").withRequiredArg().ofType(SimpleDateFormat.class).defaultsTo(new SimpleDateFormat("HH:mm:ss")).describedAs("Log date format");
        parser.acceptsAll(newList("properties"), "The location for the properties file").withRequiredArg().ofType(File.class).defaultsTo(new File("server.yml")).describedAs("Properties file");
        parser.acceptsAll(newList("plugins-folder"), "The location for the plugins folder").withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugins folder");
        parser.acceptsAll(newList("protocols-folder"), "The location for the protocols folder").withRequiredArg().ofType(File.class).defaultsTo(new File("protocols")).describedAs("Protocols folder");
        parser.acceptsAll(newList("bind-ip"), "The address to bind the server to").withRequiredArg().ofType(String.class).defaultsTo("0.0.0.0").describedAs("Bind address");
        parser.acceptsAll(newList("p", "port"), "The port to bind the server to").withRequiredArg().ofType(Integer.class).defaultsTo(25565).describedAs("Bind port");

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
