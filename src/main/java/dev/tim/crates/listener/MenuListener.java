package dev.tim.crates.listener;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.menu.CrateContentsMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {

    private final CratesPlugin plugin;
    private final CrateManager crateManager;

    public MenuListener(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();

        if(!(title.contains("Inhoud-"))){
            return;
        }

        String crateId = title.replaceAll("Inhoud-", "");
        Player player = (Player) event.getPlayer();

        List<ItemStack> contents = new ArrayList<>();

        for(ItemStack item : event.getInventory().getContents()){
            if(item == null){
                continue;
            }
            contents.add(item);
        }

        crateManager.getCrateConfig().set(crateId + ".contents", contents);
        crateManager.saveCrateFile();

        player.sendMessage(ChatColor.GREEN + "Crate inhoud aangepast");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();

        if(event.getView().getTitle().contains("Beloningen")){
            event.setCancelled(true);
            return;
        }

        if(plugin.getSpinningManager().getInventories().contains(event.getInventory())){
            event.setCancelled(true);
        }

        if(!(event.getView().getTitle().equals("Crate Lijst")) || item == null){
            return;
        }

        event.setCancelled(true);

        if(item.getItemMeta() != null && item.getType().name().contains("SHULKER")){
            Player player = (Player) event.getWhoClicked();
            String crateId = event.getCurrentItem().getItemMeta().getDisplayName();

            if(event.getClick() == ClickType.RIGHT){
                crateManager.deleteCrate(crateId);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Crate verwijdert");

            } else if(event.getClick() == ClickType.LEFT){
                if(!player.hasPermission("crate.contents")){
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Je hebt geen permissie voor dit commando");
                    return;
                }
                new CrateContentsMenu(plugin, crateId, player, true);
            }
        }
    }

}
