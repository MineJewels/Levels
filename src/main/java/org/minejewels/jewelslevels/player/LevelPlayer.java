package org.minejewels.jewelslevels.player;

import lombok.Data;
import net.abyssdev.abysslib.storage.id.Id;

import java.util.UUID;

@Data
public class LevelPlayer {

    @Id
    private final UUID player;
    private int level;

    public LevelPlayer(final UUID player) {
        this.player = player;
        this.level = 1;
    }

    public void addLevel(final int amount) {
        this.level += amount;
    }
}
