package dev.tim.crates.menu;

import dev.tim.crates.CratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VerlorenKeysMenu {

    public VerlorenKeysMenu(CratesPlugin plugin, Player player){

        YamlConfiguration crateConfig = plugin.getCrateManager().getCrateConfig();

        Inventory inventory = Bukkit.createInventory(player, 27, "Verloren Keys");

        for(Object object : crateConfig.getList("lostkeys." + player.getUniqueId())){
            ItemStack item = (ItemStack) object;
            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }

}
