package com.nftworlds.shop.shop.item.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TippedArrowItem extends Item {

    private boolean splash;
    private Color color;
    private List<PotionEffect> potionEffects;

    public TippedArrowItem(String id, Material material) {
        super(id, material);
        this.potionEffects = new ArrayList<>();
    }

    @Override
    public void applyExtra(ItemMeta itemMeta) {
        PotionMeta potionMeta = (PotionMeta) itemMeta;
        potionMeta.setColor(color);
        for (PotionEffect potionEffect : potionEffects) {
            potionMeta.addCustomEffect(potionEffect, true);
        }
    }
}
