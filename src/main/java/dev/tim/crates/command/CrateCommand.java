package dev.tim.crates.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrateCommand implements CommandExecutor{

    private final CratesPlugin plugin;
    private final CrateManager crateManager;
    private final Cache<UUID, Long> keyAllCooldown;

    public CrateCommand(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
        keyAllCooldown = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if(args.length == 1){
            if(args[0].equals("list")){
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
                case "keyall":
                    if(!sender.hasPermission("crate.keyall")){
                        sender.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                        return true;
                    }

                    if(crateManager.getCrateIds() == null){
                        sender.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    if(crateManager.getCrateIds().size() == 0){
                        sender.sendMessage(ChatColor.RED + "Er zijn nog geen crates gemaakt");
                        return true;
                    }

                    if(StringIntegerUtil.isInteger(args[1])){
                        int amount = Integer.parseInt(args[1]);
                        if(amount > 5 || amount < 1){
                            sender.sendMessage(ChatColor.RED + "Het aantal keys mag niet hoger dan 5 en lager dan 1");
                            return true;
                        }

                        if(sender instanceof Player){
                            player = (Player) sender;
                            if(!keyAllCooldown.asMap().containsKey(player.getUniqueId())){
                                keyAllCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 60000);
                            } else {
                                long distance = (keyAllCooldown.asMap().get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                                player.sendMessage(ChatColor.RED + "Wacht " + distance + " seconden om dit opnieuw te kunnen gebruiken");
                                return true;
                            }
                        }

                        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                            if(InventoryUtil.hasAvailableSlot(onlinePlayer)){
                                crateManager.giveKeyAll(onlinePlayer, amount);
                                onlinePlayer.sendMessage(ChatColor.GREEN + "Je hebt " + amount + " crate key(s) ontvangen");
                            } else {
                                for(String crateId : crateManager.getCrateIds()){
                                    crateManager.addLostKey(onlinePlayer, crateId, amount);
                                }
                                onlinePlayer.sendMessage(ChatColor.GREEN + "Je hebt " + amount + " crate key(s) ontvangen, maar je inventory zit vol dus zitten de key(s) bij " + ChatColor.YELLOW + "/verlorenkeys");
                            }
                        }

                        sender.sendMessage(ChatColor.GREEN + "Iedereen heeft " + amount + " key(s) gekregen voor elke crate");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Het aantal moet een geheel nummer zijn");
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
                        int amount = Integer.parseInt(args[3]);
                        if(amount < 1){
                            sender.sendMessage(ChatColor.RED + "Het aantal keys mag niet lager zijn dan 1");
                            return true;
                        }

                        if(InventoryUtil.hasAvailableSlot(target)){
                            crateManager.giveKey(target, args[2], amount);
                            target.sendMessage(ChatColor.GREEN + "Je hebt " + amount + " crate key(s) ontvangen");
                        } else {
                            crateManager.addLostKey(target, args[2], amount);
                            target.sendMessage(ChatColor.GREEN + "Je hebt " + amount + " crate key(s) ontvangen, maar je inventory zit vol dus zitten de key(s) bij " + ChatColor.YELLOW + "/verlorenkeys");
                        }
                        sender.sendMessage(ChatColor.GREEN.toString() + amount + " crate key(s) gegeven aan " + target.getDisplayName());
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
