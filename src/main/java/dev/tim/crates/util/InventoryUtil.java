package dev.tim.crates.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    public static boolean hasAvailableSlot(Player player){
        Inventory inv = player.getInventory();
        for (ItemStack i : inv.getStorageContents()) {
            if (i == null) {
                return true;
            }
        }
        return false;
    }

}
