package com.nftworlds.shop.shop.button;

import com.nftworlds.shop.util.DataUtil;
import com.nftworlds.shop.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Category {

    @Getter private static ArrayList<Category> categories = new ArrayList<>();

    private String id;
    private int slot;
    private ItemStack itemStack;

    public Category(String id, int slot, String item, String display, List<String> lore) {
        this.id = id;
        this.slot = slot;
        this.itemStack = ItemBuilder.build(Material.valueOf(item), display, lore, true);
        DataUtil.setInteger(itemStack, id, 1);
        categories.add(this);
    }

    public static Category getByID(String id) {
        for (Category category : categories) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

}
