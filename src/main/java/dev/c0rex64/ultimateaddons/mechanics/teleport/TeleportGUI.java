package dev.c0rex64.ultimateaddons.mechanics.teleport;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import dev.c0rex64.ultimateaddons.utils.ChatUtils;
import org.bukkit.entity.Player;

public class TeleportGUI {
    private final UltimateAddons plugin;
    private final TeleportMechanic mechanic;
    private final Player player;

    public TeleportGUI(UltimateAddons plugin, TeleportMechanic mechanic, Player player) {
        this.plugin = plugin;
        this.mechanic = mechanic;
        this.player = player;
    }

    public void open() {
        player.sendMessage(ChatUtils.colorize("&cGUI для телепортации пока не реализован."));
        player.sendMessage(ChatUtils.colorize("&7Используйте портальную сферу для создания порталов."));
    }
} 