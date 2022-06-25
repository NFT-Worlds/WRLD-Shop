package com.nftworlds.shop.config;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.shop.button.Category;
import com.nftworlds.shop.shop.button.ShopButton;
import com.nftworlds.shop.shop.item.types.*;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

@Getter
public class ShopConfig {

    private Shop plugin;

    private boolean compactMenus;

    private String mainMenuName;
    private Material filler;
    private Material fillerBuy;
    private Material fillerSell;
    private boolean useCategories;

    private String notOnline;
    private String invalidAmount;
    private String payPlayerCommand;
    private String payPlayerCommandError;
    private String linkWallet;
    private String wrldBalance;
    private String noBuyWRLD;
    private String noBuyDollars;
    private String noSellWRLD;
    private String noSellDollars;
    private String insufficientFunds;
    private String purchased;
    private String noInventorySpace;
    private String itemDropped;
    private String sold;
    private String noItemToSell;
    private String receivedWRLD;
    private String sentWRLD;

    private List<String> loreBuy;
    private List<String> loreBuyInvalid;
    private List<String> loreBuyEnd;
    private List<String> loreSell;
    private List<String> loreSellInvalid;
    private List<String> loreSellEnd;

    public ShopConfig() {
        this.plugin = Shop.getInstance();
        setup();
    }

    public void setup() {

        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        notOnline = config.getString("lang.not-online");
        invalidAmount = config.getString("lang.invalid-amount");
        payPlayerCommand = config.getString("lang.pay-player-command");
        payPlayerCommandError = config.getString("lang.pay-player-command-error");
        linkWallet = config.getString("lang.link-wallet");
        wrldBalance = config.getString("lang.wrld-balance");
        noBuyWRLD = config.getString("lang.no-buy-wrld");
        noBuyDollars = config.getString("lang.no-buy-dollars");
        noSellWRLD = config.getString("lang.no-sell-wrld");
        noSellDollars = config.getString("lang.no-sell-dollars");
        insufficientFunds = config.getString("lang.insufficient-funds");
        purchased = config.getString("lang.purchased");
        noInventorySpace = config.getString("lang.no-inventory-space");
        itemDropped = config.getString("lang.item-dropped");
        sold = config.getString("lang.sold");
        noItemToSell = config.getString("lang.no-item-to-sell");
        receivedWRLD = config.getString("lang.received-wrld");
        sentWRLD = config.getString("lang.sent-wrld");

        loreBuy = config.getStringList("lang.lore.buy");
        loreBuyInvalid = config.getStringList("lang.lore.buy-invalid");
        loreBuyEnd = config.getStringList("lang.lore.buy-end");
        loreSell = config.getStringList("lang.lore.sell");
        loreSellInvalid = config.getStringList("lang.lore.sell-invalid");
        loreSellEnd = config.getStringList("lang.lore.sell-end");

        compactMenus = config.getBoolean("compact-menus");

        mainMenuName = config.getString("menu.main-menu.name");
        filler = Material.valueOf(config.getString("menu.main-menu.filler"));
        fillerBuy = Material.valueOf(config.getString("menu.main-menu.filler_buy"));
        fillerSell = Material.valueOf(config.getString("menu.main-menu.filler_sell"));
        useCategories = config.getBoolean("menu.main-menu.use-categories");

        if (useCategories) {
            ConfigurationSection categories = config.getConfigurationSection("menu.main-menu.categories");
            for (String id : categories.getKeys(false)) {
                ConfigurationSection category = categories.getConfigurationSection(id);
                new Category(id, category.getInt("slot"), category.getString("item"), category.getString("display"), category.getStringList("lore"));
            }
        }

        ConfigurationSection buttons = config.getConfigurationSection("menu.shop");
        for (String id : buttons.getKeys(false)) {
            ConfigurationSection button = buttons.getConfigurationSection(id);
            new ShopButton(ShopButton.ButtonType.valueOf(id.toUpperCase()), button.getString("item"), button.getInt("slot"), button.getString("display"), button.getStringList("lore"));
        }

        ConfigurationSection items = config.getConfigurationSection("items");
        for (String id : items.getKeys(false)) {

            ConfigurationSection item = items.getConfigurationSection(id);

            Item shopItem;
            Material material = Material.valueOf(item.getString("material"));
            switch (material) {
                case POTION, LINGERING_POTION, SPLASH_POTION -> shopItem = new PotionItem(id, material);
                case SPAWNER -> shopItem = new SpawnerItem(id, material);
                case ENCHANTED_BOOK -> shopItem = new EnchantedBookItem(id, material);
                case TIPPED_ARROW -> shopItem = new TippedArrowItem(id, material);
                default -> shopItem = new Item(id, material);
            }
            if (useCategories) shopItem.setCategory(Category.getByID(item.getString("category")));

            if (item.isConfigurationSection("sell")) {
                ConfigurationSection sell = item.getConfigurationSection("sell");
                shopItem.setSellEss(sell.isSet("price") ? Math.max(0, sell.getDouble("price")) : 0);
                shopItem.setSellWRLD(sell.isSet("wrld-price") ? Math.max(0, sell.getDouble("wrld-price")) : 0);
            }

            if (item.isConfigurationSection("buy")) {
                ConfigurationSection buy = item.getConfigurationSection("buy");
                shopItem.setBuyEss(buy.isSet("price") ? Math.max(0, buy.getDouble("price")) : 0);
                shopItem.setBuyWRLD(buy.isSet("wrld-price") ? Math.max(0, buy.getDouble("wrld-price")) : 0);
            }

            //Item displayed
            if (item.isConfigurationSection("display")) {
                ConfigurationSection display = item.getConfigurationSection("display");
                if (display.isSet("name")) shopItem.setDisplayName(display.getString("name"));
                if (display.isSet("custommodeldata"))
                    shopItem.setDisplayCustomModelData(display.getInt("custommodeldata"));
                if (display.isSet("lore")) shopItem.setDisplayLore(display.getStringList("lore"));
                if (display.isSet("enchants")) {
                    HashMap<Enchantment, Integer> enchants = new HashMap<>();
                    display.getStringList("enchants").forEach(s -> enchants.put(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(s.split(":")[0].toLowerCase())), Integer.parseInt(s.split(":")[1])));
                    shopItem.setDisplayEnchants(enchants);
                }
            }

            //Actual item given
            if (item.isConfigurationSection("item")) {
                ConfigurationSection actual = item.getConfigurationSection("item");
                if (actual.isSet("name")) shopItem.setActualName(actual.getString("name"));
                if (actual.isSet("custommodeldata"))
                    shopItem.setActualCustomModelData(actual.getInt("custommodeldata"));
                if (actual.isSet("lore")) shopItem.setActualLore(actual.getStringList("lore"));
                if (actual.isSet("enchants")) {
                    HashMap<Enchantment, Integer> enchants = new HashMap<>();
                    actual.getStringList("enchants").forEach(s -> enchants.put(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(s.split(":")[0].toLowerCase())), Integer.parseInt(s.split(":")[1])));
                    shopItem.setActualEnchants(enchants);
                }
            }

            //Spawners
            if (item.isSet("spawner"))
                ((SpawnerItem) shopItem).setEntityType(EntityType.valueOf(item.getString("spawner")));

            //Potions
            if (item.isSet("potion")) {
                PotionItem potionItem = (PotionItem) shopItem;
                ConfigurationSection potion = item.getConfigurationSection("potion");
                if (potion.isSet("color"))
                    potionItem.setColor(PotionEffectType.getByName(potion.getString("color")).getColor());
                if (potion.isSet("effects")) {
                    for (String e : potion.getStringList("effects")) {
                        String[] parts = e.split(":");
                        potionItem.getPotionEffects().add(new PotionEffect(PotionEffectType.getByName(parts[0]), Integer.parseInt(parts[2]) * 20, Integer.parseInt(parts[1])));
                    }
                }
            }

            //Enchantment Books
            if (item.isSet("enchanted_book")) {
                EnchantedBookItem enchantedBookItem = (EnchantedBookItem) shopItem;
                ConfigurationSection enchantedBook = item.getConfigurationSection("enchanted_book");
                if (enchantedBook.isSet("enchantments")) {
                    for (String e : enchantedBook.getStringList("enchantments")) {
                        String[] parts = e.split(":");
                        enchantedBookItem.getEnchantments().put(Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase())), Integer.parseInt(parts[1]));
                    }
                }
            }

            //Tipped Arrows
            if (item.isSet("tipped_arrow")) {
                TippedArrowItem tippedArrowItem = (TippedArrowItem) shopItem;
                ConfigurationSection tippedArrow = item.getConfigurationSection("tipped_arrow");
                if (tippedArrow.isSet("color"))
                    tippedArrowItem.setColor(PotionEffectType.getByName(tippedArrow.getString("color")).getColor());
                if (tippedArrow.isSet("effects")) {
                    for (String e : tippedArrow.getStringList("effects")) {
                        String[] parts = e.split(":");
                        tippedArrowItem.getPotionEffects().add(new PotionEffect(PotionEffectType.getByName(parts[0]), Integer.parseInt(parts[2]) * 20, Integer.parseInt(parts[1])));
                    }
                }
            }

            if (item.isSet("commands")) shopItem.setCommands(item.getStringList("commands"));
            shopItem.setGiveItem(!item.isSet("give-item") || item.getBoolean("give-item"));
        }
    }
}