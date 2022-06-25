package com.nftworlds.shop.commands;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.util.ColorUtil;
import com.nftworlds.wallet.objects.Wallet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WRLDBalanceCommand implements CommandExecutor {

    private final Shop plugin;

    public WRLDBalanceCommand() {
        this.plugin = Shop.getInstance();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player) {
            String address = plugin.getWalletAPI().getPrimaryWallet((Player) sender).getAddress();
            if (address.equalsIgnoreCase("0x0000000000000000000000000000000000000000")) {
                sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getLinkWallet()));
            } else {
                double balance = plugin.getWalletAPI().getWallets((Player) sender).stream().mapToDouble(Wallet::getPolygonWRLDBalance).sum();
                sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getWrldBalance().replace("%amount%", balance + "")));
            }
        }
        return true;
    }
}