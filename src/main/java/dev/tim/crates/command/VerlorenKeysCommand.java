package dev.tim.crates.command;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.menu.VerlorenKeysMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerlorenKeysCommand implements CommandExecutor {

    private final CratesPlugin plugin;
    private final CrateManager crateManager;

    public VerlorenKeysCommand(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Dit commando kan alleen via een speler worden uitgevoerd");
            return true;
        }

        Player player = (Player) sender;

        if(crateManager.getCrateConfig().getList("lostkeys." + player.getUniqueId()) == null){
            player.sendMessage(ChatColor.RED + "Je hebt geen verloren key(s)");
            return true;
        }

        new VerlorenKeysMenu(plugin, player);
        return true;
    }
}
