package me.harsh.mbedwarskitsaddon.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {
    private final String command;

    protected SubCommand(String command) {
        this.command = command;
    }

    public void onCommand(Player player, String[] args){
        // do some shit.
    }

    public String getCommand() {
        return command;
    }
}
