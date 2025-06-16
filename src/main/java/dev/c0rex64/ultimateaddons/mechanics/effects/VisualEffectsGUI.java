package dev.c0rex64.ultimateaddons.mechanics.effects;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import dev.c0rex64.ultimateaddons.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class VisualEffectsGUI implements Listener, InventoryHolder {

    private final UltimateAddons plugin;
    private final VisualEffectsMechanic mechanic;
    private final Player player;
    private final Inventory inventory;

    public VisualEffectsGUI(UltimateAddons plugin, VisualEffectsMechanic mechanic, Player player) {
        this.plugin = plugin;
        this.mechanic = mechanic;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, ChatUtils.colorize("&5&lВизуальные эффекты"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        updateItems();
        player.openInventory(inventory);
    }

    private void updateItems() {
        inventory.clear();

        // Фон
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
        }

        // Эффекты
        inventory.setItem(10, new ItemBuilder(Material.BLAZE_POWDER)
            .setName("&6Спираль")
            .setLore("&7Красивая вращающаяся спираль", "&eНажмите для выбора")
            .build());

        inventory.setItem(11, new ItemBuilder(Material.GOLD_NUGGET)
            .setName("&6Кольцо")
            .setLore("&7Пульсирующее кольцо частиц", "&eНажмите для выбора")
            .build());

        inventory.setItem(12, new ItemBuilder(Material.ENDER_PEARL)
            .setName("&6Сфера")
            .setLore("&7Сфера из случайных частиц", "&eНажмите для выбора")
            .build());

        inventory.setItem(13, new ItemBuilder(Material.NAUTILUS_SHELL)
            .setName("&6Спираль ДНК")
            .setLore("&7Двойная спираль как ДНК", "&eНажмите для выбора")
            .build());

        inventory.setItem(14, new ItemBuilder(Material.FEATHER)
            .setName("&6Крылья")
            .setLore("&7Машущие крылья ангела", "&eНажмите для выбора")
            .build());

        inventory.setItem(15, new ItemBuilder(Material.NETHER_STAR)
            .setName("&6Аура")
            .setLore("&7Мистическая аура вокруг игрока", "&eНажмите для выбора")
            .build());

        // Цвета
        int[] colorSlots = {28, 29, 30, 31, 32, 33, 34, 35};
        VisualEffectsMechanic.ParticleColor[] colors = VisualEffectsMechanic.ParticleColor.values();
        Material[] colorMaterials = {
            Material.RED_DYE, Material.GREEN_DYE, Material.BLUE_DYE, Material.YELLOW_DYE,
            Material.PURPLE_DYE, Material.CYAN_DYE, Material.WHITE_DYE, Material.ORANGE_DYE
        };

        for (int i = 0; i < Math.min(colorSlots.length, colors.length); i++) {
            inventory.setItem(colorSlots[i], new ItemBuilder(colorMaterials[i])
                .setName("&f" + colors[i].name())
                .setLore("&7Цвет частиц", "&eНажмите для выбора")
                .build());
        }

        // Управление
        inventory.setItem(45, new ItemBuilder(Material.BARRIER)
            .setName("&cОтключить эффект")
            .setLore("&7Убрать все эффекты")
            .build());

        inventory.setItem(53, new ItemBuilder(Material.ARROW)
            .setName("&aЗакрыть")
            .build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        Player clicker = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        clicker.playSound(clicker.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

        // Эффекты
        switch (slot) {
            case 10 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.SPIRAL, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.BLUE);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Спираль"));
            }
            case 11 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.RING, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.GREEN);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Кольцо"));
            }
            case 12 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.SPHERE, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.PURPLE);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Сфера"));
            }
            case 13 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.HELIX, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.CYAN);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Спираль ДНК"));
            }
            case 14 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.WINGS, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.WHITE);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Крылья"));
            }
            case 15 -> {
                mechanic.setPlayerEffect(clicker, VisualEffectsMechanic.EffectType.AURA, 
                    Particle.REDSTONE, VisualEffectsMechanic.ParticleColor.YELLOW);
                clicker.sendMessage(ChatUtils.colorize("&aВыбран эффект: &6Аура"));
            }
            case 45 -> {
                mechanic.removePlayerEffect(clicker);
                clicker.sendMessage(ChatUtils.colorize("&cВсе эффекты отключены"));
            }
            case 53 -> clicker.closeInventory();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
} 