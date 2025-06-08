package dev.c0rex64.ultimateaddons.managers;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.mechanics.Mechanic;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MechanicsGUI implements Listener, InventoryHolder {

    private final UltimateAddons plugin;
    private final Inventory inventory;
    private final Map<Integer, Mechanic> slotToMechanic = new HashMap<>();

    public MechanicsGUI(UltimateAddons plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, ChatUtils.colorize("&5&lUltimate Addons"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        updateItems();
        player.openInventory(inventory);
    }

    private void updateItems() {
        inventory.clear();
        slotToMechanic.clear();

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
        }

        int slot = 10;
        for (Mechanic mechanic : plugin.getMechanicManager().getAllMechanics()) {
            if (slot > 43) break; 
            
            boolean isEnabled = mechanic.isEnabled();
            Material icon = isEnabled ? Material.LIME_DYE : Material.GRAY_DYE;
            
            List<String> lore = new ArrayList<>();
            lore.add("&7" + mechanic.getDescription());
            lore.add(" ");
            lore.add("&eСтатус: " + (isEnabled ? "&aВключено" : "&cВыключено"));
            lore.add(" ");
            lore.add("&bЛКМ - Настройки");
            lore.add("&bПКМ - " + (isEnabled ? "Выключить" : "Включить"));

            ItemBuilder item = new ItemBuilder(icon)
                .setName("&6&l" + mechanic.getName())
                .setLore(lore);
            
            inventory.setItem(slot, item.build());
            slotToMechanic.put(slot, mechanic);

            slot++;
            if ((slot - 9) % 9 == 0) { 
                slot += 2;
            }
        }
        
        inventory.setItem(49, new ItemBuilder(Material.BARRIER).setName("&cЗакрыть").build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();

        if (clickedSlot == 49) {
            player.closeInventory();
            return;
        }

        Mechanic mechanic = slotToMechanic.get(clickedSlot);
        if (mechanic != null) {
            if (event.isLeftClick()) {
                player.closeInventory();
                mechanic.openSettingsGUI(player);
            } else if (event.isRightClick()) {
                if (mechanic.isEnabled()) {
                    mechanic.disable();
                    player.sendMessage(ChatUtils.colorize("&cМеханика '" + mechanic.getName() + "' выключена."));
                } else {
                    mechanic.enable();
                    player.sendMessage(ChatUtils.colorize("&aМеханика '" + mechanic.getName() + "' включена."));
                }
                updateItems(); 
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
} 