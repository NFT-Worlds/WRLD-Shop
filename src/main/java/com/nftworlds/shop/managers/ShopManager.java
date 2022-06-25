package com.nftworlds.shop.managers;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.config.ShopConfig;
import com.nftworlds.shop.shop.button.Category;
import com.nftworlds.shop.shop.button.ShopButton;
import com.nftworlds.shop.shop.item.types.Item;
import com.nftworlds.shop.shop.transactions.CurrencyType;
import com.nftworlds.shop.shop.transactions.TransactionType;
import com.nftworlds.shop.util.ColorUtil;
import com.nftworlds.shop.util.DataUtil;
import com.nftworlds.shop.util.InventoryUpdate;
import com.nftworlds.shop.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class ShopManager {

    private final Shop plugin;
    private final ShopConfig config;

    private String name;
    private ItemStack filler, fillerBuy, fillerSell;

    public ShopManager() {
        this.plugin = Shop.getInstance();
        this.config = plugin.getShopConfig();
        this.name = ColorUtil.rgb(config.getMainMenuName());
        this.filler = ItemBuilder.build(config.getFiller(), " ");
        this.fillerBuy = ItemBuilder.build(config.getFillerBuy(), " ");
        this.fillerSell = ItemBuilder.build(config.getFillerSell(), " ");
    }

    public void openMenu(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 54, getName());
        populate(inventory, p, null, 1, 1, TransactionType.BUY, CurrencyType.WRLD);
        p.openInventory(inventory);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
    }

    public void populate(Inventory inventory, Player p, Category category, int page, int amount, TransactionType transactionType, CurrencyType currencyType) {
        p.playSound(p.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 1);

        InventoryUpdate.updateInventory(plugin, p, name + (category == null ? "" : ColorUtil.rgb((transactionType == TransactionType.BUY ? " &8| &a&lBuy" : " &8| &c&lSell") + "&8 | " + (currencyType == CurrencyType.WRLD ? "&e&l$WRLD" : "&2&l$"))));

        if (category == null) { //Main menu
            populateBorder(inventory, config.isUseCategories() ? filler : transactionType == TransactionType.BUY ? fillerBuy : fillerSell);
            clearItems(inventory);
            if (config.isUseCategories()) { //Normal main menu
                Category.getCategories().forEach(c -> {
                    ItemStack itemStack = c.getItemStack().clone();
                    DataUtil.setString(itemStack, "transactionType", transactionType.name());
                    DataUtil.setString(itemStack, "currencyType", currencyType.name());
                    DataUtil.setInteger(itemStack, "amount", amount);
                    inventory.setItem(c.getSlot(), itemStack);
                });
            } else { //All items without categories
                populateButtons(inventory, transactionType, currencyType, null, page, amount);
                populateItems(inventory, null, page, amount, transactionType, currencyType);
            }
        } else { //Category menu
            populateBorder(inventory, transactionType == TransactionType.BUY ? fillerBuy : fillerSell);
            populateButtons(inventory, transactionType, currencyType, category, page, amount);
            populateItems(inventory, category, page, amount, transactionType, currencyType);
        }
    }

    public void populateItems(Inventory inventory, Category category, int page, int amount, TransactionType transactionType, CurrencyType currencyType) {
        int start = 10;
        int increment = 0;
        for (Item item : Item.getShopItems().stream()
                .filter(shopItem -> category == null || shopItem.getCategory().getId().equals(category.getId()))
                .filter(!plugin.getShopConfig().isCompactMenus()
                        ? shopItem -> transactionType == TransactionType.BUY ? shopItem.getBuyWRLD() != 0 || shopItem.getBuyEss() != 0 : shopItem.getSellWRLD() != 0 || shopItem.getSellEss() != 0
                        : shopItem -> transactionType == TransactionType.BUY ? currencyType == CurrencyType.WRLD ? shopItem.getBuyWRLD() != 0 : shopItem.getBuyEss() != 0 : currencyType == CurrencyType.WRLD ? shopItem.getSellWRLD() != 0 : shopItem.getSellEss() != 0)
                .skip(28L * (page - 1)).limit(28).toList()) {
            inventory.setItem(start + increment, ItemBuilder.build(item, amount, transactionType, currencyType));
            if (increment++ == 6) {
                increment = 0;
                start += 9;
            }
        }
        while (start + increment < 45) {
            inventory.setItem(start + increment, null);
            if (increment++ == 6) {
                increment = 0;
                start += 9;
            }
        }
    }

    public void clearItems(Inventory inventory) {
        int start = 10;
        int increment = 0;
        while (start + increment < 45) {
            inventory.setItem(start + increment, null);
            if (increment++ == 6) {
                increment = 0;
                start += 9;
            }
        }
    }

    public void populateBorder(Inventory inventory, ItemStack filler) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
        }
        inventory.setItem(9, filler);
        inventory.setItem(17, filler);
        inventory.setItem(18, filler);
        inventory.setItem(26, filler);
        inventory.setItem(27, filler);
        inventory.setItem(35, filler);
        inventory.setItem(36, filler);
        inventory.setItem(44, filler);
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, filler);
        }
    }

    public void populateButtons(Inventory inventory, TransactionType transactionType, CurrencyType currencyType, Category category, int page, int amount) {
        for (ShopButton shopButton : ShopButton.getButtons().values()) {
            if (transactionType == TransactionType.BUY && shopButton.getButtonType() == ShopButton.ButtonType.TOGGLE_BUY)
                continue;
            if (transactionType == TransactionType.SELL && shopButton.getButtonType() == ShopButton.ButtonType.TOGGLE_SELL)
                continue;
            if (currencyType == CurrencyType.WRLD && shopButton.getButtonType() == ShopButton.ButtonType.TOGGLE_WRLD)
                continue;
            if (currencyType == CurrencyType.ESS && shopButton.getButtonType() == ShopButton.ButtonType.TOGGLE_ESS)
                continue;

            ItemStack clone = shopButton.getItemStack().clone();

            boolean increase;
            if ((increase = shopButton.getButtonType() == ShopButton.ButtonType.INCREASE_AMOUNT) || shopButton.getButtonType() == ShopButton.ButtonType.DECREASE_AMOUNT) {
                ItemMeta meta = clone.getItemMeta();
                int newAmount = increase ? amount < 64 ? amount * 2 : 64 : amount > 1 ? amount / 2 : 1;
                if (meta.hasDisplayName())
                    meta.setDisplayName(meta.getDisplayName().replace("%amount%", newAmount + ""));
                if (meta.hasLore())
                    meta.setLore(meta.getLore().stream().map(s -> s.replace("%amount%", newAmount + "")).toList());
                clone.setItemMeta(meta);
            }

            if (category != null) DataUtil.setString(clone, "category", category.getId());
            DataUtil.setInteger(clone, "page", page);
            DataUtil.setInteger(clone, "amount", amount);
            DataUtil.setString(clone, "transactionType", transactionType.name());
            DataUtil.setString(clone, "currencyType", currencyType.name());
            inventory.setItem(shopButton.getSlot(), clone);
        }
    }

}
