package dev.c0rex64.ultimateaddons.mechanics.broadcast;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class AutoBroadcastMechanic implements Mechanic {

    private final UltimateAddons plugin;
    private boolean enabled = false;
    private BukkitTask broadcastTask;
    private List<String> messages;
    private int interval;
    private boolean randomOrder;
    private String prefix;
    private int currentIndex = 0;
    private final Random random = new Random();

    public AutoBroadcastMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Авто-сообщения";
    }

    @Override
    public String getDescription() {
        return "Автоматическая отправка сообщений в чат.";
    }

    @Override
    public void enable() {
        if (enabled) return;
        loadConfig();
        if (messages == null || messages.isEmpty()) {
            plugin.getLogger().warning("Сообщения для авто-вещания не найдены или пусты. Механика отключается.");
            return;
        }
        enabled = true;
        startBroadcasting();
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        if (broadcastTask != null) {
            broadcastTask.cancel();
            broadcastTask = null;
        }
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
        return plugin.getConfigManager().getBroadcastConfig().getConfigurationSection("autobroadcast");
    }

    @Override
    public void openSettingsGUI(Player player) {
        player.sendMessage(ChatUtils.colorize("&cНастройки для этой механики пока не доступны."));
    }

    private void loadConfig() {
        ConfigurationSection config = getConfig();
        if (config == null) {
             plugin.getLogger().warning("Конфигурация для авто-вещания не найдена. Механика отключается.");
             this.enabled = false;
             return;
        }
        this.messages = config.getStringList("messages");
        this.interval = config.getInt("interval", 300);
        this.randomOrder = config.getBoolean("random", false);
        this.prefix = config.getString("prefix", "&6[Broadcast]");
    }

    private void startBroadcasting() {
        if (messages.isEmpty()) return;

        broadcastTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            String message;
            if (randomOrder) {
                message = messages.get(random.nextInt(messages.size()));
            } else {
                message = messages.get(currentIndex);
                currentIndex = (currentIndex + 1) % messages.size();
            }
            Bukkit.broadcastMessage(ChatUtils.colorize(prefix + " " + message));
        }, interval * 20L, interval * 20L);
    }
} 