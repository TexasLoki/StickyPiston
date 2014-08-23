package org.pistonmc.stickypiston;

import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pistonmc.logging.Logging;
import org.pistonmc.stickypiston.network.protocol.Protocol;

import java.io.File;
import java.io.IOException;
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
        parser.acceptsAll(asList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
        parser.acceptsAll(asList("properties"), "The location for the properties file").withRequiredArg().ofType(File.class).defaultsTo(new File("server.yml")).describedAs("Properties file");
        parser.acceptsAll(asList("plugins-folder"), "The location for the plugins folder").withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugins folder");
        parser.acceptsAll(asList("protocols-folder"), "The location for the protocols folder").withRequiredArg().ofType(File.class).defaultsTo(new File("protocols")).describedAs("Protocols folder");

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

        new StickyServer(options);
    }

    private static List<String> asList(String... strings) {
        return Lists.newArrayList(strings);
    }

}
