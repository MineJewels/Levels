package org.minejewels.jewelslevels.commands.sub;

import net.abyssdev.abysslib.command.AbyssSubCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.utils.PlayerUtils;
import net.abyssdev.abysslib.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.eclipse.collections.api.factory.Sets;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.level.Level;
import org.minejewels.jewelslevels.player.LevelPlayer;

import java.util.Optional;

public class LevelAddCommand extends AbyssSubCommand<JewelsLevels> {

    private final int maxLevel;

    public LevelAddCommand(final JewelsLevels plugin) {
        super(plugin, 0, Sets.immutable.of("add"));

        this.maxLevel = plugin.getSettingsConfig().getInt("max-level");
    }

    @Override
    public void execute(CommandContext<?> context) {

        final CommandSender player = context.getSender();

        if (!player.hasPermission("levels.admin")) {
            this.plugin.getMessageCache().sendMessage(player, "messages.no-permission");
            return;
        }

        final Optional<OfflinePlayer> optionalPlayer = Optional.of(context.asOfflinePlayer(0));

        if (!optionalPlayer.isPresent()) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-player");
            return;
        }

        final String optionalLevel = context.asString(1);

        if (!Utils.isInteger(optionalLevel)) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-level");
            return;
        }

        final OfflinePlayer target = optionalPlayer.get();
        final LevelPlayer levelPlayer = this.plugin.getPlayerStorage().get(target.getUniqueId());
        final int level = Integer.parseInt(optionalLevel);

        if (level + levelPlayer.getLevel() > this.maxLevel) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-level");
            return;
        }

        if (level + levelPlayer.getLevel() < 1) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-level");
            return;
        }

        final Level upgrade = this.plugin.getLevelRegistry().get(level + levelPlayer.getLevel()).get();

        levelPlayer.addLevel(level);

        if (target.isOnline()) {
            PlayerUtils.dispatchCommands(target.getPlayer(), upgrade.getCommands());

            final PlaceholderReplacer replacer = new PlaceholderReplacer()
                    .addPlaceholder("%level%", Utils.format(upgrade.getLevel()))
                    .addPlaceholder("%new_level%", Utils.format(upgrade.getLevel() + 1))
                    .addPlaceholder("%max_level%", Utils.format(this.plugin.getMaxLevels()))
                    .addPlaceholder("%reward%", upgrade.getReward())
                    .addPlaceholder("%prefix%", this.plugin.getPrefixRegistry().get(upgrade.getLevel()).orElse("N/A!").replace("%level%", Utils.format(upgrade.getLevel())));

            plugin.getMessageCache().sendMessage(player, "messages.leveled-up", replacer);
        }
    }
}
