package dev.c0rex64.ultimateaddons.mechanics.weather;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import dev.c0rex64.ultimateaddons.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherSettingsGUI implements Listener, InventoryHolder {
    private final WeatherMechanic weatherMechanic;
    private final Player player;
    private final Inventory inventory;

    public WeatherSettingsGUI(UltimateAddons plugin, WeatherMechanic weatherMechanic, Player player) {
        this.weatherMechanic = weatherMechanic;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, "Настройки погоды");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        updateItems();
        player.openInventory(inventory);
    }

    private void updateItems() {
        inventory.clear();

        boolean isEnabled = weatherMechanic.isEnabled();
        ItemBuilder toggleButton = new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
            .setName(isEnabled ? "&aПогода включена" : "&cПогода выключена")
            .setLore(
                "&7Нажмите, чтобы " + (isEnabled ? "выключить" : "включить"),
                "&7механику погоды."
            );

        ItemBuilder infoItem = new ItemBuilder(Material.PAPER)
            .setName("&eТекущая погода")
            .setLore(
                "&7Тип: &f" + weatherMechanic.getCurrentWeather().name(),
                "&7Сила ветра: &f" + String.format("%.2f", weatherMechanic.getWindStrength()),
                "&7Направление ветра: &f" + String.format("%.1f", weatherMechanic.getWindDirection()) + "°",
                "&7Температура: &f" + String.format("%.1f", weatherMechanic.getTemperature()) + "°C",
                "&7Влажность: &f" + String.format("%.1f", weatherMechanic.getHumidity()) + "%"
            );

        inventory.setItem(11, toggleButton.build());
        inventory.setItem(15, infoItem.build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }
        
        event.setCancelled(true);
        Player clickedPlayer = (Player) event.getWhoClicked();

        if (event.getSlot() == 11) {
            if (weatherMechanic.isEnabled()) {
                weatherMechanic.disable();
                clickedPlayer.sendMessage(ChatUtils.colorize("&cМеханика погоды выключена."));
            } else {
                weatherMechanic.enable();
                clickedPlayer.sendMessage(ChatUtils.colorize("&aМеханика погоды включена."));
            }
            updateItems();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
} 