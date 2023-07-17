package org.minejewels.jewelslevels.player.storage;

import net.abyssdev.abysslib.patterns.registry.Registry;
import net.abyssdev.abysslib.storage.json.JsonStorage;
import net.abyssdev.abysslib.utils.file.Files;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.player.LevelPlayer;

import java.io.File;
import java.util.UUID;

public class LevelPlayerStorage extends JsonStorage<UUID, LevelPlayer> {
    public LevelPlayerStorage(final JewelsLevels plugin) {
        super(Files.file("data.json", plugin), LevelPlayer.class);
    }
}
