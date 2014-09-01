package org.pistonmc.stickypiston.commands;

import org.pistonmc.ChatColor;
import org.pistonmc.Piston;
import org.pistonmc.commands.Command;
import org.pistonmc.commands.CommandArguments;
import org.pistonmc.commands.CommandRegistry;
import org.pistonmc.commands.CommandSender;
import org.pistonmc.event.command.CommandPreProcessEvent;
import org.pistonmc.util.OtherUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultCommandRegistry implements CommandRegistry {

    private List<Command> commandList;

    public DefaultCommandRegistry() {
        commandList = new ArrayList<>();

        addCommand(new HelpCommand());
        addCommand(new TestCommand());
        addCommand(new StopCommand());
    }

    @Override
    public void execute(String[] args, CommandSender sender) {
        if (args.length < 1) {
            return;
        }

        String label = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        Command command = null;
        List<Command> list = OtherUtils.reverse(commandList);
        for (Command cmd : list) {
            for (String s : cmd.getAliases()) {
                if (s.equalsIgnoreCase(label)) {
                    command = cmd;
                }
            }
        }

        CommandArguments arguments = new CommandArguments(label, args);

        CommandPreProcessEvent event = new CommandPreProcessEvent(command, arguments, sender);
        Piston.getEventManager().call(event);
        command = event.getCommand();

        if (event.isCancelled()) {
            return;
        }

        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Unknown command. Use \"/help\" for a list of commands.");
        } else {
            try {
                command.onExecute(arguments, sender);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "There was an error while processing your command.");
                Piston.getLogger().debug(e);
            }
        }
    }

    @Override
    public Command addCommand(Command command) {
        commandList.add(command);
        return command;
    }

    @Override
    public Command addCommand(Class<?> cls) {
        return null;
    }

    @Override
    public Command build(Class<?> cls) {
        return null;
    }

    @Override
    public Collection<Command> getCommands() {
        return commandList;
    }

}