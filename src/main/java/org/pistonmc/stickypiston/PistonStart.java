package org.pistonmc.stickypiston;

import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pistonmc.logging.Logging;
import org.pistonmc.plugin.protocol.Protocol;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PistonStart {

    private static Map<String, Protocol> protocols;

    static {
        protocols = new HashMap<>();
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("h", "help"), "Show this help dialog.").forHelp();
        parser.acceptsAll(asList("debug"), "Whether to show debug messages").withRequiredArg().ofType(Boolean.class).defaultsTo(false).describedAs("Debug messages");
        parser.acceptsAll(asList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
        parser.acceptsAll(asList("d", "date-format"), "Format of the date to display in the console").withRequiredArg().ofType(SimpleDateFormat.class).defaultsTo(new SimpleDateFormat("HH:mm:ss")).describedAs("Log date format");
        parser.acceptsAll(asList("properties"), "The location for the properties file").withRequiredArg().ofType(File.class).defaultsTo(new File("server.yml")).describedAs("Properties file");
        parser.acceptsAll(asList("plugins-folder"), "The location for the plugins folder").withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugins folder");
        parser.acceptsAll(asList("protocols-folder"), "The location for the protocols folder").withRequiredArg().ofType(File.class).defaultsTo(new File("protocols")).describedAs("Protocols folder");
        parser.acceptsAll(asList("bind-ip"), "The address to bind the server to").withRequiredArg().ofType(String.class).defaultsTo("0.0.0.0").describedAs("Bind address");
        parser.acceptsAll(asList("p", "port"), "The port to bind the server to").withRequiredArg().ofType(Integer.class).defaultsTo(25565).describedAs("Bind port");

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

    private static List<String> asList(String... strings) {
        return Lists.newArrayList(strings);
    }

}
