package org.minejewels.jewelslevels.commands;

import net.abyssdev.abysslib.command.AbyssCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import org.bukkit.entity.Player;
import org.eclipse.collections.api.factory.Lists;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.commands.sub.LevelSetCommand;
import org.minejewels.jewelslevels.menus.SkillTreeMenu;

public class LevelCommand extends AbyssCommand<JewelsLevels, Player> {

    public LevelCommand(JewelsLevels plugin) {
        super(plugin, "level", Player.class);

        this.setAliases(Lists.mutable.of("level", "levels", "plevel", "playerlevel"));

        this.register(new LevelSetCommand(plugin));

        this.register();
    }

    @Override
    public void execute(CommandContext<Player> commandContext) {

        new SkillTreeMenu(plugin).open(commandContext.getSender(), 0);
    }
}
