package me.nik.combatplus;

import me.nik.combatplus.commands.CommandManager;
import me.nik.combatplus.files.Config;
import me.nik.combatplus.files.Lang;
import me.nik.combatplus.handlers.PapiHook;
import me.nik.combatplus.handlers.UpdateChecker;
import me.nik.combatplus.listeners.AttributesSet;
import me.nik.combatplus.listeners.Blocking;
import me.nik.combatplus.listeners.BowBoost;
import me.nik.combatplus.listeners.DamageModifiers;
import me.nik.combatplus.listeners.DisabledItems;
import me.nik.combatplus.listeners.EnchantedGoldenApple;
import me.nik.combatplus.listeners.Enderpearl;
import me.nik.combatplus.listeners.FishingRodKnockback;
import me.nik.combatplus.listeners.GoldenApple;
import me.nik.combatplus.listeners.GuiListener;
import me.nik.combatplus.listeners.ItemFrameRotate;
import me.nik.combatplus.listeners.Offhand;
import me.nik.combatplus.listeners.PlayerRegen;
import me.nik.combatplus.listeners.fixes.Projectiles;
import me.nik.combatplus.managers.MsgType;
import me.nik.combatplus.utils.ACManager;
import me.nik.combatplus.utils.CustomRecipes;
import me.nik.combatplus.utils.Messenger;
import me.nik.combatplus.utils.MiscUtils;
import me.nik.combatplus.utils.ResetStats;
import me.nik.combatplus.utils.SetAttackSpeed;
import me.nik.combatplus.utils.SetCustomHealth;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class CombatPlus extends JavaPlugin {

    private Config config;
    private Lang lang;

    @Override
    public void onEnable() {
        this.lang = new Lang();
        this.config = new Config();

        //Load Files
        loadFiles();

        Messenger.initialize(lang, config);

        //Startup Message
        consoleMessage("");
        consoleMessage("          " + ChatColor.RED + "Combat Plus v" + this.getDescription().getVersion());
        consoleMessage("");
        consoleMessage("             " + ChatColor.WHITE + "Author: Nik");
        consoleMessage("");

        //Unsupported Version Checker
        checkSupported();

        //Load Commands
        getCommand("combatplus").setExecutor(new CommandManager(this));

        //Load Listeners
        initialize();

        //Load Player Stats to allow reloading availability
        loadStats();

        //Check for Updates
        checkForUpdates();

        //Load bStats
        new MetricsLite(this, 6982);

        //Hook PlaceholderAPI
        if (MiscUtils.isPlaceholderApiEnabled()) {
            new PapiHook(this).register();
        }

        //Hook AntiCheats
        new ACManager().hookMatrixAC();
    }
    @Override
    public void onDisable() {
        //Load Default Stats to avoid server damage
        setDefaultStats();

        //Reload Files
        config.reload();
        config.save();
        lang.reload();
        lang.save();

        //Done
        consoleMessage(Messenger.message(MsgType.CONSOLE_DISABLED));
    }

    @Override
    public FileConfiguration getConfig() {
        return config.get();
    }

    @Override
    public void saveConfig() {
        config.save();
    }

    @Override
    public void reloadConfig() {
        config.reload();
    }

    public void setupConfig() {
        config.setup(this);
    }

    /**
     * Load all the built-in files
     */
    private void loadFiles() {
        config.setup(this);
        config.addDefaults();
        config.get().options().copyDefaults(true);
        config.save();
        lang.setup(this);
        lang.addDefaults();
        lang.get().options().copyDefaults(true);
        lang.save();
    }

    private void checkForUpdates() {
        if (isEnabled("settings.check_for_updates")) {
            BukkitTask updateChecker = new UpdateChecker(this).runTaskAsynchronously(this);
        } else {
            consoleMessage(Messenger.message(MsgType.CONSOLE_UPDATE_DISABLED));
        }
    }

    /**
     * Default Stats to avoid Server Damage
     */
    private void setDefaultStats() {
        if (serverVersion("1.8")) return;
        Bukkit.getOnlinePlayers().forEach(player -> {
            final double defaultHealth = config.get().getDouble("advanced.settings.base_player_health");
            final AttributeInstance playerMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            playerMaxHealth.setBaseValue(defaultHealth);
            final double defaultAttSpd = config.get().getDouble("advanced.settings.new_pvp.attack_speed");
            final AttributeInstance playerAttSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
            playerAttSpeed.setBaseValue(defaultAttSpd);
            player.saveData();
        });
    }

    /**
     * Load 1.8 Attack Speed and Custom Stats on startup
     * Potentially adding Reloading Compatibility
     */
    private void loadStats() {
        if (serverVersion("1.8")) return;
        SetAttackSpeed setAttackSpeed = new SetAttackSpeed(this);
        ResetStats resetStats = new ResetStats(this);
        SetCustomHealth setCustomHealth = new SetCustomHealth(this);

        if (isEnabled("combat.settings.old_pvp")) {
            this.getServer().getOnlinePlayers().forEach(setAttackSpeed::setAttackSpd);
        } else {
            this.getServer().getOnlinePlayers().forEach(resetStats::resetAttackSpeed);
        }

        if (isEnabled("custom.player_health.enabled")) {
            this.getServer().getOnlinePlayers().forEach(setCustomHealth::setHealth);
        } else {
            this.getServer().getOnlinePlayers().forEach(resetStats::resetMaxHealth);
        }
    }

    /**
     * Initialize enabled Listeners
     */
    private void initialize() {
        consoleMessage(Messenger.message(MsgType.CONSOLE_INITIALIZE));

        if (isEnabled("combat.settings.old_pvp") || isEnabled("custom.player_health.enabled")) {
            registerEvent(new AttributesSet(this));
        }
        if (isEnabled("combat.settings.old_weapon_damage") || isEnabled("combat.settings.old_tool_damage") || isEnabled("combat.settings.disable_sweep_attacks.enabled")) {
            registerEvent(new DamageModifiers(this));
        }
        if (isEnabled("combat.settings.disable_arrow_boost")) {
            registerEvent(new BowBoost(this));
        }
        if (isEnabled("combat.settings.old_player_regen")) {
            registerEvent(new PlayerRegen(this));
        }
        if (isEnabled("disabled_items.enabled")) {
            registerEvent(new DisabledItems(this));
        }
        if (isEnabled("disable_item_frame_rotation.enabled")) {
            registerEvent(new ItemFrameRotate(this));
        }
        if (isEnabled("disable_offhand.enabled")) {
            registerEvent(new Offhand(this));
        }
        if (isEnabled("fixes.projectile_fixer")) {
            registerEvent(new Projectiles());
        }
        if (isEnabled("golden_apple_cooldown.golden_apple.enabled")) {
            registerEvent(new GoldenApple(this));
        }
        if (isEnabled("golden_apple_cooldown.enchanted_golden_apple.enabled")) {
            registerEvent(new EnchantedGoldenApple(this));
        }
        if (isEnabled("enderpearl_cooldown.enabled")) {
            registerEvent(new Enderpearl(this));
        }
        if (isEnabled("recipes.enchanted_golden_apple")) {
            try {
                this.getServer().addRecipe(new CustomRecipes(this).enchantedGoldenAppleRecipe());
            } catch (Exception ignored) {
            }
        }
        if (isEnabled("knockback.fishing_rod.enabled")) {
            registerEvent(new FishingRodKnockback(this));
        }
        if (isEnabled("combat.settings.sword_blocking.enabled")) {
            registerEvent(new Blocking(this));
        }
        //GUI Listener (Do not remove this, idiot nik)
        registerEvent(new GuiListener());
    }

    /**
     * Disable unsupported features
     */
    private void checkSupported() {
        if (serverVersion("1.8")) {
            setBoolean("combat.settings.old_pvp", false);
            setBoolean("combat.settings.old_weapon_damage", false);
            setBoolean("combat.settings.old_tool_damage", false);
            setBoolean("combat.settings.old_sharpness", false);
            setBoolean("combat.settings.disable_sweep_attacks.enabled", false);
            setBoolean("combat.settings.old_player_regen", false);
            setBoolean("custom.player_health.enabled", false);
            setBoolean("disable_offhand.enabled", false);
            setBoolean("recipes.enchanted_golden_apple", false);
            setBoolean("knockback.fishing_rod.enabled", false);
            setBoolean("golden_apple_cooldown.golden_apple.actionbar", false);
            setBoolean("golden_apple_cooldown.enchanted_golden_apple.actionbar", false);
            setBoolean("enderpearl_cooldown.actionbar", false);
            setBoolean("combat.settings.sword_blocking.enabled", false);
            config.save();
            config.reload();
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_VERSION));
        } else if (serverVersion("1.9")) {
            setBoolean("combat.settings.disable_sweep_attacks.enabled", false);
            setBoolean("recipes.enchanted_golden_apple", false);
            setBoolean("knockback.fishing_rod.enabled", false);
            config.save();
            config.reload();
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_VERSION));
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_SWEEP_ATTACK));
        } else if (serverVersion("1.10")) {
            setBoolean("combat.settings.disable_sweep_attacks.enabled", false);
            setBoolean("recipes.enchanted_golden_apple", false);
            setBoolean("knockback.fishing_rod.enabled", false);
            config.save();
            config.reload();
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_VERSION));
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_SWEEP_ATTACK));
        } else if (serverVersion("1.11")) {
            setBoolean("recipes.enchanted_golden_apple", false);
            setBoolean("knockback.fishing_rod.enabled", false);
            config.save();
            config.reload();
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_VERSION));
        } else if (serverVersion("1.12")) {
            setBoolean("recipes.enchanted_golden_apple", false);
            setBoolean("knockback.fishing_rod.enabled", false);
            config.save();
            config.reload();
            consoleMessage(Messenger.message(MsgType.CONSOLE_UNSUPPORTED_VERSION));
        }
    }

    /**
     * @param version The version to look for
     * @return Whether or not the Server's running on the specified version
     */
    public boolean serverVersion(String version) {
        return Bukkit.getVersion().contains(version);
    }

    /**
     * Registers the specified Listener
     *
     * @param listener The listener to register
     */
    public void registerEvent(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * @param path The path to the boolean
     * @return Whether or not the config boolean is true
     */
    private boolean isEnabled(String path) {
        return config.get().getBoolean(path);
    }

    /**
     * @param path  Path to the boolean
     * @param value true/false
     */
    private void setBoolean(String path, boolean value) {
        config.get().set(path, value);
    }

    /**
     * @param message The console message to send to the Server (ChatColor Friendly)
     */
    public void consoleMessage(String message) {
        this.getServer().getConsoleSender().sendMessage(message);
    }
}
