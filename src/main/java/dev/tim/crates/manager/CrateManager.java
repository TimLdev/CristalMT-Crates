package dev.tim.crates.manager;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.model.Key;
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
        crateConfig.set("crates." + id + ".name", name);
        crateConfig.set("crates." + id + ".createdBy", createdBy);
        crateConfig.set("crates." + id + ".shulkerboxColor", shulkerboxColor);
        crateConfig.set("crates." + id + ".location.world", location.getWorld().getName());
        crateConfig.set("crates." + id + ".location.x", location.getX());
        crateConfig.set("crates." + id + ".location.y", location.getY());
        crateConfig.set("crates." + id + ".location.z", location.getZ());
        crateConfig.set("crates." + id + ".contents", new ArrayList<>(Arrays.asList(new ItemStack(Material.DIRT, 1))));
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
                        Bukkit.getWorld(crateConfig.getString("crates." + crateId + ".location.world")),
                        crateConfig.getDouble("crates." + crateId + ".location.x"),
                        crateConfig.getDouble("crates." + crateId + ".location.y"),
                        crateConfig.getDouble("crates." + crateId + ".location.z"));
                location.getBlock().setType(Material.AIR);

                for(Entity entity : location.getWorld().getNearbyEntities(location, 1, 1, 1)){
                    if(entity instanceof ArmorStand){
                        entity.remove();
                    }
                }

                crateConfig.set("crates." + crateId, null);
                saveCrateFile();
                break;
            }
        }
    }

    public void giveKey(Player player, String crateId, int amount){
        player.getInventory().addItem(new Key(crateId, amount, this).getAsItemStack());
    }

    public void addLostKey(Player player, String crateId, int amount){
        List<ItemStack> keys = new ArrayList<>();

        if(crateConfig.getList("lostkeys." + player.getUniqueId()) != null){
            for(Object object : crateConfig.getList("lostkeys." + player.getUniqueId())){
                ItemStack item = (ItemStack) object;
                keys.add(item);
            }
        }

        keys.add(new Key(crateId, amount, this).getAsItemStack());

        crateConfig.set("lostkeys." + player.getUniqueId(), keys);
        saveCrateFile();
    }

    public void giveKeyAll(Player player, int amount){
        for(String crateId : getCrateIds()){
            giveKey(player, crateId, amount);
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
        crateFile = new File(plugin.getDataFolder(), "data.yml");
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
        if(crateConfig.getConfigurationSection("crates.") == null){
            return null;
        }
        return crateConfig.getConfigurationSection("crates.").getKeys(false);
    }
}
