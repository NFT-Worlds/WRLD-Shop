package com.nftworlds.shop;

import com.nftworlds.shop.commands.PayWRLDCommand;
import com.nftworlds.shop.commands.ShopCommand;
import com.nftworlds.shop.commands.WRLDBalanceCommand;
import com.nftworlds.shop.config.ShopConfig;
import com.nftworlds.shop.listeners.PlayerListener;
import com.nftworlds.shop.managers.ShopManager;
import com.nftworlds.wallet.api.WalletAPI;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Shop extends JavaPlugin {

    private static Shop plugin;

    private WalletAPI walletAPI;
    private Economy econ;

    private ShopConfig shopConfig;
    private ShopManager shopManager;

    public void onEnable() {
        plugin = this;

        this.walletAPI = new WalletAPI();
        setupEconomy();

        this.shopConfig = new ShopConfig();
        this.shopManager = new ShopManager();

        registerEvents();
        registerCommands();

        getServer().getConsoleSender().sendMessage("NFTWorldsShop has been enabled");
    }

    public void onDisable() {
        plugin = null;
        getServer().getConsoleSender().sendMessage("NFTWorldsShop has been disabled");
    }

    public void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        getCommand("wrldbalance").setExecutor(new WRLDBalanceCommand());
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("paywrld").setExecutor(new PayWRLDCommand());
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.econ = economyProvider.getProvider();
        }
        return (this.econ != null);
    }

    public static Shop getInstance() {
        return plugin;
    }
}
