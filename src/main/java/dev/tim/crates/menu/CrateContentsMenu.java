package dev.tim.crates.menu;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CrateContentsMenu {

    public CrateContentsMenu(CratesPlugin plugin, String crateId, Player player, boolean managing){

        YamlConfiguration crateConfig = plugin.getCrateManager().getCrateConfig();

        Inventory inventory;

        if(managing){
            inventory = Bukkit.createInventory(player, 27, "Inhoud-" + crateId);
        } else {
            inventory = Bukkit.createInventory(player, 27, "Beloningen van " + ChatUtil.translate(crateConfig.getString("crates." + crateId + ".name")));
        }

        for(Object object : crateConfig.getList("crates." + crateId + ".contents")){
            ItemStack item = (ItemStack) object;
            inventory.addItem(item);
        }

        player.openInventory(inventory);

    }

}
