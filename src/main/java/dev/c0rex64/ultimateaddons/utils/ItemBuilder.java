package dev.c0rex64.ultimateaddons.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }
    
    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(ChatUtils.colorize(name));
        return this;
    }
    
    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.stream(lore).map(ChatUtils::colorize).toList());
        return this;
    }
    
    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore.stream().map(ChatUtils::colorize).toList());
        return this;
    }
    
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }
    
    public ItemBuilder flag(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }
    
    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }
    
    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
    
    public ItemBuilder glow() {
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }
    
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
} 