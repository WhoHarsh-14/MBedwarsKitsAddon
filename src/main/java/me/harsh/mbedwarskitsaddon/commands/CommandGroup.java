package me.harsh.mbedwarskitsaddon.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Material;
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
            if (strings.length == 0){
                KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_help"));
                return false;
            }
            System.out.println("Command used (over 1 arg)");
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getCommand().equalsIgnoreCase(strings[0])){
                    System.out.println("Sub command found! : " + strings[0]);
                    final String[] args = remove(strings.clone(), 0);
                    subCommand.onCommand(player,args);
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        final List<String> tab = new ArrayList<>();
        if ((strings[0].equalsIgnoreCase("remove") || strings[0].equalsIgnoreCase("edit"))&& strings.length == 2){
            return new ArrayList<>(KitManager.getInstance().getLoadedKits().keySet());
        }
        switch (strings.length){
            case 1:
                tab.addAll(getCommandList());
                break;
            case 2:
                tab.add("<id>");
                break;
            case 3:
                tab.add("<name>");
                break;
            case 4:
                tab.addAll(materialList());
        }
        return tab;
    }

    public List<String> getCommandList(){
        final List<String> commands = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            commands.add(subCommand.getCommand());
        }
        return commands;
    }
//    public List<String> getSubCommandArgs(int index){
//        final List<String> commands = new ArrayList<>();
//        for (SubCommand subCommand : subCommands) {
//            final String s= subCommand.getTab().get(index);
//            if (s != null)
//                commands.add(s);
//        }
//        return commands;
//    }

    private List<String> materialList(){
        final List<String> mat = new ArrayList<>();
        for (Material value : Material.values()) {
            mat.add(value.name());
        }

        return mat;
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
