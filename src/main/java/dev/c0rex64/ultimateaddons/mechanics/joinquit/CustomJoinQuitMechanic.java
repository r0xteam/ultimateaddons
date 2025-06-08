package dev.c0rex64.ultimateaddons.mechanics.joinquit;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomJoinQuitMechanic implements Mechanic, Listener {

    private final UltimateAddons plugin;
    private boolean enabled = false;
    private String joinMessage;
    private String quitMessage;

    public CustomJoinQuitMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Сообщения входа/выхода";
    }

    @Override
    public String getDescription() {
        return "Кастомные сообщения при входе и выходе игрока.";
    }

    @Override
    public void enable() {
        if (enabled) return;
        loadConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        enabled = true;
    }

    @Override
    public void disable() {
        if (!enabled) return;
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
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
        return plugin.getConfigManager().getJoinQuitConfig().getConfigurationSection("join-quit-messages");
    }

    @Override
    public void openSettingsGUI(Player player) {
        player.sendMessage(ChatUtils.colorize("&cНастройки для этой механики пока не доступны."));
    }

    private void loadConfig() {
        ConfigurationSection config = getConfig();
        this.joinMessage = config.getString("join-message", "&e[+] &7%player%");
        this.quitMessage = config.getString("quit-message", "&c[-] &7%player%");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if (joinMessage == null || joinMessage.isEmpty()) return;
        
        String message = joinMessage.replace("%player%", event.getPlayer().getName());
        plugin.getServer().broadcastMessage(ChatUtils.colorize(message));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (quitMessage == null || quitMessage.isEmpty()) return;
        
        String message = quitMessage.replace("%player%", event.getPlayer().getName());
        plugin.getServer().broadcastMessage(ChatUtils.colorize(message));
    }
} 