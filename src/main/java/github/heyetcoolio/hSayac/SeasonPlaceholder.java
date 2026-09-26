// Copyright (c) 2025 heyetcoolio

package github.heyetcoolio.hSayac;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SeasonPlaceholder extends PlaceholderExpansion {

    private final HSayac plugin;
    private final String identifier;

    public SeasonPlaceholder(HSayac plugin, String identifier) {
        this.plugin = plugin;
        this.identifier = identifier;
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return "heyetcoolio";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public boolean persist() {
        return true; // /papi reload yapıldığında placeholder'ın silinmesini engeller
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // %hsayac_kalan_x%
        if (params.startsWith("kalan_")) {
            String counterName = params.replace("kalan_", "");

            HSayac.Counter counter = plugin.counters.get(counterName);

            if (counter != null) {
                return counter.getRemainingFormatted();
            } else {
                return "";
            }
        }

        return null;
    }
}