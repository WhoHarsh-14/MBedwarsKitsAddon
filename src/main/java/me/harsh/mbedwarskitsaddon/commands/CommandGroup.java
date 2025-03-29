package me.harsh.mbedwarskitsaddon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandGroup implements CommandExecutor, TabCompleter {

    final List<SubCommand> subCommands;
    final String mainCommandGroup;

    public CommandGroup(String mainCommandGroup, List<SubCommand> subCommands){
        this.mainCommandGroup = mainCommandGroup;
        this.subCommands = subCommands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player){
            final Player player = (Player) commandSender;
            if (strings.length == 1 && s.equalsIgnoreCase(mainCommandGroup)){
                for (SubCommand subCommand : subCommands) {
                    if (subCommand.getCommand().equalsIgnoreCase(strings[0])){
                        final String[] args = remove(strings.clone(), 0);
                        subCommand.onCommand(player,args);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1)
            return getCommandList();

        return new ArrayList<>();
    }

    public List<String> getCommandList(){
        final List<String> commands = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            commands.add(subCommand.getCommand());
        }
        return commands;
    }

    public String getMainCommandGroup() {
        return mainCommandGroup;
    }

    public String[] remove(String[] arr, int in) {
        if (arr == null || in < 0 || in >= arr.length) {
            return arr;
        }
        String[] arr2 = new String[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == in)
                continue;

            arr2[k++] = arr[i];
        }
        return arr2;
    }
}
