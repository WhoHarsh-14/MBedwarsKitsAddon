package me.harsh.mbedwarskitsaddon.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public abstract class SubCommand {
    private final String command;
    private Map<Integer, String> tab = new HashMap<>();

    protected SubCommand(String command) {
        this.command = command;
    }

    public void onCommand(Player player, String[] args){
        // do some shit.
    }

    public Map<Integer, String> getTab() {
        return tab;
    }

    public String getCommand() {
        return command;
    }
}
