package me.nik.combatplus.listeners;

import me.nik.combatplus.CombatPlus;
import me.nik.combatplus.files.Config;
import me.nik.combatplus.files.Lang;
import me.nik.combatplus.utils.Messenger;
import me.nik.combatplus.utils.MiscUtils;
import me.nik.combatplus.utils.WorldUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EnchantedGoldenApple implements Listener {

    private final CombatPlus plugin;
    private final WorldUtils worldUtils = new WorldUtils();

    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private final int cdtime = Config.get().getInt("golden_apple_cooldown.enchanted_golden_apple.cooldown");
    private final boolean actionbar = Config.get().getBoolean("golden_apple_cooldown.enchanted_golden_apple.actionbar");
    public static String PAPICOOLDOWN = "Ready";

    public EnchantedGoldenApple(CombatPlus plugin) {
        this.plugin = plugin;
    }

    private void taskRun(UUID uuid) {
        cooldown.put(uuid, System.currentTimeMillis());
        new BukkitRunnable() {

            @Override
            public void run() {
                cooldown.remove(uuid);
                PAPICOOLDOWN = "Ready";
            }
        }.runTaskLaterAsynchronously(plugin, cdtime * 20);
    }

    // This Listener adds a cooldown between eating Enchanted Golden Apples

    @EventHandler(ignoreCancelled = true)
    public void onEatEnchantedGoldenApple(PlayerItemConsumeEvent e) {
        if (worldUtils.gappleDisabledWorlds(e.getPlayer())) return;
        if (e.getPlayer().hasPermission("cp.bypass.gapple")) return;
        if (isEnchantedGoldenApple(e)) {
            final UUID p = e.getPlayer().getUniqueId();
            final Player player = e.getPlayer();
            if (cooldown.containsKey(p)) {
                e.setCancelled(true);
                long secondsleft = ((cooldown.get(p) / 1000) + cdtime) - (System.currentTimeMillis() / 1000);
                player.sendMessage(Messenger.message("enchanted_golden_apple_cooldown").replaceAll("%seconds%", String.valueOf(secondsleft)));
            } else {
                taskRun(p);
                if (MiscUtils.isPlaceholderApiEnabled()) {
                    setupPlaceholder(p);
                }
                Messenger.debug(player, "&3Enchanted Golden Apple Cooldown &f&l>> &6Added to cooldown: &atrue");
                if (actionbar) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (cooldown.containsKey(p)) {
                                long secondsleft = ((cooldown.get(p) / 1000) + cdtime) - (System.currentTimeMillis() / 1000);
                                String message = Lang.get().getString("enchanted_golden_apple_cooldown_actionbar").replaceAll("%seconds%", String.valueOf(secondsleft));
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messenger.format(message)));
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(plugin, 0, 20);
                }
            }
        }
    }

    private void setupPlaceholder(UUID p) {

        new BukkitRunnable() {

            @Override
            public void run() {
                if (cooldown.containsKey(p)) {
                    long secondsleft = ((cooldown.get(p) / 1000) + cdtime) - (System.currentTimeMillis() / 1000);
                    PAPICOOLDOWN = String.valueOf(secondsleft);
                } else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    private boolean isEnchantedGoldenApple(PlayerItemConsumeEvent e) {
        try {
            return e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE;
        } catch (NoSuchFieldError ignored) {
            ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
            ItemStack consumedItem = e.getItem();
            return consumedItem.getType().name().contains("GOLDEN_APPLE") &&
                    !Objects.equals(goldenApple.getData(), consumedItem.getData());
        }
    }
}