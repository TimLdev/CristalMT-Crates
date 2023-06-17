package dev.tim.crates.menu;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.util.ChatUtil;
import dev.tim.crates.util.ShulkerBoxUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CrateListMenu {

    public CrateListMenu(CratesPlugin plugin, Player player){

        Inventory inventory = Bukkit.createInventory(player, 54, "Crate Lijst");

        YamlConfiguration crateConfig = plugin.getCrateManager().getCrateConfig();

        for(String crateId : plugin.getCrateManager().getCrateIds()){
            ItemStack item = new ItemStack(ShulkerBoxUtil.getShulkerBox(crateConfig.getString(crateId + ".shulkerboxColor")));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(crateId);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Naam: " + ChatColor.RESET + ChatUtil.translate(crateConfig.getString(crateId + ".name")));
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Gemaakt Door: " + ChatColor.WHITE + crateConfig.getString(crateId + ".createdBy"));
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Shulkerbox kleur: " + ChatColor.WHITE + crateConfig.getString(crateId + ".shulkerboxColor"));
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Locatie: " + ChatColor.WHITE + "Wereld: " + crateConfig.getString(crateId + ".location.world") + " X: " + crateConfig.getString(crateId + ".location.x") + " Y: " + crateConfig.getString(crateId + ".location.y") + " Z: " + crateConfig.getString(crateId + ".location.z"));
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Rechtsklik om te verwijderen");
            lore.add(ChatColor.GRAY + "Linksklik om de inhoud te veranderen");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        player.openInventory(inventory);

    }

}
