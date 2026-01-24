// Copyright (c) 2025 heyetcoolio

package github.heyetcoolio.hSayac;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SeasonCommand implements CommandExecutor {

    private final HSayac plugin;

    public SeasonCommand(HSayac plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hsayac.admin")) {
            //noinspection deprecation
            sender.sendMessage(ChatColor.RED + "Yetkin yok.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadCounters();
            //noinspection deprecation
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.reload")));
            return true;
        }

        //noinspection deprecation
        sender.sendMessage(ChatColor.YELLOW + "Kullanım: /hsayac reload");
        return true;
    }
}