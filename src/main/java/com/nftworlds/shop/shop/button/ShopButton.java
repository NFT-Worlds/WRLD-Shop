package com.nftworlds.shop.shop.button;

import com.nftworlds.shop.util.DataUtil;
import com.nftworlds.shop.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

@Getter
public class ShopButton {

    @Getter private static HashMap<ButtonType, ShopButton> buttons = new HashMap<>();

    private ItemStack itemStack;
    private int slot;
    private ButtonType buttonType;

    public ShopButton(ButtonType id, String item, int slot, String display, List<String> lore) {
        this.slot = slot;
        this.buttonType = id;
        this.itemStack = ItemBuilder.build(Material.valueOf(item), display, lore, true);
        DataUtil.setInteger(itemStack, id.name(), 1);
        buttons.put(id, this);
    }

    public enum ButtonType {
        PREVIOUS,
        NEXT,
        TOGGLE_BUY,
        TOGGLE_SELL,
        TOGGLE_WRLD,
        TOGGLE_ESS,
        DECREASE_AMOUNT,
        INCREASE_AMOUNT,
        MAIN_MENU
    }

}
