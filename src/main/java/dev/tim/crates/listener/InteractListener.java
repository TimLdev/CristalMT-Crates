package dev.tim.crates.listener;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.menu.CrateContentsMenu;
import dev.tim.crates.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final CratesPlugin plugin;
    private final CrateManager crateManager;

    public InteractListener(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getClickedBlock() == null || (!event.getClickedBlock().getType().name().contains("SHULKER"))){
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if(crateManager.getCrateIds() == null){
            return;
        }

        for(String crateId : crateManager.getCrateIds()){
            Location location = new Location(
                    Bukkit.getWorld(crateManager.getCrateConfig().getString("crates." + crateId + ".location.world")),
                    crateManager.getCrateConfig().getDouble("crates." + crateId + ".location.x"),
                    crateManager.getCrateConfig().getDouble("crates." + crateId + ".location.y"),
                    crateManager.getCrateConfig().getDouble("crates." + crateId + ".location.z"));

            Block crate = location.getWorld().getBlockAt(location);

            if(clickedBlock.equals(crate)){
                Player player = event.getPlayer();

                event.setCancelled(true);

                if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if(item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null &&
                    item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Crate Key") &&
                    item.getItemMeta().getLore() != null && item.getItemMeta().getLore().get(2).equals(crateId)){
                        if(InventoryUtil.hasAvailableSlot(player)){
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                            plugin.getSpinningManager().spin(player, crateId);
                        } else {
                            player.sendMessage(ChatColor.RED + "Je inventory zit vol");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Je hebt de juiste key nodig om deze crate te openen");
                    }
                } else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                    new CrateContentsMenu(plugin, crateId, player, false);
                }
                break;
            }
        }
    }

}
