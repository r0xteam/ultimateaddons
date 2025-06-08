package dev.c0rex64.ultimateaddons.managers;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MechanicManager {
    
    private final UltimateAddons plugin;
    private final Map<String, Mechanic> mechanics = new HashMap<>();
    private final MechanicsGUI mechanicsGUI;
    
    public MechanicManager(UltimateAddons plugin) {
        this.plugin = plugin;
        this.mechanicsGUI = new MechanicsGUI(plugin);
    }
    
    public void registerMechanic(Mechanic mechanic) {
        mechanics.put(mechanic.getName().toLowerCase(), mechanic);
        plugin.getLogger().info("Зарегистрирована механика: " + mechanic.getName());
    }
    
    public void enableAll() {
        mechanics.values().forEach(mechanic -> {
            if (mechanic.getConfig().getBoolean("enabled", true)) {
                mechanic.enable();
                plugin.getLogger().info("Механика " + mechanic.getName() + " включена");
            }
        });
    }
    
    public void disableAll() {
        mechanics.values().forEach(mechanic -> {
            if (mechanic.isEnabled()) {
                mechanic.disable();
                plugin.getLogger().info("Механика " + mechanic.getName() + " отключена");
            }
        });
    }
    
    public void reloadAll() {
        mechanics.values().forEach(Mechanic::reload);
    }
    
    public Mechanic getMechanic(String name) {
        return mechanics.get(name.toLowerCase());
    }
    
    public Collection<Mechanic> getAllMechanics() {
        return mechanics.values();
    }
    
    public void openMainGUI(Player player) {
        mechanicsGUI.open(player);
    }
} 