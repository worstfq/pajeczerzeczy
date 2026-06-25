package me.beniomazi.spiderhunt.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Tworzy przedmioty pluginu i identyfikuje je wylacznie po PersistentDataContainer,
 * niezaleznie od nazwy czy lore.
 */
public class ItemFactory {

    private static final byte MARK = (byte) 1;

    private static final TextColor SWORD_NAME_COLOR = TextColor.color(0x6A0DAD);
    private static final TextColor TRAP_NAME_COLOR = TextColor.color(0x4B0082);

    private final NamespacedKey swordKey;
    private final NamespacedKey trapKey;

    public ItemFactory(Plugin plugin) {
        this.swordKey = new NamespacedKey(plugin, "spider_sword");
        this.trapKey = new NamespacedKey(plugin, "spider_trap");
    }

    /** Tworzy Kiel Arachny (miecz z netherytu). */
    public ItemStack createSword() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Kiel Arachny", SWORD_NAME_COLOR)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(line("Wykuty z jadu i pradawnego cienia,", NamedTextColor.GRAY));
        lore.add(line("ostrze tnace nici przeznaczenia.", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(line("Prawy przycisk myszy:", NamedTextColor.DARK_PURPLE));
        lore.add(line("zastaw pajecza pulapke 3x3.", NamedTextColor.LIGHT_PURPLE));
        lore.add(Component.empty());
        lore.add(quote("\"Kazdy low zaczyna sie od jednej nici.\""));
        meta.lore(lore);

        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(swordKey, PersistentDataType.BYTE, MARK);

        item.setItemMeta(meta);
        return item;
    }

    /** Tworzy Siec Usidlenia (jednorazowy aktywator) w podanej ilosci. */
    public ItemStack createTrap(int amount) {
        ItemStack item = new ItemStack(Material.BLACK_DYE, clampStack(amount));
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Siec Usidlenia", TRAP_NAME_COLOR)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(line("Splatany klab lepkiej przedzy,", NamedTextColor.GRAY));
        lore.add(line("gotow pochlonac nieostrozna zwierzyne.", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(line("Prawy przycisk myszy:", NamedTextColor.DARK_PURPLE));
        lore.add(line("rozstaw pulapke 3x3 (zuzywa 1 szt.).", NamedTextColor.LIGHT_PURPLE));
        lore.add(Component.empty());
        lore.add(quote("\"Cierpliwosc przedzy zawsze zostaje nagrodzona.\""));
        meta.lore(lore);

        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(trapKey, PersistentDataType.BYTE, MARK);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isSword(ItemStack item) {
        return hasMark(item, swordKey);
    }

    public boolean isTrap(ItemStack item) {
        return hasMark(item, trapKey);
    }

    private boolean hasMark(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.BYTE);
    }

    private Component line(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    private Component quote(String text) {
        return Component.text(text, NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true);
    }

    private int clampStack(int amount) {
        if (amount < 1) {
            return 1;
        }
        return Math.min(amount, 64);
    }
}
