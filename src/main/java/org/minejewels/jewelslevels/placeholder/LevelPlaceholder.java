package org.minejewels.jewelslevels.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.abyssdev.abysslib.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.player.LevelPlayer;

public class LevelPlaceholder extends PlaceholderExpansion {

    private final JewelsLevels plugin;

    public LevelPlaceholder(final JewelsLevels plugin) {
        this.plugin = plugin;

        this.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "level";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Consciences";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        final LevelPlayer levelPlayer = this.plugin.getPlayerStorage().get(player.getUniqueId());

        if (params.equalsIgnoreCase("prefix")) {
            return this.plugin.getPrefixRegistry().get(levelPlayer.getLevel()).get().replace("%level%", Utils.format(levelPlayer.getLevel()));
        }

        return "There has been a error!";
    }
}
