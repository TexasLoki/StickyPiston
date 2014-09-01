package org.pistonmc.stickypiston.commands;

import org.pistonmc.Piston;
import org.pistonmc.commands.Command;
import org.pistonmc.commands.CommandArguments;
import org.pistonmc.commands.CommandSender;

public class StopCommand implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"stop"};
    }

    @Override
    public String getDescription() {
        return "Stop the server";
    }

    @Override
    public String getUsage() {
        return "[message]";
    }

    @Override
    public void onExecute(CommandArguments args, CommandSender sender) {
        Piston.shutdown(args.getJoinedString());
    }

}
