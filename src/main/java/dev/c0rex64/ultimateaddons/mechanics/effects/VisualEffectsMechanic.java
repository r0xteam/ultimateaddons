package dev.c0rex64.ultimateaddons.mechanics.effects;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VisualEffectsMechanic implements Mechanic {

    private final UltimateAddons plugin;
    private boolean enabled = false;
    private BukkitTask effectsTask;
    private final Map<UUID, PlayerEffectData> playerEffects = new HashMap<>();

    public VisualEffectsMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Визуальные эффекты";
    }

    @Override
    public String getDescription() {
        return "Красивые анимированные эффекты частиц для игроков.";
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        startEffectsTask();
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        if (effectsTask != null) {
            effectsTask.cancel();
        }
        playerEffects.clear();
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
        return plugin.getConfigManager().getEffectsConfig().getConfigurationSection("visual-effects");
    }

    @Override
    public void openSettingsGUI(Player player) {
        new VisualEffectsGUI(plugin, this, player).open();
    }

    private void startEffectsTask() {
        effectsTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("ultimateaddons.effects")) continue;
                
                PlayerEffectData data = playerEffects.computeIfAbsent(player.getUniqueId(), 
                    k -> new PlayerEffectData());
                
                if (data.effectType != EffectType.NONE) {
                    renderEffect(player, data);
                }
                
                data.tick++;
            }
        }, 0L, 1L);
    }

    private void renderEffect(Player player, PlayerEffectData data) {
        Location loc = player.getLocation().add(0, 1, 0);
        double time = data.tick * 0.1;

        switch (data.effectType) {
            case SPIRAL -> renderSpiral(loc, time, data.particle, data.color);
            case RING -> renderRing(loc, time, data.particle, data.color);
            case SPHERE -> renderSphere(loc, time, data.particle, data.color);
            case HELIX -> renderHelix(loc, time, data.particle, data.color);
            case WINGS -> renderWings(loc, time, data.particle, data.color);
            case AURA -> renderAura(loc, time, data.particle, data.color);
        }
    }

    private void renderSpiral(Location center, double time, Particle particle, ParticleColor color) {
        for (int i = 0; i < 3; i++) {
            double angle = time + (i * Math.PI * 2 / 3);
            double radius = 1.5;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.sin(time * 2) * 0.5;
            
            Location particleLoc = center.clone().add(x, y, z);
            spawnColoredParticle(particleLoc, particle, color);
        }
    }

    private void renderRing(Location center, double time, Particle particle, ParticleColor color) {
        int points = 20;
        double radius = 1.0 + Math.sin(time) * 0.3;
        
        for (int i = 0; i < points; i++) {
            double angle = (i * Math.PI * 2 / points) + time;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location particleLoc = center.clone().add(x, 0, z);
            spawnColoredParticle(particleLoc, particle, color);
        }
    }

    private void renderSphere(Location center, double time, Particle particle, ParticleColor color) {
        int points = 30;
        double radius = 1.2;
        
        for (int i = 0; i < points; i++) {
            double phi = Math.acos(1 - 2 * Math.random());
            double theta = 2 * Math.PI * Math.random() + time;
            
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            
            Location particleLoc = center.clone().add(x, y, z);
            spawnColoredParticle(particleLoc, particle, color);
        }
    }

    private void renderHelix(Location center, double time, Particle particle, ParticleColor color) {
        for (int i = 0; i < 2; i++) {
            double offset = i * Math.PI;
            double angle = time + offset;
            double radius = 1.0;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = (time % (Math.PI * 4)) - Math.PI * 2;
            
            if (Math.abs(y) <= 2) {
                Location particleLoc = center.clone().add(x, y, z);
                spawnColoredParticle(particleLoc, particle, color);
            }
        }
    }

    private void renderWings(Location center, double time, Particle particle, ParticleColor color) {
        double wingSpan = 2.0;
        double flapSpeed = Math.sin(time * 3) * 0.3;
        
        for (int wing = -1; wing <= 1; wing += 2) {
            for (int i = 0; i < 10; i++) {
                double t = i / 9.0;
                double x = wing * wingSpan * t;
                double y = Math.sin(t * Math.PI) * (0.8 + flapSpeed);
                double z = -t * 0.5;
                
                Location particleLoc = center.clone().add(x, y, z);
                spawnColoredParticle(particleLoc, particle, color);
            }
        }
    }

    private void renderAura(Location center, double time, Particle particle, ParticleColor color) {
        int particles = 15;
        double radius = 1.5 + Math.sin(time * 2) * 0.5;
        
        for (int i = 0; i < particles; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = Math.random() * radius;
            double x = Math.cos(angle) * distance;
            double z = Math.sin(angle) * distance;
            double y = Math.random() * 2 - 1;
            
            Location particleLoc = center.clone().add(x, y, z);
            spawnColoredParticle(particleLoc, particle, color);
        }
    }

    private void spawnColoredParticle(Location loc, Particle particle, ParticleColor color) {
        if (particle == Particle.REDSTONE) {
            loc.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0, 
                new Particle.DustOptions(color.toBukkitColor(), 1.0f));
        } else {
            loc.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
        }
    }

    public void setPlayerEffect(Player player, EffectType type, Particle particle, ParticleColor color) {
        PlayerEffectData data = playerEffects.computeIfAbsent(player.getUniqueId(), 
            k -> new PlayerEffectData());
        data.effectType = type;
        data.particle = particle;
        data.color = color;
        data.tick = 0;
    }

    public void removePlayerEffect(Player player) {
        PlayerEffectData data = playerEffects.get(player.getUniqueId());
        if (data != null) {
            data.effectType = EffectType.NONE;
        }
    }

    public enum EffectType {
        NONE, SPIRAL, RING, SPHERE, HELIX, WINGS, AURA
    }

    public enum ParticleColor {
        RED(255, 0, 0),
        GREEN(0, 255, 0),
        BLUE(0, 0, 255),
        YELLOW(255, 255, 0),
        PURPLE(255, 0, 255),
        CYAN(0, 255, 255),
        WHITE(255, 255, 255),
        ORANGE(255, 165, 0);

        private final int r, g, b;

        ParticleColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public org.bukkit.Color toBukkitColor() {
            return org.bukkit.Color.fromRGB(r, g, b);
        }
    }

    private static class PlayerEffectData {
        EffectType effectType = EffectType.NONE;
        Particle particle = Particle.REDSTONE;
        ParticleColor color = ParticleColor.BLUE;
        int tick = 0;
    }
} 