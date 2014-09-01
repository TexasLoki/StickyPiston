package org.pistonmc.stickypiston.commands;

import org.pistonmc.commands.Command;
import org.pistonmc.commands.CommandArguments;
import org.pistonmc.commands.CommandSender;

public class TestCommand implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"test"};
    }

    @Override
    public String getDescription() {
        return "A test command";
    }

    @Override
    public String getUsage() {
        return "[arguments]";
    }

    @Override
    public void onExecute(CommandArguments args, CommandSender sender) {
        for (String s : args.getArguments()) {
            sender.sendMessage(s);
        }
    }

}
