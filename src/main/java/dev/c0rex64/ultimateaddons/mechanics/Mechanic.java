package dev.c0rex64.ultimateaddons.mechanics;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface Mechanic {
    
    String getName();
    
    String getDescription();
    
    void enable();
    
    void disable();
    
    void reload();
    
    boolean isEnabled();
    
    ConfigurationSection getConfig();
    
    void openSettingsGUI(Player player);
} 