package com.nftworlds.shop.shop.item.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EnchantedBookItem extends Item {

    private boolean splash;
    private Color color;
    private Map<Enchantment, Integer> enchantments;

    public EnchantedBookItem(String id, Material material) {
        super(id, material);
        this.enchantments = new HashMap<>();
    }

    @Override
    public void applyExtra(ItemMeta itemMeta) {
        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
        if (!enchantments.isEmpty())
            enchantments.forEach((enchantment, integer) -> enchantmentStorageMeta.addStoredEnchant(enchantment, integer, true));
    }

}
