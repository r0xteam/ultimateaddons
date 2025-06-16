package dev.c0rex64.ultimateaddons.mechanics.teleport;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportMechanic implements Mechanic, Listener {

    private final UltimateAddons plugin;
    private boolean enabled = false;
    private BukkitTask portalTask;
    private final Map<UUID, TeleportData> playerData = new HashMap<>();
    private final Map<Location, PortalData> activePortals = new HashMap<>();

    public TeleportMechanic(UltimateAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Телепортация";
    }

    @Override
    public String getDescription() {
        return "Система телепортации с анимированными порталами.";
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startPortalAnimations();
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        if (portalTask != null) {
            portalTask.cancel();
        }
        activePortals.clear();
        playerData.clear();
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
        return plugin.getConfigManager().getTeleportConfig().getConfigurationSection("teleport");
    }

    @Override
    public void openSettingsGUI(Player player) {
        new TeleportGUI(plugin, this, player).open();
    }

    private void startPortalAnimations() {
        portalTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Location, PortalData> entry : activePortals.entrySet()) {
                Location loc = entry.getKey();
                PortalData portal = entry.getValue();
                
                portal.tick++;
                renderPortal(loc, portal);
                
                // Проверяем игроков рядом с порталом
                for (Player player : loc.getWorld().getPlayers()) {
                    if (player.getLocation().distance(loc) <= 2.0) {
                        if (portal.destination != null) {
                            teleportPlayer(player, portal.destination);
                            break;
                        }
                    }
                }
                
                // Удаляем портал через определенное время
                if (portal.tick > getConfig().getInt("portal-duration", 1200)) {
                    activePortals.remove(loc);
                }
            }
        }, 0L, 2L);
    }

    private void renderPortal(Location center, PortalData portal) {
        double time = portal.tick * 0.1;
        
        // Внешнее кольцо
        for (int i = 0; i < 20; i++) {
            double angle = (i * Math.PI * 2 / 20) + time;
            double radius = 2.0 + Math.sin(time * 2) * 0.3;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location particleLoc = center.clone().add(x, 1, z);
            center.getWorld().spawnParticle(Particle.PORTAL, particleLoc, 3, 0.1, 0.1, 0.1, 0);
        }
        
        // Внутренние частицы
        for (int i = 0; i < 15; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 1.5;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.random() * 3;
            
            Location particleLoc = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, particleLoc, 1, 0, 0, 0, 0);
        }
        
        // Центральная спираль
        for (int i = 0; i < 5; i++) {
            double angle = time * 3 + (i * Math.PI * 2 / 5);
            double radius = 0.5;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = 1.5 + Math.sin(time * 4 + i) * 0.5;
            
            Location particleLoc = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    private void teleportPlayer(Player player, Location destination) {
        TeleportData data = playerData.get(player.getUniqueId());
        if (data != null && System.currentTimeMillis() - data.lastTeleport < 3000) {
            return; // Кулдаун
        }
        
        // Эффекты перед телепортацией
        Location playerLoc = player.getLocation();
        for (int i = 0; i < 30; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 2;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.random() * 3;
            
            Location particleLoc = playerLoc.clone().add(x, y, z);
            playerLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 1, 0, 0, 0, 0);
        }
        
        player.playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        
        // Телепортация
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(destination);
            
            // Эффекты после телепортации
            Location newLoc = player.getLocation();
            for (int i = 0; i < 20; i++) {
                double angle = Math.random() * Math.PI * 2;
                double radius = Math.random() * 1.5;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                double y = Math.random() * 2;
                
                Location particleLoc = newLoc.clone().add(x, y, z);
                newLoc.getWorld().spawnParticle(Particle.TOTEM, particleLoc, 1, 0, 0, 0, 0);
            }
            
            player.playSound(newLoc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
            player.sendMessage(ChatUtils.colorize("&aTелепортация завершена!"));
            
        }, 20L);
        
        if (data == null) {
            data = new TeleportData();
            playerData.put(player.getUniqueId(), data);
        }
        data.lastTeleport = System.currentTimeMillis();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() == Material.ENDER_PEARL && item.hasItemMeta() && 
            item.getItemMeta().getDisplayName().contains("Портальная сфера")) {
            
            event.setCancelled(true);
            
            if (!player.hasPermission("ultimateaddons.teleport.create")) {
                player.sendMessage(ChatUtils.colorize("&cУ вас нет прав для создания порталов!"));
                return;
            }
            
            Location loc = player.getLocation();
            createPortal(loc, null);
            player.sendMessage(ChatUtils.colorize("&aПортал создан! Он исчезнет через некоторое время."));
            
            // Убираем предмет
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        }
    }

    public void createPortal(Location location, Location destination) {
        PortalData portal = new PortalData();
        portal.destination = destination;
        portal.tick = 0;
        
        activePortals.put(location.clone(), portal);
        
        // Звук создания портала
        location.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 1.0f);
        
        // Эффект создания
        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 3;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.random() * 4;
            
            Location particleLoc = location.clone().add(x, y, z);
            location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    public ItemStack createPortalSphere() {
        return new dev.c0rex64.ultimateaddons.utils.ItemBuilder(Material.ENDER_PEARL)
            .setName("&5Портальная сфера")
            .setLore(
                "&7Создает временный портал",
                "&7в месте использования.",
                "&e",
                "&eПКМ - Создать портал"
            )
            .build();
    }

    private static class TeleportData {
        long lastTeleport = 0;
    }

    private static class PortalData {
        Location destination;
        int tick = 0;
    }
} 