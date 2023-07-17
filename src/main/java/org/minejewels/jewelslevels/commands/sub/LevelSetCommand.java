package org.minejewels.jewelslevels.commands.sub;

import net.abyssdev.abysslib.command.AbyssSubCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import org.bukkit.entity.Player;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.player.LevelPlayer;

public class LevelSetCommand extends AbyssSubCommand<JewelsLevels> {

    public LevelSetCommand(final JewelsLevels plugin) {
        super(plugin, 0, Sets.immutable.of("set"));
    }

    @Override
    public void execute(CommandContext<?> context) {

        final Player player = context.getSender();

        final LevelPlayer levelPlayer = this.plugin.getPlayerStorage().get(player.getUniqueId());

        levelPlayer.setLevel(context.asInt(0));
    }
}
