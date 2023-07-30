package org.minejewels.jewelslevels.menus;

import net.abyssdev.abysslib.builders.ItemBuilder;
import net.abyssdev.abysslib.builders.PageBuilder;
import net.abyssdev.abysslib.economy.registry.impl.DefaultEconomyRegistry;
import net.abyssdev.abysslib.menu.MenuBuilder;
import net.abyssdev.abysslib.menu.item.MenuItemBuilder;
import net.abyssdev.abysslib.menu.templates.PagedAbyssMenu;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.utils.PlayerUtils;
import net.abyssdev.abysslib.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.minejewels.jewelslevels.JewelsLevels;
import org.minejewels.jewelslevels.level.Level;
import org.minejewels.jewelslevels.player.LevelPlayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SkillTreeMenu extends PagedAbyssMenu<JewelsLevels> {

    private final List<Integer> menuSlots;

    private final MenuItemBuilder next, prev, current, back;
    private final ItemBuilder lockedSlots, purchasedSlots, nextUpgrade;

    private final int maxPages;

    public SkillTreeMenu(final JewelsLevels plugin) {
        super(plugin, plugin.getMenuConfig(),"skill-tree.");

        final FileConfiguration config = plugin.getMenuConfig();
        final String path = "skill-tree";

        this.menuSlots = config.getIntegerList(path + ".upgrade-slots");

        this.maxPages = config.getInt(path + ".max-pages");

        this.next = new MenuItemBuilder(
                new ItemBuilder(config, path + ".action-items.next-page"),
                config.getInt(path + ".action-items.next-page.slot"));

        this.prev = new MenuItemBuilder(
                new ItemBuilder(config, path + ".action-items.previous-page"),
                config.getInt(path + ".action-items.previous-page.slot"));

        this.current = new MenuItemBuilder(
                new ItemBuilder(config, path + ".action-items.current-page"),
                config.getInt(path + ".action-items.current-page.slot"));

        this.back = new MenuItemBuilder(
                new ItemBuilder(config, path + ".action-items.back-menu"),
                config.getInt(path + ".action-items.back-menu.slot"));

        this.lockedSlots = new ItemBuilder(config, path + ".display-items.locked-slots");
        this.purchasedSlots = new ItemBuilder(config, path + ".display-items.purchased-slots");
        this.nextUpgrade = new ItemBuilder(config, path + ".display-items.next-upgrade");
    }

    public void open(final Player player, final int page) {

        final LevelPlayer levelPlayer = this.plugin.getPlayerStorage().get(player.getUniqueId());

        final MenuBuilder builder = this.createBase();

        final PlaceholderReplacer itemReplacer = new PlaceholderReplacer()
                .addPlaceholder("%current_level%", Utils.format(levelPlayer.getLevel()))
                .addPlaceholder("%max_level%", Utils.format(this.plugin.getMaxLevels()))
                .addPlaceholder("%page%", Utils.format(page + 1));

        final LinkedList<Level> upgradeList = new LinkedList<>(this.plugin.getLevelRegistry().values());

        final PageBuilder<Level> pageBuilder = new PageBuilder<>(upgradeList, this.menuSlots.size());

        builder.setItem(this.next.getSlot(), this.next.getItem().parse(itemReplacer));
        builder.setItem(this.prev.getSlot(), this.prev.getItem().parse(itemReplacer));
        builder.setItem(this.current.getSlot(), this.current.getItem().parse(itemReplacer));
        builder.setItem(this.back.getSlot(), this.back.getItem().parse(itemReplacer));

        builder.addClickEvent(this.back.getSlot(), event -> player.closeInventory());

        builder.addClickEvent(this.next.getSlot(), event -> {
            if (page + 2 > maxPages) return;
            if (pageBuilder.hasPage(page + 1)) {
                this.open(player, page + 1);
            }
        });

        builder.addClickEvent(this.prev.getSlot(), event -> {
            if (page - 1 > -1) {
                this.open(player, page - 1);
            }
        });

        List<Level> upgrades = pageBuilder.getPage(page);
        int index = 0;

        Collections.sort(upgrades, Comparator.comparingInt(Level::getLevel));

        for (final int slot : this.menuSlots) {
            if (index >= upgrades.size()) {
                break;
            }

            final Level upgrade = upgrades.get(index);

            final PlaceholderReplacer replacer = new PlaceholderReplacer()
                    .addPlaceholder("%cost%", Utils.format(this.calculatePrice(upgrade.getLevel())))
                    .addPlaceholder("%level%", Utils.format(upgrade.getLevel()))
                    .addPlaceholder("%new_level%", Utils.format(upgrade.getLevel() + 1))
                    .addPlaceholder("%max_level%", Utils.format(this.plugin.getMaxLevels()))
                    .addPlaceholder("%reward%", upgrade.getReward())
                    .addPlaceholder("%prefix%", this.plugin.getPrefixRegistry().get(upgrade.getLevel()).orElse("N/A!").replace("%level%", Utils.format(upgrade.getLevel())));

            if (upgrade.getLevel() <= levelPlayer.getLevel()) {
                builder.setItem(slot, this.purchasedSlots.parse(replacer));
            }

            if (upgrade.getLevel()-1 == levelPlayer.getLevel()) {
                builder.setItem(slot, this.nextUpgrade.parse(replacer));
            }

            if (upgrade.getLevel()-1 > levelPlayer.getLevel()) {
                builder.setItem(slot, this.lockedSlots.parse(replacer));
            }

            builder.addClickEvent(slot, event -> {

                if (upgrade.getLevel() <= levelPlayer.getLevel()) {
                    plugin.getMessageCache().sendMessage(player, "messages.already-purchased");
                    return;
                }

                if (upgrade.getLevel()-1 == levelPlayer.getLevel()) {

                    final double cost = this.calculatePrice(upgrade.getLevel());

                    if (!DefaultEconomyRegistry.get().getEconomy("vault").hasBalance(player, cost)) {
                        plugin.getMessageCache().sendMessage(player, "messages.not-enough");
                        return;
                    }

                    DefaultEconomyRegistry.get().getEconomy("vault").withdrawBalance(player, cost);

                    plugin.getMessageCache().sendMessage(player, "messages.leveled-up", replacer);
                    levelPlayer.addLevel(1);

                    PlayerUtils.dispatchCommands(player, upgrade.getCommands());

                    this.open(player, page);
                    return;
                }

                plugin.getMessageCache().sendMessage(player, "messages.level-too-high");
            });
            index++;

        }

        player.openInventory(builder.build());
    }

    public double calculatePrice(int level) {
        return this.plugin.getSettingsConfig().getInt("base-price") * Math.pow(this.plugin.getSettingsConfig().getDouble("price-increase"), level - 1);
    }
}
