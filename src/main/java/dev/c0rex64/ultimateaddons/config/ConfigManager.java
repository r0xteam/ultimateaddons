package dev.c0rex64.ultimateaddons.config;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    private final UltimateAddons plugin;
    private FileConfiguration mainConfig;
    private FileConfiguration weatherConfig;
    private FileConfiguration broadcastConfig;
    private FileConfiguration joinQuitConfig;
    private FileConfiguration maintenanceConfig;
    private FileConfiguration effectsConfig;
    private FileConfiguration teleportConfig;
    
    public ConfigManager(UltimateAddons plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        plugin.saveDefaultConfig();
        mainConfig = plugin.getConfig();
        
        loadWeatherConfig();
        loadBroadcastConfig();
        loadJoinQuitConfig();
        loadMaintenanceConfig();
        loadEffectsConfig();
        loadTeleportConfig();
    }
    
    private void loadWeatherConfig() {
        File weatherFile = new File(plugin.getDataFolder(), "weather.yml");
        if (!weatherFile.exists()) {
            plugin.saveResource("weather.yml", false);
        }
        weatherConfig = YamlConfiguration.loadConfiguration(weatherFile);
    }
    
    private void loadBroadcastConfig() {
        File broadcastFile = new File(plugin.getDataFolder(), "broadcast.yml");
        if (!broadcastFile.exists()) {
            plugin.saveResource("broadcast.yml", false);
        }
        broadcastConfig = YamlConfiguration.loadConfiguration(broadcastFile);
    }
    
    private void loadJoinQuitConfig() {
        File joinQuitFile = new File(plugin.getDataFolder(), "joinquit.yml");
        if (!joinQuitFile.exists()) {
            plugin.saveResource("joinquit.yml", false);
        }
        joinQuitConfig = YamlConfiguration.loadConfiguration(joinQuitFile);
    }
    
    private void loadMaintenanceConfig() {
        File maintenanceFile = new File(plugin.getDataFolder(), "maintenance.yml");
        if (!maintenanceFile.exists()) {
            plugin.saveResource("maintenance.yml", false);
        }
        maintenanceConfig = YamlConfiguration.loadConfiguration(maintenanceFile);
    }
    
    private void loadEffectsConfig() {
        File effectsFile = new File(plugin.getDataFolder(), "effects.yml");
        if (!effectsFile.exists()) {
            plugin.saveResource("effects.yml", false);
        }
        effectsConfig = YamlConfiguration.loadConfiguration(effectsFile);
    }

    private void loadTeleportConfig() {
        File teleportFile = new File(plugin.getDataFolder(), "teleport.yml");
        if (!teleportFile.exists()) {
            plugin.saveResource("teleport.yml", false);
        }
        teleportConfig = YamlConfiguration.loadConfiguration(teleportFile);
    }
    
    public void saveWeatherConfig() {
        try {
            weatherConfig.save(new File(plugin.getDataFolder(), "weather.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить weather.yml: " + e.getMessage());
        }
    }
    
    public void saveBroadcastConfig() {
        try {
            broadcastConfig.save(new File(plugin.getDataFolder(), "broadcast.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить broadcast.yml: " + e.getMessage());
        }
    }
    
    public void saveJoinQuitConfig() {
        try {
            joinQuitConfig.save(new File(plugin.getDataFolder(), "joinquit.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить joinquit.yml: " + e.getMessage());
        }
    }
    
    public void saveMaintenanceConfig() {
        try {
            maintenanceConfig.save(new File(plugin.getDataFolder(), "maintenance.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить maintenance.yml: " + e.getMessage());
        }
    }
    
    public void saveEffectsConfig() {
        try {
            effectsConfig.save(new File(plugin.getDataFolder(), "effects.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить effects.yml: " + e.getMessage());
        }
    }

    public void saveTeleportConfig() {
        try {
            teleportConfig.save(new File(plugin.getDataFolder(), "teleport.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить teleport.yml: " + e.getMessage());
        }
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        mainConfig = plugin.getConfig();
        loadWeatherConfig();
        loadBroadcastConfig();
        loadJoinQuitConfig();
        loadMaintenanceConfig();
        loadEffectsConfig();
        loadTeleportConfig();
    }
    
    public FileConfiguration getMainConfig() {
        return mainConfig;
    }
    
    public FileConfiguration getWeatherConfig() {
        return weatherConfig;
    }
    
    public FileConfiguration getBroadcastConfig() {
        return broadcastConfig;
    }
    
    public FileConfiguration getJoinQuitConfig() {
        return joinQuitConfig;
    }
    
    public FileConfiguration getMaintenanceConfig() {
        return maintenanceConfig;
    }
    
    public FileConfiguration getEffectsConfig() {
        return effectsConfig;
    }

    public FileConfiguration getTeleportConfig() {
        return teleportConfig;
    }
} 