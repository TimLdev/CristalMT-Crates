package dev.tim.crates.manager;

import dev.tim.crates.CratesPlugin;
import dev.tim.crates.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinningManager {

    private final CratesPlugin plugin;
    private final CrateManager crateManager;
    private final List<Inventory> inventories;

    private int itemIndex = 0;

    public SpinningManager(CratesPlugin plugin){
        this.plugin = plugin;
        this.crateManager = plugin.getCrateManager();
        inventories = new ArrayList<>();
    }

    public void setupInventory(Inventory inv, String crateId){
        List<ItemStack> rewards = new ArrayList<>();
        for(Object object : crateManager.getCrateConfig().getList(crateId + ".contents")){
            rewards.add((ItemStack) object);
        }

        int startSlot = new Random().nextInt(rewards.size());

        for(int i = 0; i < startSlot; i++){
            for(int items = 9; items < 18; items++){
                inv.setItem(items, rewards.get((items + itemIndex) % rewards.size()));
            }
            itemIndex++;
        }

        ItemStack frame = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta meta = frame.getItemMeta();
        meta.setDisplayName(" ");
        frame.setItemMeta(meta);

        for(int i : new int[]{0,1,2,3,5,6,7,8,18,19,20,21,22,23,24,25,26}){
            inv.setItem(i, frame);
        }

        ItemStack hopper = new ItemStack(Material.HOPPER);
        ItemMeta hopperMeta = hopper.getItemMeta();
        hopperMeta.setDisplayName(" ");
        hopper.setItemMeta(hopperMeta);
        inv.setItem(4, hopper);
    }

    public void spin(Player player, String crateId){
        Inventory inventory = Bukkit.createInventory(player, 27, ChatUtil.translate(crateManager.getCrateConfig().getString(crateId + ".name")));
        setupInventory(inventory, crateId);
        inventories.add(inventory);
        player.openInventory(inventory);

        List<ItemStack> rewards = new ArrayList<>();
        for(Object object : crateManager.getCrateConfig().getList(crateId + ".contents")){
            rewards.add((ItemStack) object);
        }

        Random random = new Random();
        double seconds = 3 + (5 - 3) * random.nextDouble();

        new BukkitRunnable(){
            double delay = 0;
            int ticks = 0;
            boolean done = false;

            @Override
            public void run() {
                if(done){
                    return;
                }
                ticks++;
                delay += 1 / (20 * seconds);
                if(ticks > delay * 10){
                    ticks = 0;

                    for(int items = 9; items < 18; items++){
                        inventory.setItem(items, rewards.get((items + itemIndex) % rewards.size()));
                    }
                    itemIndex++;

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);

                    if(delay >= 0.5){
                        done = true;
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                ItemStack item = inventory.getItem(13);
                                player.getInventory().addItem(item);
                                player.updateInventory();
                                player.closeInventory();
                                cancel();
                            }
                        }.runTaskLater(plugin, 50);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);

    }

    public List<Inventory> getInventories() {
        return inventories;
    }
}
