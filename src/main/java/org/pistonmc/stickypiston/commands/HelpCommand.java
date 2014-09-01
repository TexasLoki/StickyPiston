package org.pistonmc.stickypiston.commands;

import org.pistonmc.Piston;
import org.pistonmc.commands.Command;
import org.pistonmc.commands.CommandArguments;
import org.pistonmc.commands.CommandSender;

public class HelpCommand implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"help", "?"};
    }

    @Override
    public String getDescription() {
        return "Lists all registered commands";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void onExecute(CommandArguments args, CommandSender sender) {
        for (Command c : Piston.getCommandRegistry().getCommands()) {
            sender.sendMessage(c.getAliases()[0] + (c.getUsage().equals("") ? "" : " " + c.getUsage()) + " - " + c.getDescription());
        }
    }

}
