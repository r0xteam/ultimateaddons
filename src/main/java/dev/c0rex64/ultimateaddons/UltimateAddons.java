package dev.c0rex64.ultimateaddons;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.c0rex64.ultimateaddons.commands.MainCommand;
import dev.c0rex64.ultimateaddons.config.ConfigManager;
import dev.c0rex64.ultimateaddons.managers.MechanicManager;
import dev.c0rex64.ultimateaddons.mechanics.broadcast.AutoBroadcastMechanic;
import dev.c0rex64.ultimateaddons.mechanics.effects.VisualEffectsMechanic;
import dev.c0rex64.ultimateaddons.mechanics.joinquit.CustomJoinQuitMechanic;
import dev.c0rex64.ultimateaddons.mechanics.maintenance.MaintenanceMechanic;
import dev.c0rex64.ultimateaddons.mechanics.teleport.TeleportMechanic;
import dev.c0rex64.ultimateaddons.mechanics.weather.WeatherMechanic;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateAddons extends JavaPlugin {
    
    private static UltimateAddons instance;
    private ProtocolManager protocolManager;
    private MechanicManager mechanicManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        protocolManager = ProtocolLibrary.getProtocolManager();
        
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        
        mechanicManager = new MechanicManager(this);
        mechanicManager.registerMechanic(new WeatherMechanic(this));
        mechanicManager.registerMechanic(new AutoBroadcastMechanic(this));
        mechanicManager.registerMechanic(new CustomJoinQuitMechanic(this));
        mechanicManager.registerMechanic(new MaintenanceMechanic(this));
        mechanicManager.registerMechanic(new VisualEffectsMechanic(this));
        mechanicManager.registerMechanic(new TeleportMechanic(this));
        mechanicManager.enableAll();
        
        getCommand("ultimateaddons").setExecutor(new MainCommand(this));
        
        getLogger().info("UltimateAddons v" + getDescription().getVersion() + " успешно загружен!");
    }
    
    @Override
    public void onDisable() {
        if (mechanicManager != null) {
            mechanicManager.disableAll();
        }
        
        getLogger().info("UltimateAddons отключен!");
    }
    
    public static UltimateAddons getInstance() {
        return instance;
    }
    
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    
    public MechanicManager getMechanicManager() {
        return mechanicManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
} 