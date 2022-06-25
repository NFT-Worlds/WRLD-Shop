package com.nftworlds.shop.commands;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {

    private final Shop plugin;

    public ShopCommand() {
        this.plugin = Shop.getInstance();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player) {
            plugin.getShopManager().openMenu((Player) sender);
        } else {
            sender.sendMessage(ColorUtil.rgb("&cOnly players can execute this command"));
        }
        return true;
    }
}