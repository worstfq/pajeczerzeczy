package me.beniomazi.spiderhunt.listeners;

import me.beniomazi.spiderhunt.SpiderHuntPlugin;
import me.beniomazi.spiderhunt.items.ItemFactory;
import me.beniomazi.spiderhunt.managers.CooldownManager;
import me.beniomazi.spiderhunt.managers.TrapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Obsluga uzycia obu przedmiotow:
 *  - Kiel Arachny: PPM -> pulapka na bloku, na ktory patrzy gracz,
 *  - Siec Usidlenia: PPM -> rzut nieskonczona mikstura; pulapka powstaje tam, gdzie wyladuje.
 * Oba przedmioty maja osobne cooldowny (logiczny + wizualny "jak tarcza" na hotbarze).
 */
public class ItemUseListener implements Listener {

    private static final int SWORD_RANGE = 5;
    private static final String TYPE_SWORD = "spider_sword";
    private static final String TYPE_TRAP = "spider_trap";

    private final SpiderHuntPlugin plugin;
    private final ItemFactory items;
    private final CooldownManager cooldowns;
    private final TrapManager traps;

    public ItemUseListener(SpiderHuntPlugin plugin, ItemFactory items,
                           CooldownManager cooldowns, TrapManager traps) {
        this.plugin = plugin;
        this.items = items;
        this.cooldowns = cooldowns;
        this.traps = traps;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Player player = event.getPlayer();

        if (items.isSword(item)) {
            event.setCancelled(true);
            handleSword(player);
        } else if (items.isTrap(item)) {
            // Zawsze anulujemy, by mikstura sie nie zuzyla (jest nieskonczona).
            event.setCancelled(true);
            handleTrapThrow(player);
        }
    }

    private void handleSword(Player player) {
        long remaining = cooldowns.getRemainingMillis(TYPE_SWORD, player.getUniqueId());
        if (remaining > 0L) {
            sendCooldown(player, remaining);
            return;
        }

        int result = traps.createTrapFromSight(player, SWORD_RANGE);
        if (result == TrapManager.RESULT_NO_TARGET) {
            player.sendMessage(plugin.prefix().append(
                    Component.text("Nie patrzysz na zaden blok w zasiegu (max 5 blokow).", NamedTextColor.RED)));
            return;
        }
        if (result == 0) {
            player.sendMessage(plugin.prefix().append(
                    Component.text("Brak wolnego miejsca, by rozpiac pajeczyne.", NamedTextColor.RED)));
            return;
        }

        int cooldown = plugin.getSwordCooldownSeconds();
        cooldowns.apply(TYPE_SWORD, player.getUniqueId(), cooldown);
        applyVisualCooldown(player, Material.NETHERITE_SWORD, cooldown);

        player.sendMessage(plugin.prefix().append(
                Component.text("Pajecza pulapka zostala zastawiona!", NamedTextColor.LIGHT_PURPLE)));
    }

    private void handleTrapThrow(Player player) {
        long remaining = cooldowns.getRemainingMillis(TYPE_TRAP, player.getUniqueId());
        if (remaining > 0L) {
            sendCooldown(player, remaining);
            return;
        }

        // Rzut wlasnym pociskiem - przedmiot w rece nie jest zuzywany.
        ThrownPotion projectile = player.launchProjectile(ThrownPotion.class);
        projectile.setItem(items.createTrap());
        projectile.getPersistentDataContainer().set(items.trapKey(), PersistentDataType.BYTE, (byte) 1);

        int cooldown = plugin.getTrapCooldownSeconds();
        cooldowns.apply(TYPE_TRAP, player.getUniqueId(), cooldown);
        applyVisualCooldown(player, Material.SPLASH_POTION, cooldown);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (!potion.getPersistentDataContainer().has(items.trapKey(), PersistentDataType.BYTE)) {
            return;
        }

        // Nasza mikstura nie nadaje zadnych efektow - tylko zastawia pulapke.
        event.setCancelled(true);

        int placed = traps.createTrapAt(potion.getLocation());

        ProjectileSource shooter = potion.getShooter();
        if (shooter instanceof Player thrower) {
            if (placed > 0) {
                thrower.sendMessage(plugin.prefix().append(
                        Component.text("Pajecza pulapka zostala zastawiona!", NamedTextColor.LIGHT_PURPLE)));
            } else {
                thrower.sendMessage(plugin.prefix().append(
                        Component.text("Pajeczyna nie znalazla miejsca, by sie rozpiac.", NamedTextColor.RED)));
            }
        }
    }

    /** Wizualny cooldown na itemie w hotbarze (biale przesuniecie - jak przy tarczy). */
    private void applyVisualCooldown(Player player, Material material, int seconds) {
        if (seconds <= 0) {
            return;
        }
        player.setCooldown(material, seconds * 20);
    }

    private void sendCooldown(Player player, long remainingMillis) {
        long secondsLeft = (long) Math.ceil(remainingMillis / 1000.0);
        player.sendMessage(plugin.prefix()
                .append(Component.text("Odczekaj jeszcze ", NamedTextColor.RED))
                .append(Component.text(secondsLeft + "s", NamedTextColor.YELLOW))
                .append(Component.text(" przed kolejnym uzyciem.", NamedTextColor.RED)));
    }
}
