package com.nftworlds.shop.commands;

import com.nftworlds.shop.Shop;
import com.nftworlds.shop.util.ColorUtil;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayWRLDCommand implements CommandExecutor {

    private final Shop plugin;

    public PayWRLDCommand() {
        this.plugin = Shop.getInstance();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length >= 2) {
                Player from = (Player) sender;
                Player to;
                try {
                    to = Bukkit.getPlayer(args[0]);
                } catch (Exception ex) {
                    sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNotOnline()));
                    return true;
                }
                if (to != null && to.isOnline()) {

                    double amount;
                    try {
                        amount = Double.parseDouble(args[1]);
                    } catch (Exception ex) {
                        sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getInvalidAmount()));
                        return true;
                    }

                    StringBuilder reason = new StringBuilder();
                    if (args.length >= 3) {
                        for (int i = 2; i < args.length; i++) {
                            reason.append(args[i]).append(" ");
                        }
                    }

                    from.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getPayPlayerCommand().replace("%player%", to.getName()).replace("%amount%", amount + "")));
                    plugin.getWalletAPI().getNFTPlayer(from).createPlayerPayment(NFTPlayer.getByUUID(to.getUniqueId()), amount, Network.POLYGON, reason.toString());
                    from.playSound(from.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                } else {
                    sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getNotOnline()));
                }
            } else {
                sender.sendMessage(ColorUtil.rgb(plugin.getShopConfig().getPayPlayerCommandError()));
            }
        } else {
            sender.sendMessage(ColorUtil.rgb("&cOnly players may create peer to peer payments"));
        }
        return true;
    }
}