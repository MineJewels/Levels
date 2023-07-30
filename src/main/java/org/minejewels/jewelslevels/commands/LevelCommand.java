package org.minejewels.jewelslevels.commands;

import net.abyssdev.abysslib.command.AbyssCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import net.abyssdev.abysslib.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eclipse.collections.api.factory.Lists;
import org.jetbrains.annotations.NotNull;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.commands.sub.LevelAddCommand;
import org.minejewels.jewelslevels.commands.sub.LevelSetCommand;
import org.minejewels.jewelslevels.menus.SkillTreeMenu;

import java.util.List;

public class LevelCommand extends AbyssCommand<JewelsLevels, Player> {

    private final int maxLevel;

    public LevelCommand(JewelsLevels plugin) {
        super(plugin, "level", Player.class);

        this.setAliases(Lists.mutable.of("level", "levels", "plevel", "playerlevel"));

        this.maxLevel = plugin.getSettingsConfig().getInt("max-level");

        this.register(new LevelSetCommand(plugin), new LevelAddCommand(plugin));

        this.register();
    }

    @Override
    public void execute(CommandContext<Player> commandContext) {
        new SkillTreeMenu(plugin).open(commandContext.getSender(), 0);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        if (!sender.hasPermission("levels.admin")) return Lists.mutable.empty();

        switch (args.length) {
            case 1:
                return Lists.mutable.of("set", "add");
            case 2:
                switch (args[0].toLowerCase()) {
                    case "set":
                    case "add":
                        return this.getPlayerNamesOnline();
                    default:
                        return Lists.mutable.empty();
                }
            case 3:
                switch (args[0].toLowerCase()) {
                    case "set":
                    case "add":
                        return Lists.mutable.of(Utils.format(this.maxLevel));
                    default:
                        return Lists.mutable.empty();
                }
            default:
                return Lists.mutable.empty();
        }
    }

    private List<String> getPlayerNamesOnline() {

        List<String> playerNames = Lists.mutable.empty();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}
