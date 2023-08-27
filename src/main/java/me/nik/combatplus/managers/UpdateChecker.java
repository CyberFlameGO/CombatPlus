package me.nik.combatplus.managers;

import me.nik.combatplus.CombatPlus;
import me.nik.combatplus.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker extends BukkitRunnable implements Listener {

    private final CombatPlus plugin;

    public UpdateChecker(CombatPlus plugin) {
        this.plugin = plugin;
    }

    private String newVersion;

    @Override
    public void run() {

        try {

            URLConnection connection = new URL("https://raw.githubusercontent.com/NikV2/CombatPlus/master/version.txt").openConnection();

            connection.addRequestProperty("User-Agent", "Mozilla/4.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            this.newVersion = reader.readLine();

            reader.close();

        } catch (IOException e) {
            ChatUtils.log("Couldn't check for updates, Is the server connected to the internet?");
            return;
        }

        if (!this.plugin.getDescription().getVersion().equals(this.newVersion)) {

            Bukkit.getServer().getConsoleSender().sendMessage(
                    MsgType.UPDATE_REMINDER.getMessage()
                            .replace("%current%", this.plugin.getDescription().getVersion())
                            .replace("%new%", this.newVersion)
            );

            Bukkit.getPluginManager().registerEvents(this, this.plugin);

        } else Bukkit.getServer().getConsoleSender().sendMessage(MsgType.CONSOLE_UPDATE_NOT_FOUND.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (player.hasPermission(Permissions.ADMIN.getPermission())) {

            player.sendMessage(MsgType.UPDATE_REMINDER.getMessage()
                    .replace("%current%", this.plugin.getDescription().getVersion())
                    .replace("%new%", this.newVersion));
        }
    }
}