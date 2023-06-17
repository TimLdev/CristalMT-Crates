package dev.tim.crates.command;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.menu.CrateContentsMenu;
import dev.tim.crates.menu.CrateListMenu;
import dev.tim.crates.util.InventoryUtil;
import dev.tim.crates.util.ShulkerBoxUtil;
import dev.tim.crates.util.StringIntegerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor{

    private final CratesPlugin plugin;
    private final CrateManager crateManager;

    public CrateCommand(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if(args.length == 1){
            switch (args[0]){
                case "keyall":
                    if(!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.RED + "Dit commando kan alleen via een speler worden uitgevoerd");
                        return true;
                    }

                    player = (Player) sender;

                    if(!player.hasPermission("crate.keyall")){
                        player.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                        return true;
                    }

                    if(crateManager.getCrateIds() == null){
                        player.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    if(crateManager.getCrateIds().size() == 0 ){
                        player.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                    } else {
                        crateManager.giveKeyAll(player);
                        player.sendMessage(ChatColor.GREEN + "Key(s) gekregen");
                    }
                    return true;
                case "list":
                    if(!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.RED + "Dit commando kan alleen via een speler worden uitgevoerd");
                        return true;
                    }

                    player = (Player) sender;

                    if(!player.hasPermission("crate.list")){
                        player.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                        return true;
                    }

                    if(crateManager.getCrateIds() == null){
                        player.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    if(crateManager.getCrateIds().size() == 0){
                        player.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                    } else {
                        new CrateListMenu(plugin, player);
                    }
                    return true;
            }
        }

        if(args.length == 2){
            switch (args[0]){
                case "delete":
                    if(!sender.hasPermission("crate.delete")){
                        sender.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                        return true;
                    }

                    if(crateManager.getCrateIds() == null){
                        sender.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    int i = 0;
                    for(String crateId : crateManager.getCrateIds()){
                        if(!(crateId.equals(args[1]))){
                            i++;
                        }
                    }
                    if(i == crateManager.getCrateIds().size()){
                        sender.sendMessage(ChatColor.RED + "Crate niet gevonden");
                    } else {
                        crateManager.deleteCrate(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "Crate verwijdert");
                    }
                    return true;
                case "contents":
                    if(!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.RED + "Dit commando kan alleen via een speler worden uitgevoerd");
                        return true;
                    }

                    player = (Player) sender;

                    if(!player.hasPermission("crate.contents")){
                        player.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                        return true;
                    }

                    if(crateManager.getCrateIds() == null){
                        player.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    int j = 0;
                    for(String crateId : crateManager.getCrateIds()){
                        if(!(crateId.equals(args[1]))){
                            j++;
                        }
                    }
                    if(j == crateManager.getCrateIds().size()){
                        player.sendMessage(ChatColor.RED + "Crate niet gevonden");
                    } else {
                        new CrateContentsMenu(plugin, args[1], player, true);
                    }
                    return true;
            }
        }

        if(args.length >= 3){
            if(args[0].equals("create")){
                if(!(sender instanceof Player)){
                    sender.sendMessage(ChatColor.RED + "Dit commando kan alleen via een speler worden uitgevoerd");
                    return true;
                }

                player = (Player) sender;

                if(!player.hasPermission("crate.create")){
                    player.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                    return true;
                }

                int i = 0;
                for(String color : ShulkerBoxUtil.getShulkerColors()){
                    if(!(args[1].equals(color))){
                        i++;
                    }
                }
                if(i == ShulkerBoxUtil.getShulkerColors().size()){
                    player.sendMessage(ChatColor.RED + "Shulkerbox kleur niet gevonden! Opties:");
                    player.sendMessage(ShulkerBoxUtil.getShulkerColors().toString());
                } else {
                    StringBuilder builder = new StringBuilder();
                    for(int j = 2; j < args.length; j++){
                        builder.append(args[j]).append(" ");
                    }
                    crateManager.createCrate(builder.toString(), player.getName(), args[1], player.getLocation().getBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Crate aangemaakt");
                }
                return true;
            }
        }

        if(args.length == 4){
            if(args[0].equals("givekey")){
                if(!sender.hasPermission("crate.givekey")){
                    sender.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                    return true;
                }

                if(crateManager.getCrateIds() == null){
                    sender.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(ChatColor.RED + "Speler niet gevonden");
                    return true;
                }

                int j = 0;
                for(String crateId : crateManager.getCrateIds()){
                    if(!(crateId.equals(args[2]))){
                        j++;
                    }
                }
                if(j == crateManager.getCrateIds().size()){
                    sender.sendMessage(ChatColor.RED + "Crate niet gevonden");
                } else {
                    if(StringIntegerUtil.isInteger(args[3])){
                        if(InventoryUtil.hasAvailableSlot(target)){
                            crateManager.giveKey(target, args[2], Integer.parseInt(args[3]));
                            target.sendMessage(ChatColor.GREEN + "Je hebt " + args[3] + " crate key(s) ontvangen");
                        } else {
                            crateManager.addLostKey(target, args[2], Integer.parseInt(args[3]));
                            target.sendMessage(ChatColor.GREEN + "Je hebt " + args[3] + " crate key(s) ontvangen, maar je inventory zit vol dus zitten de key(s) bij " + ChatColor.YELLOW + "/verlorenkeys");
                        }
                        sender.sendMessage(ChatColor.GREEN + args[3] + " crate key(s) gegeven aan " + target.getDisplayName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Het aantal moet een geheel nummer zijn");
                    }
                }
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Crates plugin");
        sender.sendMessage(ChatColor.RED + "Gemaakt door: youtube.com/@zwess");
        return false;
    }

}
