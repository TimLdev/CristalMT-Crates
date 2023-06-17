package dev.tim.crates.manager;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.util.ChatUtil;
import dev.tim.crates.util.ShulkerBoxUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateManager {

    private final CratesPlugin plugin;

    private File crateFile;
    private YamlConfiguration crateConfig;

    public CrateManager(CratesPlugin plugin){
        this.plugin = plugin;
    }

    public void createCrate(String name, String createdBy, String shulkerboxColor, Location location){
        String id = String.valueOf(new Random().nextInt(9999999) + 10000);
        crateConfig.set(id + ".name", name);
        crateConfig.set(id + ".createdBy", createdBy);
        crateConfig.set(id + ".shulkerboxColor", shulkerboxColor);
        crateConfig.set(id + ".location.world", location.getWorld().getName());
        crateConfig.set(id + ".location.x", location.getX());
        crateConfig.set(id + ".location.y", location.getY());
        crateConfig.set(id + ".location.z", location.getZ());
        crateConfig.set(id + ".contents", new ArrayList<>(Arrays.asList(new ItemStack(Material.DIRT, 1))));
        saveCrateFile();

        location.getWorld().getBlockAt(location).setType(ShulkerBoxUtil.getShulkerBox(shulkerboxColor));
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0,0.3,0).add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);

        stand.setCustomNameVisible(true);
        stand.setCustomName(ChatUtil.translate(name));
    }

    public void deleteCrate(String id){
        for(String crateId : getCrateIds()){
            if(id.equals(crateId)){
                Location location = new Location(
                        Bukkit.getWorld(crateConfig.getString(crateId + ".location.world")),
                        crateConfig.getDouble(crateId + ".location.x"),
                        crateConfig.getDouble(crateId + ".location.y"),
                        crateConfig.getDouble(crateId + ".location.z"));
                location.getBlock().setType(Material.AIR);

                for(Entity entity : location.getWorld().getNearbyEntities(location, 1, 1, 1)){
                    if(entity instanceof ArmorStand){
                        entity.remove();
                    }
                }

                crateConfig.set(crateId, null);
                saveCrateFile();
                break;
            }
        }
    }

    public void giveKey(Player player, String crateId, int amount){
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, amount);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Crate Key");
        keyMeta.setLore(Arrays.asList(ChatColor.WHITE + "Rechtsklik op crate om te openen", ChatColor.WHITE + "Key voor: " + ChatColor.RESET + ChatUtil.translate(crateConfig.getString(crateId + ".name")), crateId));
        keyMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        keyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        key.setItemMeta(keyMeta);
        player.getInventory().addItem(key);
    }

    public void giveKeyAll(Player player){
        for(String crateId : getCrateIds()){
            giveKey(player, crateId, 1);
        }
    }

    public void saveCrateFile(){
        try {
            crateConfig.save(crateFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCrateFile(){
        plugin.getDataFolder().mkdir();
        crateFile = new File(plugin.getDataFolder(), "crates.yml");
        if(!crateFile.exists()){
            try {
                crateFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        crateConfig = YamlConfiguration.loadConfiguration(crateFile);
    }

    public YamlConfiguration getCrateConfig() {
        return crateConfig;
    }

    public Set<String> getCrateIds(){
        return crateConfig.getKeys(false);
    }

}
