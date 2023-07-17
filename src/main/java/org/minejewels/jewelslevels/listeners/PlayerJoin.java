package org.minejewels.jewelslevels.listeners;

import net.abyssdev.abysslib.listener.AbyssListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.player.LevelPlayer;

public class PlayerJoin extends AbyssListener<JewelsLevels> {

    public PlayerJoin(final JewelsLevels plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (this.plugin.getPlayerStorage().contains(player.getUniqueId())) return;

        this.plugin.getPlayerStorage().save(new LevelPlayer(player.getUniqueId()));
    }
}
