package com.nftworlds.shop.shop.item.types;

import com.nftworlds.shop.shop.button.Category;
import com.nftworlds.shop.shop.item.ShopItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Item implements ShopItem {

    @Getter private static ArrayList<Item> shopItems = new ArrayList<>();

    private int index;
    private String id;
    private Material material;
    private Category category;

    private String displayName;
    private List<String> displayLore;
    private Map<Enchantment, Integer> displayEnchants;
    private int displayCustomModelData;

    private boolean giveItem;
    private String actualName;
    private List<String> actualLore;
    private Map<Enchantment, Integer> actualEnchants;
    private int actualCustomModelData;

    private List<String> commands;

    private double sellEss, sellWRLD, buyEss, buyWRLD;

    public Item(String id, Material material) {
        this.index = shopItems.size();
        this.id = id;
        this.material = material;
        this.displayEnchants = new HashMap<>();
        this.actualEnchants = new HashMap<>();
        this.displayLore = new ArrayList<>();
        this.actualLore = new ArrayList<>();
        shopItems.add(this);
    }

    @Override
    public void applyExtra(ItemMeta meta) {

    }
}
