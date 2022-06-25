package com.nftworlds.shop.listeners;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.payload.PlayerBuyItemPayload;
import com.nftworlds.shop.shop.button.Category;
import com.nftworlds.shop.shop.button.ShopButton;
import com.nftworlds.shop.shop.item.types.Item;
import com.nftworlds.shop.shop.transactions.CurrencyType;
import com.nftworlds.shop.shop.transactions.TransactionType;
import com.nftworlds.shop.util.*;
import com.nftworlds.wallet.event.PeerToPeerPayEvent;
import com.nftworlds.wallet.event.PlayerTransactEvent;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerListener implements Listener {

    private Shop plugin;

    public PlayerListener() {
        this.plugin = Shop.getInstance();
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory != null && event.getView().getTitle().equals(plugin.getShopManager().getName()) && inventory.getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            Player p = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            int slot = event.getSlot();

            //Main Menu Buttons
            Category.getCategories().stream().filter(category -> category.getSlot() == slot && DataUtil.hasInteger(clicked, category.getId())).findFirst()
                    .ifPresent(category -> plugin.getShopManager().populate(inventory, p, category, 1,
                            DataUtil.getInteger(clicked, "amount"),
                            TransactionType.valueOf(DataUtil.getString(clicked, "transactionType")),
                            CurrencyType.valueOf(DataUtil.getString(clicked, "currencyType"))));

            //Shop Buttons
            Optional<ShopButton> shopButtonOptional = ShopButton.getButtons().values().stream().filter(shopButton -> shopButton.getSlot() == slot && DataUtil.hasInteger(clicked, shopButton.getButtonType().name())).findFirst();
            if (shopButtonOptional.isPresent()) {
                ShopButton shopButton = shopButtonOptional.get();

                Category category = Category.getByID(DataUtil.getString(clicked, "category"));
                int page = DataUtil.getInteger(clicked, "page");
                int amount = DataUtil.getInteger(clicked, "amount");
                TransactionType transactionType = TransactionType.valueOf(DataUtil.getString(clicked, "transactionType"));
                CurrencyType currencyType = CurrencyType.valueOf(DataUtil.getString(clicked, "currencyType"));

                switch (shopButton.getButtonType()) {
                    case NEXT -> {
                        if (inventory.getItem(43) != null) {
                            plugin.getShopManager().populate(inventory, p, category, page + 1, amount, transactionType, currencyType);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                    case PREVIOUS -> {
                        if (page - 1 != 0) {
                            plugin.getShopManager().populate(inventory, p, category, page - 1, amount, transactionType, currencyType);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                    case MAIN_MENU -> plugin.getShopManager().populate(inventory, p, null, 1, amount, transactionType, currencyType);
                    case TOGGLE_BUY -> plugin.getShopManager().populate(inventory, p, category, page, amount, TransactionType.BUY, currencyType);
                    case TOGGLE_SELL -> plugin.getShopManager().populate(inventory, p, category, page, amount, TransactionType.SELL, currencyType);
                    case TOGGLE_ESS -> plugin.getShopManager().populate(inventory, p, category, page, amount, transactionType, CurrencyType.ESS);
                    case TOGGLE_WRLD -> plugin.getShopManager().populate(inventory, p, category, page, amount, transactionType, CurrencyType.WRLD);
                    case INCREASE_AMOUNT -> {
                        if (amount < 64) {
                            plugin.getShopManager().populate(inventory, p, category, page, amount * 2, transactionType, currencyType);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                    case DECREASE_AMOUNT -> {
                        if (amount > 1) {
                            plugin.getShopManager().populate(inventory, p, category, page, amount / 2, transactionType, currencyType);
                        } else {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                }
                return;
            }

            //Items
            if (DataUtil.hasInteger(clicked, "index")) {
                Item shopItem = Item.getShopItems().get(DataUtil.getInteger(clicked, "index"));

                int amount = clicked.getAmount();

                TransactionType transactionType = TransactionType.valueOf(DataUtil.getString(clicked, "transactionType"));
                CurrencyType currencyType = CurrencyType.valueOf(DataUtil.getString(clicked, "currencyType"));

                if (transactionType == TransactionType.BUY && event.getClick().isLeftClick()) { //Buy Item
                    if (currencyType == CurrencyType.WRLD) {
                        if (shopItem.getBuyWRLD() == 0) {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoBuyWRLD()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return;
                        }
                        p.sendMessage(" ");
                        try {
                            NFTPlayer.getByUUID(p.getUniqueId()).requestWRLD(shopItem.getBuyWRLD() * amount, Network.POLYGON, "Shop Purchase | " + amount + "x " + shopItem.getDisplayName(), true, new PlayerBuyItemPayload(shopItem, amount));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        p.sendMessage(" ");
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    } else {
                        if (shopItem.getBuyEss() == 0) {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoBuyDollars()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return;
                        }
                        if (this.plugin.getEcon().getBalance((OfflinePlayer) event.getWhoClicked()) < shopItem.getBuyEss() * amount) {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getInsufficientFunds()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return;
                        }
                        if (InventoryUtil.hasSpace(p.getInventory(), shopItem, amount)) {
                            this.plugin.getEcon().withdrawPlayer((OfflinePlayer) event.getWhoClicked(), shopItem.getBuyEss() * amount);
                            p.getInventory().addItem(ItemBuilder.buildActual(shopItem, amount));
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getPurchased()
                                    .replace("%amount%", amount + "")
                                    .replace("%item%", shopItem.getDisplayName())
                                    .replace("%dollar%", "$")
                                    .replace("%price%", (shopItem.getBuyEss() * amount) + "")
                                    .replace("%wrld%", "")
                            ));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        } else {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoInventorySpace()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                } else { //Sell Item
                    if (event.getClick().isLeftClick() || event.getClick().isRightClick()) {
                        if (currencyType == CurrencyType.WRLD && shopItem.getSellWRLD() == 0) {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoSellWRLD()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return;
                        }
                        if (currencyType == CurrencyType.ESS && shopItem.getSellEss() == 0) {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoSellDollars()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return;
                        }
                    }
                    if (event.getClick().isLeftClick()) {
                        boolean found = false;
                        int itemsLeft = amount;
                        for (ItemStack itemStack : p.getInventory().getStorageContents()) {
                            if (ItemUtil.isMatch(itemStack, shopItem, false)) {
                                found = true;
                                if (itemsLeft > itemStack.getAmount()) {
                                    itemsLeft -= itemStack.getAmount();
                                    itemStack.setAmount(0);
                                } else {
                                    itemsLeft -= amount;
                                    itemStack.setAmount(itemStack.getAmount() - amount);
                                }
                                if (itemsLeft <= 0) break;
                            }
                        }
                        if (found) {
                            if (currencyType == CurrencyType.ESS) {
                                this.plugin.getEcon().depositPlayer((OfflinePlayer) event.getWhoClicked(), shopItem.getSellEss() * amount - itemsLeft);
                            } else {
                                NFTPlayer.getByUUID(p.getUniqueId()).sendWRLD(shopItem.getSellWRLD() * amount - itemsLeft, Network.POLYGON, "Shop Sale: " + shopItem.getId() + " x" + (amount - itemsLeft));
                            }
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getSold()
                                    .replace("%amount%", (amount - itemsLeft) + "")
                                    .replace("%item%", shopItem.getDisplayName())
                                    .replace("%dollar%", currencyType == CurrencyType.ESS ? "$" : "")
                                    .replace("%price%", currencyType == CurrencyType.ESS ? (shopItem.getSellEss() * (amount - itemsLeft)) + "" : (shopItem.getSellWRLD() * (amount - itemsLeft)) + "")
                                    .replace("%wrld%", currencyType == CurrencyType.WRLD ? "$WRLD" : "")
                            ));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        } else {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoItemToSell()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    } else if (event.getClick().isRightClick()) {
                        int count = 0;
                        for (ItemStack itemStack : p.getInventory().getStorageContents()) {
                            if (ItemUtil.isMatch(itemStack, shopItem, false)) {
                                count += itemStack.getAmount();
                            }
                        }
                        if (count > 0) {
                            p.getInventory().remove(shopItem.getMaterial());
                            if (currencyType == CurrencyType.ESS) {
                                this.plugin.getEcon().depositPlayer((OfflinePlayer) event.getWhoClicked(), shopItem.getSellEss() * count);
                            } else {
                                NFTPlayer.getByUUID(p.getUniqueId()).sendWRLD(shopItem.getSellWRLD() * count, Network.POLYGON, "Shop Sale: " + shopItem.getId() + " x" + count);
                            }
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getSold()
                                    .replace("%amount%", (count) + "")
                                    .replace("%item%", shopItem.getDisplayName())
                                    .replace("%dollar%", currencyType == CurrencyType.ESS ? "$" : "")
                                    .replace("%price%", currencyType == CurrencyType.ESS ? (shopItem.getSellEss() * count) + "" : (shopItem.getSellWRLD() * count) + "")
                                    .replace("%wrld%", currencyType == CurrencyType.WRLD ? "$WRLD" : "")
                            ));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        } else {
                            p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNoItemToSell()));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(plugin.getShopManager().getName()) && event.getInventory().getType() != InventoryType.PLAYER) {
            Player p = (Player) event.getPlayer();
            p.playSound(p.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        }
    }

    @EventHandler
    public void onPay(PlayerTransactEvent<?> event) {
        if (event.getPayload() instanceof PlayerBuyItemPayload payload) {
            Player p = event.getPlayer();
            Item shopItem = payload.shopItem();
            int amount = payload.amount();

            if (InventoryUtil.hasSpace(p.getInventory(), shopItem, amount)) {
                p.getInventory().addItem(ItemBuilder.buildActual(shopItem, amount));
                p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getPurchased()
                        .replace("%amount%", amount + "")
                        .replace("%item%", shopItem.getDisplayName())
                        .replace("%dollar%", "")
                        .replace("%price%", (shopItem.getBuyWRLD() * (amount)) + "")
                        .replace("%wrld%", "$WRLD")
                ));
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            } else {
                p.getWorld().dropItem(p.getLocation(), ItemBuilder.buildActual(shopItem, amount));
                p.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getItemDropped()));
            }
            p.playSound(p.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        }
    }

    @EventHandler
    public void onPeerToPeer(PeerToPeerPayEvent event) {
        Player to = event.getTo();
        Player from = event.getFrom();
        if (to.isOnline()) {
            to.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getReceivedWRLD()
                    .replace("%amount%", event.getAmount() + "")
                    .replace("player%", from.getName())
                    .replace("%for%", event.getReason().isEmpty() ? "" : "for")
                    .replace("%reason%", event.getReason().isEmpty() ? "" : event.getReason())
            ));
            to.playSound(to.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        }
        if (from.isOnline()) {
            from.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getSentWRLD()
                    .replace("%amount%", event.getAmount() + "")
                    .replace("player%", to.getName())
                    .replace("%for%", event.getReason().isEmpty() ? "" : "for")
                    .replace("%reason%", event.getReason().isEmpty() ? "" : event.getReason())
            ));
            from.playSound(from.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        }
    }
}
