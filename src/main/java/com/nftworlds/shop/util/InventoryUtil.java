package com.nftworlds.shop.util;

import com.nftworlds.shop.shop.item.types.Item;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtil {

    public static boolean hasSpace(Inventory inventory, Item shopItem, int amount) {
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return true;
            } else {
                if (itemStack.getType() != shopItem.getMaterial()) {
                    continue;
                }
                if (itemStack.getAmount() >= itemStack.getMaxStackSize()) {
                    continue;
                }
                ItemMeta meta = itemStack.getItemMeta();
                if (meta.hasDisplayName() && shopItem.getActualName() == null || !meta.hasDisplayName() && shopItem.getActualName() != null) {
                    continue;
                }
                if (shopItem.getActualName() != null && !meta.getDisplayName().equals(shopItem.getActualName())) {
                    continue;
                }
                if (meta.hasLore() && shopItem.getActualLore().isEmpty() || !meta.hasLore() && !shopItem.getActualLore().isEmpty()) {
                    continue;
                }
                if (!shopItem.getActualLore().isEmpty() && !meta.getLore().equals(shopItem.getActualLore())) {
                    continue;
                }
                if (!meta.getEnchants().equals(shopItem.getActualEnchants())) {
                    continue;
                }

                amount -= itemStack.getMaxStackSize() - itemStack.getAmount();
            }
            if (amount <= 0) {
                return true;
            }
        }
        return false;
    }
}