package dev.c0rex64.ultimateaddons.mechanics.maintenance;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class MaintenanceMechanic implements Mechanic, Listener {

    private final UltimateAddons plugin;
    private boolean enabled = false;
    private String kickMessage;
    private String maintenanceMotd;

    public MaintenanceMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Режим обслуживания";
    }

    @Override
    public String getDescription() {
        return "Ограничивает доступ к серверу для обычных игроков.";
    }

    @Override
    public void enable() {
        if (enabled) return;
        loadConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        enabled = true;
        kickNonWhitelistedPlayers();
    }

    @Override
    public void disable() {
        if (!enabled) return;
        PlayerLoginEvent.getHandlerList().unregister(this);
        ServerListPingEvent.getHandlerList().unregister(this);
        enabled = false;
    }

    @Override
    public void reload() {
        disable();
        enable();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public ConfigurationSection getConfig() {
        return plugin.getConfigManager().getMaintenanceConfig().getConfigurationSection("maintenance");
    }

    @Override
    public void openSettingsGUI(Player player) {
        player.sendMessage(ChatUtils.colorize("&cНастройки для этой механики пока не доступны."));
    }

    private void loadConfig() {
        ConfigurationSection config = getConfig();
        if (config == null) {
            plugin.getLogger().warning("Конфигурация для режима обслуживания не найдена. Используются значения по умолчанию.");
            this.kickMessage = "&cСервер находится на техническом обслуживании. Пожалуйста, зайдите позже.";
            this.maintenanceMotd = "&cСервер на обслуживании!";
            return;
        }
        this.kickMessage = config.getString("kick-message", "&cСервер находится на техническом обслуживании. Пожалуйста, зайдите позже.");
        this.maintenanceMotd = config.getString("maintenance-motd", "&cСервер на обслуживании!");
    }

    private void kickNonWhitelistedPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("ultimateaddons.maintenance.bypass")) {
                player.kickPlayer(ChatUtils.colorize(kickMessage));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!enabled) return;
        if (!event.getPlayer().hasPermission("ultimateaddons.maintenance.bypass")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatUtils.colorize(kickMessage));
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (!enabled) return;
        event.setMotd(ChatUtils.colorize(maintenanceMotd));
        event.setMaxPlayers(0);
    }
} 