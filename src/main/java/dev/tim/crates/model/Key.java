package dev.tim.crates.model;

import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Key {

    private final ItemStack key;

    public Key(String crateId, int amount, CrateManager crateManager){
        key = new ItemStack(Material.TRIPWIRE_HOOK, amount);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Crate Key");
        keyMeta.setLore(Arrays.asList(ChatColor.WHITE + "Rechtsklik op crate om te openen", ChatColor.WHITE + "Key voor: " + ChatColor.RESET + ChatUtil.translate(crateManager.getCrateConfig().getString("crates." + crateId + ".name")), crateId));
        keyMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        keyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        key.setItemMeta(keyMeta);
    }

    public ItemStack getAsItemStack(){
        return key;
    }

}
