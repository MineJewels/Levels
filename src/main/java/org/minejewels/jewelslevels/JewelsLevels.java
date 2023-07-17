package org.minejewels.jewelslevels;

import lombok.Getter;
import net.abyssdev.abysslib.config.AbyssConfig;
import net.abyssdev.abysslib.patterns.registry.Registry;
import net.abyssdev.abysslib.plugin.AbyssPlugin;
import net.abyssdev.abysslib.storage.Storage;
import net.abyssdev.abysslib.text.MessageCache;
import org.minejewels.jewelslevels.commands.LevelCommand;
import org.minejewels.jewelslevels.level.Level;
import org.minejewels.jewelslevels.listeners.PlayerJoin;
import org.minejewels.jewelslevels.placeholder.LevelPlaceholder;
import org.minejewels.jewelslevels.player.LevelPlayer;
import org.minejewels.jewelslevels.player.storage.LevelPlayerStorage;
import org.minejewels.jewelslevels.registry.LevelRegistry;
import org.minejewels.jewelslevels.registry.PrefixRegistry;

import java.util.UUID;

@Getter
public final class JewelsLevels extends AbyssPlugin {

    private final AbyssConfig settingsConfig = this.getAbyssConfig("settings");
    private final AbyssConfig levelsConfig = this.getAbyssConfig("levels");
    private final AbyssConfig langConfig = this.getAbyssConfig("lang");
    private final AbyssConfig menuConfig  = this.getAbyssConfig("menu");

    private final MessageCache messageCache = new MessageCache(this.getConfig("lang"));


    private final int maxLevels = settingsConfig.getInt("max-level");

    private final Registry<Integer, Level> levelRegistry = new LevelRegistry();
    private final Registry<Integer, String> prefixRegistry = new PrefixRegistry();
    private final Storage<UUID, LevelPlayer> playerStorage = new LevelPlayerStorage(this);

    @Override
    public void onEnable() {
        this.cacheLevels();
        this.cachePrefixes();

        this.loadMessages(this.messageCache, this.getConfig("lang"));

        new PlayerJoin(this);
        new LevelCommand(this);

        new LevelPlaceholder(this);
    }

    @Override
    public void onDisable() {
        this.playerStorage.write();
    }

    private void cacheLevels() {

        for (int level = 1; level <= this.maxLevels; level++) {
            this.levelRegistry.register(
                    level,
                    new Level(level, this.levelsConfig.getColoredString("levels." + level + ".reward"), this.levelsConfig.getStringList("levels." + level + ".commands"))
            );
        }
    }

    private void cachePrefixes() {

        for (final String key : this.settingsConfig.getSectionKeys("prefixes")) {

            this.settingsConfig.getIntegerList("prefixes." + key + ".levels").forEach(level -> {
                this.prefixRegistry.register(level, this.settingsConfig.getColoredString("prefixes." + key + ".prefix"));
            });
        }
    }
}
