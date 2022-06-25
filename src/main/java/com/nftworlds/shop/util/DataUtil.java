package com.nftworlds.shop.util;

import com.nftworlds.shop.Shop;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DataUtil {

    public static void setString(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Shop.getInstance(), key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void setInteger(ItemStack item, String key, int value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Shop.getInstance(), key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    public static void setDouble(ItemStack item, String key, double value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Shop.getInstance(), key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    public static String getString(ItemStack item, String key) {
        NamespacedKey namedKey = new NamespacedKey(Shop.getInstance(), key);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(namedKey, PersistentDataType.STRING, null);
    }

    public static int getInteger(ItemStack item, String key) {
        NamespacedKey namedKey = new NamespacedKey(Shop.getInstance(), key);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(namedKey, PersistentDataType.INTEGER, 0);
    }

    public static double getDouble(ItemStack item, String key) {
        NamespacedKey namedKey = new NamespacedKey(Shop.getInstance(), key);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(namedKey, PersistentDataType.DOUBLE, 0.0);
    }

    public static boolean hasInteger(ItemStack item, String key) {
        NamespacedKey namedKey = new NamespacedKey(Shop.getInstance(), key);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(namedKey, PersistentDataType.INTEGER);
    }

}
