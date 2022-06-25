package com.nftworlds.shop.util;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.config.ShopConfig;
import com.nftworlds.shop.shop.item.types.Item;
import com.nftworlds.shop.shop.transactions.CurrencyType;
import com.nftworlds.shop.shop.transactions.TransactionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    //Build filler
    public static ItemStack build(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.rgb(name));
        item.setItemMeta(meta);
        return item;
    }

    //Build buttons
    public static ItemStack build(Material material, String name, List<String> lore, boolean removeStats) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.rgb(name));
        meta.setLore(lore.stream().map(ColorUtil::rgb).toList());
        if (removeStats) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }

    //Build items in GUI
    public static ItemStack build(Item shopItem, int amount, TransactionType transactionType, CurrencyType currencyType) {

        ShopConfig shopConfig = Shop.getInstance().getShopConfig();

        ItemStack item = new ItemStack(shopItem.getMaterial(), amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.rgb(shopItem.getDisplayName()));
        if (shopItem.getDisplayCustomModelData() != 0) meta.setCustomModelData(shopItem.getDisplayCustomModelData());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_POTION_EFFECTS);
        List<String> lore = shopItem.getDisplayLore().stream().map(ColorUtil::rgb).collect(Collectors.toList());
        if (!lore.isEmpty()) lore.add(" ");
        if (transactionType == TransactionType.BUY) {
            if (Shop.getInstance().getShopConfig().isCompactMenus()) {
                for (String s : shopConfig.getLoreBuy()) {
                    lore.add(ColorUtil.rgb(s
                            .replace("%dollar%", currencyType == CurrencyType.ESS ? "$" : "")
                            .replace("%amount%", (currencyType == CurrencyType.WRLD ? shopItem.getBuyWRLD() * amount : shopItem.getBuyEss() * amount) + "")
                            .replace("%wrld%", currencyType == CurrencyType.WRLD ? "$WRLD" : "")
                    ));
                }
            } else {
                if (shopItem.getBuyWRLD() != 0) {
                    if (currencyType == CurrencyType.ESS) {
                        for (String s : shopConfig.getLoreBuyInvalid()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "")
                                    .replace("%amount%", shopItem.getBuyWRLD() * amount + "")
                                    .replace("%wrld%", "$WRLD")
                            ));
                        }
                    } else {
                        for (String s : shopConfig.getLoreBuy()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "")
                                    .replace("%amount%", shopItem.getBuyWRLD() * amount + "")
                                    .replace("%wrld%", "$WRLD")
                            ));
                        }
                    }
                }
                if (shopItem.getBuyEss() != 0) {
                    if (currencyType == CurrencyType.WRLD) {
                        for (String s : shopConfig.getLoreBuyInvalid()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "$")
                                    .replace("%amount%", shopItem.getBuyEss() * amount + "")
                                    .replace("%wrld%", "")
                            ));
                        }
                    } else {
                        for (String s : shopConfig.getLoreBuy()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "$")
                                    .replace("%amount%", shopItem.getBuyEss() * amount + "")
                                    .replace("%wrld%", "")
                            ));
                        }
                    }
                }

                for (String s : shopConfig.getLoreBuyEnd()) {
                    lore.add(ColorUtil.rgb(s.replace("%amount%", amount + "")));
                }
            }
        } else {
            if (Shop.getInstance().getShopConfig().isCompactMenus()) {
                for (String s : shopConfig.getLoreSell()) {
                    lore.add(ColorUtil.rgb(s
                            .replace("%dollar%", currencyType == CurrencyType.ESS ? "$" : "")
                            .replace("%amount%", (currencyType == CurrencyType.WRLD ? shopItem.getSellWRLD() * amount : shopItem.getSellEss() * amount) + "")
                            .replace("%wrld%", currencyType == CurrencyType.WRLD ? "$WRLD" : "")
                    ));
                }
            } else {
                if (shopItem.getSellWRLD() != 0) {
                    if (currencyType == CurrencyType.ESS) {
                        for (String s : shopConfig.getLoreSellInvalid()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "")
                                    .replace("%amount%", shopItem.getSellWRLD() * amount + "")
                                    .replace("%wrld%", "$WRLD")
                            ));
                        }
                    } else {
                        for (String s : shopConfig.getLoreSell()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "")
                                    .replace("%amount%", shopItem.getSellWRLD() * amount + "")
                                    .replace("%wrld%", "$WRLD")
                            ));
                        }
                    }
                }
                if (shopItem.getSellEss() != 0) {
                    if (currencyType == CurrencyType.WRLD) {
                        for (String s : shopConfig.getLoreSellInvalid()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "$")
                                    .replace("%amount%", shopItem.getSellEss() * amount + "")
                                    .replace("%wrld%", "")
                            ));
                        }
                    } else {
                        for (String s : shopConfig.getLoreSell()) {
                            lore.add(ColorUtil.rgb(s
                                    .replace("%dollar%", "$")
                                    .replace("%amount%", shopItem.getSellEss() * amount + "")
                                    .replace("%wrld%", "")
                            ));
                        }
                    }
                }
            }

            for (String s : shopConfig.getLoreSellEnd()) {
                lore.add(ColorUtil.rgb(s.replace("%amount%", amount + "")));
            }
        }

        meta.setLore(lore);

        shopItem.applyExtra(meta);

        item.setItemMeta(meta);
        shopItem.getDisplayEnchants().forEach((enchantment, integer) -> {
            if (enchantment != null) item.addUnsafeEnchantment(enchantment, integer);
        });

        DataUtil.setInteger(item, "index", shopItem.getIndex());
        DataUtil.setString(item, "item", shopItem.getId());
        DataUtil.setString(item, "transactionType", transactionType.name());
        DataUtil.setString(item, "currencyType", currencyType.name());

        return item;
    }

    //Build actual items
    public static ItemStack buildActual(Item shopItem, int amount) {
        ItemStack item = new ItemStack(shopItem.getMaterial(), amount);
        ItemMeta meta = item.getItemMeta();
        if (shopItem.getActualName() != null) meta.setDisplayName(ColorUtil.rgb(shopItem.getActualName()));
        if (shopItem.getActualCustomModelData() != 0) meta.setCustomModelData(shopItem.getActualCustomModelData());
        if (!shopItem.getActualLore().isEmpty()) {
            List<String> lore = shopItem.getActualLore().stream().map(ColorUtil::rgb).collect(Collectors.toList());
            meta.setLore(lore);
        }

        shopItem.applyExtra(meta);

        item.setItemMeta(meta);
        shopItem.getActualEnchants().forEach((enchantment, integer) -> {
            if (enchantment != null) item.addUnsafeEnchantment(enchantment, integer);
        });
        return item;
    }
}
