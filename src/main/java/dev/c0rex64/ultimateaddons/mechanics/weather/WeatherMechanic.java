package dev.c0rex64.ultimateaddons.mechanics.weather;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class WeatherMechanic implements Mechanic {
    
    private final UltimateAddons plugin;
    private boolean enabled = false;
    private BukkitTask weatherTask;
    private BukkitTask stormTask;
    private BukkitTask hurricaneTask;
    private final Map<UUID, WeatherData> playerWeatherData = new HashMap<>();
    private final Random random = new Random();
    
    private WeatherType currentWeather = WeatherType.CLEAR;
    private double windStrength = 0.0;
    private double windDirection = 0.0;
    private double temperature = 20.0;
    private double humidity = 50.0;

    private final EnumSet<Biome> coldBiomes = EnumSet.of(
            Biome.SNOWY_PLAINS, Biome.ICE_SPIKES, Biome.SNOWY_TAIGA,
            Biome.SNOWY_BEACH, Biome.GROVE, Biome.SNOWY_SLOPES,
            Biome.JAGGED_PEAKS, Biome.FROZEN_PEAKS, Biome.FROZEN_RIVER,
            Biome.DEEP_FROZEN_OCEAN
    );

    private final EnumSet<Biome> dryBiomes = EnumSet.of(
            Biome.DESERT, Biome.BADLANDS, Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA
    );
    
    public WeatherMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Погода";
    }
    
    @Override
    public String getDescription() {
        return "Реалистичная система погоды с штормами и эффектами";
    }
    
    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        
        startWeatherCycle();
        startEffectsTask();
        
        Bukkit.getPluginManager().registerEvents(new WeatherListener(this), plugin);
    }
    
    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        
        if (weatherTask != null) {
            weatherTask.cancel();
        }
        if (stormTask != null) {
            stormTask.cancel();
        }
        if (hurricaneTask != null) {
            hurricaneTask.cancel();
        }
        
        playerWeatherData.clear();
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
        return plugin.getConfigManager().getWeatherConfig().getConfigurationSection("weather");
    }
    
    @Override
    public void openSettingsGUI(Player player) {
        new WeatherSettingsGUI(plugin, this, player).open();
    }
    
    private void startWeatherCycle() {
        weatherTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updateWeatherConditions();
            
            if (random.nextDouble() < getConfig().getDouble("storm-chance", 0.1)) {
                if (currentWeather == WeatherType.RAIN || currentWeather == WeatherType.HEAVY_RAIN) {
                    startStorm();
                }
            }
        }, 0L, getConfig().getLong("update-interval", 600L) * 20L);
    }
    
    private void startEffectsTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("ultimateaddons.weather")) continue;
                
                applyWeatherEffects(player);
                
                if (currentWeather != WeatherType.CLEAR) {
                    showWeatherParticles(player);
                }
            }
        }, 0L, 4L);
    }
    
    private void updateWeatherConditions() {
        double changeChance = random.nextDouble();
        
        if (changeChance < 0.3) {
            WeatherType[] types = WeatherType.values();
            currentWeather = types[random.nextInt(types.length)];
            
            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() == World.Environment.NORMAL) {
                    switch (currentWeather) {
                        case CLEAR, FOG -> {
                            world.setStorm(false);
                            world.setThundering(false);
                        }
                        case RAIN, DRIZZLE -> {
                            world.setStorm(true);
                            world.setThundering(false);
                        }
                        case HEAVY_RAIN, STORM -> {
                            world.setStorm(true);
                            world.setThundering(true);
                        }
                    }
                }
            }
        }
        
        windStrength = random.nextDouble() * getConfig().getDouble("max-wind-strength", 1.5);
        windDirection = random.nextDouble() * 360;
        temperature = 15 + random.nextDouble() * 20;
        humidity = 30 + random.nextDouble() * 60;
    }
    
    private void applyWeatherEffects(Player player) {
        WeatherData data = playerWeatherData.computeIfAbsent(player.getUniqueId(), k -> new WeatherData());
        
        if (windStrength > 1.0) {
            double radians = Math.toRadians(windDirection);
            double windX = Math.cos(radians) * windStrength * 0.1;
            double windZ = Math.sin(radians) * windStrength * 0.1;
            
            if (player.isFlying() || !player.isOnGround()) {
                player.setVelocity(player.getVelocity().add(
                    new org.bukkit.util.Vector(windX, 0, windZ)
                ));
            }
        }
        
        if (currentWeather == WeatherType.FOG) {
            ConfigurationSection fogConfig = getWeatherTypeConfig(WeatherType.FOG);
            float density = (float) fogConfig.getDouble("fog-density", 0.1);
            sendFogEffect(player, data, density);
        }
        
        if (temperature < 5 && currentWeather != WeatherType.CLEAR) {
            ConfigurationSection weatherConfig = getWeatherTypeConfig(currentWeather);
            if (weatherConfig != null && random.nextDouble() < weatherConfig.getDouble("freeze-chance", 0.1)) {
                 player.setFreezeTicks(Math.min(player.getFreezeTicks() + 2, 200));
            }
        }
    }
    
    private void showWeatherParticles(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        Biome biome = world.getBiome(loc);

        WeatherType effectiveWeather = getEffectiveWeatherForBiome(currentWeather, biome);
        if (effectiveWeather == WeatherType.CLEAR) return;

        ConfigurationSection weatherConfig = getWeatherTypeConfig(effectiveWeather);
        if (weatherConfig == null) return;

        int amount = weatherConfig.getInt("particle-amount", 10);
        double speed = weatherConfig.getDouble("particle-speed", -0.5);

        Particle particle;
        int radiusX = 20, radiusY = 15, radiusZ = 20;

        switch (effectiveWeather) {
            case DRIZZLE -> particle = Particle.WATER_DROP;
            case RAIN, HEAVY_RAIN, STORM -> particle = Particle.FALLING_WATER;
            case SNOW -> particle = Particle.SNOWFLAKE;
            case FOG -> {
                particle = Particle.WHITE_ASH;
                radiusY = 5;
            }
            default -> {
                return;
            }
        }

        for (int i = 0; i < amount; i++) {
            Location particleLoc = loc.clone().add(
                random.nextDouble() * (radiusX * 2) - radiusX,
                random.nextDouble() * radiusY,
                random.nextDouble() * (radiusZ * 2) - radiusZ
            );
            world.spawnParticle(particle, particleLoc, 1, 0, speed, 0, 0);
        }

        if (effectiveWeather == WeatherType.HEAVY_RAIN || effectiveWeather == WeatherType.STORM || effectiveWeather == WeatherType.HURRICANE) {
            if (random.nextDouble() < weatherConfig.getDouble("splash-chance", 0.1)) {
                world.spawnParticle(Particle.WATER_SPLASH, loc.clone().add(
                    random.nextDouble() * 10 - 5, 0, random.nextDouble() * 10 - 5
                ), 5, 0.5, 0.1, 0.5, 0);
            }
        }
        
        if (effectiveWeather == WeatherType.HURRICANE) {
            addHurricaneDebris(loc, world, weatherConfig);
        }
    }

    private void addHurricaneDebris(Location loc, World world, ConfigurationSection config) {
        int amount = config.getInt("debris-particle-amount", 30);
        double windX = Math.cos(Math.toRadians(windDirection)) * windStrength * 0.5;
        double windZ = Math.sin(Math.toRadians(windDirection)) * windStrength * 0.5;

        for (int i = 0; i < amount; i++) {
            Location particleLoc = loc.clone().add(
                random.nextDouble() * 40 - 20,
                random.nextDouble() * 20,
                random.nextDouble() * 40 - 20
            );
            // Пыль от блоков земли или листвы для эффекта мусора
            Material debrisMaterial = random.nextBoolean() ? Material.DIRT : Material.OAK_LEAVES;
            world.spawnParticle(Particle.BLOCK_DUST, particleLoc, 5, windX, 0.1, windZ, debrisMaterial.createBlockData());
        }
    }

    private WeatherType getEffectiveWeatherForBiome(WeatherType globalWeather, Biome biome) {
        if (dryBiomes.contains(biome)) {
            // В сухих биомах нет осадков
            if (globalWeather != WeatherType.CLEAR && globalWeather != WeatherType.FOG) {
                return WeatherType.CLEAR;
            }
        } else if (coldBiomes.contains(biome) || temperature < 0) {
            // В холодных биомах или при низкой температуре дождь превращается в снег
             if (globalWeather == WeatherType.RAIN || globalWeather == WeatherType.HEAVY_RAIN ||
                globalWeather == WeatherType.STORM || globalWeather == WeatherType.DRIZZLE ||
                 globalWeather == WeatherType.HURRICANE) {
                return WeatherType.SNOW;
            }
        }
        return globalWeather;
    }

    private ConfigurationSection getWeatherTypeConfig(WeatherType type) {
        return getConfig().getConfigurationSection("types." + type.name());
    }
    
    private void sendFogEffect(Player player, WeatherData data, float density) {
        PacketContainer packet = plugin.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getGameStateIDs().write(0, 8);
        packet.getFloat().write(0, density);
        
        plugin.getProtocolManager().sendServerPacket(player, packet);
    }
    
    private void startStorm() {
        if (stormTask != null && !stormTask.isCancelled()) return;
        
        currentWeather = WeatherType.STORM;
        
        stormTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ConfigurationSection stormConfig = getWeatherTypeConfig(WeatherType.STORM);
            if (stormConfig == null) return;

            // Шанс перерасти в ураган
            if (random.nextDouble() < stormConfig.getDouble("hurricane-chance", 0.05)) {
                startHurricane();
                return;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("ultimateaddons.weather")) continue;
                
                Location loc = player.getLocation();
                World world = loc.getWorld();
                
                if (random.nextDouble() < stormConfig.getDouble("lightning-chance", 0.02)) {
                    Location strikeLocation = loc.clone().add(
                        random.nextDouble() * 100 - 50,
                        0,
                        random.nextDouble() * 100 - 50
                    );
                    strikeLocation.setY(world.getHighestBlockYAt(strikeLocation));
                    
                    world.strikeLightningEffect(strikeLocation);
                    
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                    }, (long)(strikeLocation.distance(loc) / 340 * 20));
                }
                
                if (random.nextDouble() < 0.3) {
                    player.playSound(loc, Sound.WEATHER_RAIN_ABOVE, 0.5f, 1.0f);
                }
            }
        }, 0L, 20L);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (stormTask != null) {
                stormTask.cancel();
                stormTask = null;
                currentWeather = WeatherType.RAIN;
            }
        }, getConfig().getLong("storm-duration", 900L) * 20L);
    }
    
    private void startHurricane() {
        if (hurricaneTask != null && !hurricaneTask.isCancelled()) return;
        
        if (stormTask != null) {
            stormTask.cancel();
            stormTask = null;
        }

        currentWeather = WeatherType.HURRICANE;
        windStrength = getConfig().getDouble("max-wind-strength", 1.5) * 2; 

        Bukkit.broadcastMessage(ChatUtils.colorize("&c&lВНИМАНИЕ! &cНадвигается ураган! Найдите укрытие!"));

        hurricaneTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ConfigurationSection hurricaneConfig = getWeatherTypeConfig(WeatherType.HURRICANE);
            if (hurricaneConfig == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                 if (!player.hasPermission("ultimateaddons.weather")) continue;
                
                if (random.nextDouble() < 0.2) {
                    player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f);
                }

                if (hurricaneConfig.getBoolean("destruction.enabled", false)) {
                     applyDestructiveEffects(player, hurricaneConfig);
                }
            }
        }, 0L, 20L);

        long duration = getConfig().getLong("hurricane-duration", 1200L);
        Bukkit.getScheduler().runTaskLater(plugin, this::stopHurricane, duration * 20L);
    }

    private void stopHurricane() {
        if (hurricaneTask != null) {
            hurricaneTask.cancel();
            hurricaneTask = null;
            currentWeather = WeatherType.HEAVY_RAIN; 
            Bukkit.broadcastMessage(ChatUtils.colorize("&aУраган утих, но сильный дождь продолжается."));
        }
    }

    private void applyDestructiveEffects(Player player, ConfigurationSection config) {
        if (random.nextDouble() > config.getDouble("destruction.break-chance", 0.01)) return;

        Location center = player.getLocation();
        World world = center.getWorld();
        if (world == null) return;
        int radius = config.getInt("destruction.radius", 5);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (random.nextBoolean()) continue; 
                    Block block = center.clone().add(x, y, z).getBlock();
                    Material type = block.getType();
                    if (config.getStringList("destruction.breakable-blocks").contains(type.name())) {
                        block.breakNaturally();
                        world.spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 10, type.createBlockData());
                        return; 
                    }
                }
            }
        }
    }
    
    public WeatherType getCurrentWeather() {
        return currentWeather;
    }
    
    public double getWindStrength() {
        return windStrength;
    }
    
    public double getWindDirection() {
        return windDirection;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public double getHumidity() {
        return humidity;
    }
    
    private static class WeatherData {
        long lastFogPacket = 0;
    }
}