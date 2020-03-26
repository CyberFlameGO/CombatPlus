package me.nik.combatplus.listeners;

import me.nik.combatplus.CombatPlus;
import me.nik.combatplus.files.Config;
import me.nik.combatplus.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DamageModifiers implements Listener {
    CombatPlus plugin = CombatPlus.getPlugin(CombatPlus.class);
    private void disableSweep(EntityDamageEvent e, Entity player) {
        boolean canceled = e.isCancelled();
        double x = e.getEntity().getVelocity().getX();
        double y = e.getEntity().getVelocity().getY();
        double z = e.getEntity().getVelocity().getZ();
        if (!Config.get().getBoolean("settings.async")) {
            e.setCancelled(true);
            e.getEntity().setVelocity(new Vector().zero());
            if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                if (player.hasPermission("cp.admin")) {
                    player.sendMessage(Messenger.prefix(ChatColor.GREEN + "Canceled Sweep: " + canceled + ChatColor.RED + " Velocity: " + "X: " + x + " Y: " + y + " Z: " + z + ChatColor.YELLOW + " Async: " + "False"));
                }
            }
        } else {
            e.setCancelled(true);
            new BukkitRunnable() {
                final Player p = (Player) player;

                @Override
                public void run() {
                    e.getEntity().setVelocity(new Vector().zero());
                    if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                        if (p.hasPermission("cp.admin")) {
                            p.sendMessage(Messenger.prefix(ChatColor.GREEN + "Canceled Sweep: " + canceled + ChatColor.RED + " Velocity: " + "X: " + x + " Y: " + y + " Z: " + z + ChatColor.YELLOW + " Async: " + "True"));
                            cancel();
                        } else cancel();
                    } else cancel();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void oldPickaxeDmg(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + Config.get().getDouble("advanced.settings.modifiers.old_pickaxes_damage");
        if (!Config.get().getBoolean("settings.async")) {
            e.setDamage(newDmg);
            if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                if (player.hasPermission("cp.admin")) {
                    player.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Pickaxe" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "False"));
                }
            }
        } else {
            new BukkitRunnable() {
                final Player p = (Player) player;

                @Override
                public void run() {
                    e.setDamage(newDmg);
                    if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                        if (p.hasPermission("cp.admin")) {
                            p.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Pickaxe" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "True"));
                            cancel();
                        } else cancel();
                    } else cancel();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void oldAxeDmg(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + Config.get().getDouble("advanced.settings.modifiers.old_axes_damage");
        if (!Config.get().getBoolean("settings.async")) {
            e.setDamage(newDmg);
            if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                if (player.hasPermission("cp.admin")) {
                    player.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Axe" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "False"));
                }
            }
        } else {
            new BukkitRunnable() {
                final Player p = (Player) player;

                @Override
                public void run() {
                    e.setDamage(newDmg);
                    if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                        if (p.hasPermission("cp.admin")) {
                            p.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Axe" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "True"));
                            cancel();
                        } else cancel();
                    } else cancel();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void oldShovelDmg(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + Config.get().getDouble("advanced.settings.modifiers.old_shovels_damage");
        if (!Config.get().getBoolean("settings.async")) {
            e.setDamage(newDmg);
            if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                if (player.hasPermission("cp.admin")) {
                    player.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Shovel" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "False"));
                }
            }
        } else {
            new BukkitRunnable() {
                final Player p = (Player) player;

                @Override
                public void run() {
                    e.setDamage(newDmg);
                    if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                        if (p.hasPermission("cp.admin")) {
                            p.sendMessage(Messenger.prefix(ChatColor.AQUA + "Tool: " + "Shovel" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "True"));
                            cancel();
                        } else cancel();
                    } else cancel();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void oldSwordDmg(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + Config.get().getDouble("advanced.settings.modifiers.old_swords_damage");
        if (!Config.get().getBoolean("settings.async")) {
            e.setDamage(newDmg);
            if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                if (player.hasPermission("cp.admin")) {
                    player.sendMessage(Messenger.prefix(ChatColor.AQUA + "Weapon: " + "Sword" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "False"));
                }
            }
        } else {
            new BukkitRunnable() {
                final Player p = (Player) player;

                @Override
                public void run() {
                    e.setDamage(newDmg);
                    if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                        if (p.hasPermission("cp.admin")) {
                            p.sendMessage(Messenger.prefix(ChatColor.AQUA + "Weapon: " + "Sword" + ChatColor.GREEN + " Damage Dealt: " + newDmg + ChatColor.RED + " Default Damage: " + damageDealt + ChatColor.YELLOW + " Async: " + "True"));
                            cancel();
                        } else cancel();
                    } else cancel();
                }
            }.runTaskAsynchronously(plugin);
        }
    }
    private void oldSharpDamage(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        if (handItem.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            double damageDealt = e.getDamage();
            double sharpLvl = handItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            double oldSharpDmg = sharpLvl >= 1 ? 1 + (sharpLvl - 1) * 0.5 : 0; //1.9+
            double newSharpDmg = sharpLvl >= 1 ? sharpLvl * 1.25 : 0; //1.8
            double total = damageDealt + newSharpDmg;
            if (!Config.get().getBoolean("settings.async")) {
                e.setDamage(damageDealt + newSharpDmg);
                if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                    if (player.hasPermission("cp.admin")) {
                        player.sendMessage(Messenger.prefix(ChatColor.AQUA + "Weapon: " + "Sword" + ChatColor.GREEN + " Total Damage Dealt: " + total + ChatColor.GREEN + " Modified Sharp Damage: " + newSharpDmg + ChatColor.RED + " Default Sharp Damage: " + oldSharpDmg + ChatColor.YELLOW + " Async: " + "False"));
                    }
                }
            } else {
                new BukkitRunnable() {
                    final Player p = (Player) player;

                    @Override
                    public void run() {
                        e.setDamage(damageDealt + newSharpDmg);
                        if (Config.get().getBoolean("settings.developer_mode")) { //Developer Mode
                            if (p.hasPermission("cp.admin")) {
                                p.sendMessage(Messenger.prefix(ChatColor.AQUA + "Weapon: " + "Sword" + ChatColor.GREEN + " Total Damage Dealt: " + total + ChatColor.GREEN + " Modified Sharp Damage: " + newSharpDmg + ChatColor.RED + " Default Sharp Damage: " + oldSharpDmg + ChatColor.YELLOW + " Async: " + "True"));
                                cancel();
                            } else cancel();
                        } else cancel();
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        for (String world : Config.get().getStringList("combat.settings.disabled_worlds")) {
            if (e.getDamager().getWorld().getName().equalsIgnoreCase(world)) return;
        }
        Player player = (Player) e.getDamager();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (Config.get().getBoolean("combat.settings.old_weapon_damage")) {
            if (handItem.getType().name().endsWith("_SWORD")) {
                oldSwordDmg(e, player, handItem);
            }
        }
        if (Config.get().getBoolean("combat.settings.old_tool_damage")) {
            if (handItem.getType().name().endsWith("_PICKAXE")) {
                oldPickaxeDmg(e, player, handItem);
            } else if (handItem.getType().name().endsWith("_AXE")) {
                oldAxeDmg(e, player, handItem);
            } else if (handItem.getType().name().endsWith("_SPADE") || handItem.getType().name().endsWith("_SHOVEL")) {
                oldShovelDmg(e, player, handItem);
            }
        }
        if (Config.get().getBoolean("combat.settings.disable_sweep_attacks")) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                disableSweep(e, player);
            }
        }
        if (Config.get().getBoolean("combat.settings.old_sharpness")) {
            oldSharpDamage(e, player, handItem);
        }
    }
}