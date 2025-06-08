package dev.c0rex64.ultimateaddons.commands;

import dev.c0rex64.ultimateaddons.UltimateAddons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {
    
    private final UltimateAddons plugin;
    
    public MainCommand(UltimateAddons plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("ultimateaddons.admin")) {
            sender.sendMessage(Component.text("У вас нет прав для использования этой команды!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Эта команда доступна только игрокам!", NamedTextColor.RED));
                return true;
            }
            plugin.getMechanicManager().openMainGUI(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigManager().reloadConfigs();
                plugin.getMechanicManager().reloadAll();
                sender.sendMessage(Component.text("UltimateAddons перезагружен!", NamedTextColor.GREEN));
            }
            case "help" -> {
                sender.sendMessage(Component.text("=== UltimateAddons Помощь ===", NamedTextColor.GOLD));
                sender.sendMessage(Component.text("/" + label + " - Открыть меню", NamedTextColor.YELLOW));
                sender.sendMessage(Component.text("/" + label + " reload - Перезагрузить конфиг", NamedTextColor.YELLOW));
                sender.sendMessage(Component.text("/" + label + " help - Показать помощь", NamedTextColor.YELLOW));
            }
            default -> sender.sendMessage(Component.text("Неизвестная команда. Используйте /" + label + " help", NamedTextColor.RED));
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                    @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("ultimateaddons.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("reload", "help")
                .stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
} 