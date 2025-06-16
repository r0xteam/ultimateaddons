package dev.c0rex64.ultimateaddons.utils;

import org.bukkit.ChatColor;
 
public class ChatUtils {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
} 