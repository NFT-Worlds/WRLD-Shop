package com.nftworlds.shop.util;

import com.nftworlds.shop.shop.item.types.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static boolean isMatch(ItemStack itemStack, Item shopItem, boolean buying) {
        if (itemStack == null) return false;
        if (itemStack.getType() != shopItem.getMaterial()) {
            return false;
        }
        if (buying && itemStack.getAmount() >= itemStack.getMaxStackSize()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasDisplayName() && shopItem.getActualName() == null || !meta.hasDisplayName() && shopItem.getActualName() != null) {
            return false;
        }
        if (shopItem.getActualName() != null && !meta.getDisplayName().equals(shopItem.getActualName())) {
            return false;
        }
        if (meta.hasLore() && shopItem.getActualLore().isEmpty() || !meta.hasLore() && !shopItem.getActualLore().isEmpty()) {
            return false;
        }
        if (!shopItem.getActualLore().isEmpty() && !meta.getLore().equals(shopItem.getActualLore())) {
            return false;
        }
        if (!meta.getEnchants().equals(shopItem.getActualEnchants())) {
            return false;
        }
        return true;
    }

}
