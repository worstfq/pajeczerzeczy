package me.beniomazi.spiderhunt.listeners;

import me.beniomazi.spiderhunt.SpiderHuntPlugin;
import me.beniomazi.spiderhunt.items.ItemFactory;
import me.beniomazi.spiderhunt.managers.CooldownManager;
import me.beniomazi.spiderhunt.managers.TrapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Obsluguje uzycie obu przedmiotow prawym przyciskiem myszy:
 * sprawdza cooldown, tworzy pulapke, odtwarza komunikaty i zuzywa pulapke.
 */
public class ItemUseListener implements Listener {

    private static final int MAX_RANGE = 5;

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
        // Tylko reka glowna, aby uniknac podwojnego wyzwolenia.
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

        boolean isSword = items.isSword(item);
        boolean isTrap = items.isTrap(item);
        if (!isSword && !isTrap) {
            return;
        }

        // Blokujemy domyslne zachowanie (np. barwienie tabliczki czarnym barwnikiem).
        event.setCancelled(true);

        Player player = event.getPlayer();
        String type = isSword ? "spider_sword" : "spider_trap";
        int cooldownSeconds = isSword
                ? plugin.getSwordCooldownSeconds()
                : plugin.getTrapCooldownSeconds();

        long remaining = cooldowns.getRemainingMillis(type, player.getUniqueId());
        if (remaining > 0L) {
            long secondsLeft = (long) Math.ceil(remaining / 1000.0);
            player.sendMessage(prefix()
                    .append(Component.text("Odczekaj jeszcze ", NamedTextColor.RED))
                    .append(Component.text(secondsLeft + "s", NamedTextColor.YELLOW))
                    .append(Component.text(" przed kolejnym uzyciem.", NamedTextColor.RED)));
            return;
        }

        int result = traps.createTrap(player, MAX_RANGE);

        if (result == TrapManager.RESULT_NO_TARGET) {
            player.sendMessage(prefix().append(
                    Component.text("Nie patrzysz na zaden blok w zasiegu (max 5 blokow).", NamedTextColor.RED)));
            return;
        }
        if (result == 0) {
            player.sendMessage(prefix().append(
                    Component.text("Brak wolnego miejsca, by rozpiac pajeczyne.", NamedTextColor.RED)));
            return;
        }

        // Sukces: cooldown + ewentualne zuzycie.
        cooldowns.apply(type, player.getUniqueId(), cooldownSeconds);

        if (isTrap) {
            consumeOne(player);
        }

        player.sendMessage(prefix().append(
                Component.text("Pajecza pulapka zostala zastawiona!", NamedTextColor.LIGHT_PURPLE)));
    }

    /** Zuzywa dokladnie 1 sztuke przedmiotu z reki glownej. */
    private void consumeOne(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        int newAmount = hand.getAmount() - 1;
        if (newAmount <= 0) {
            player.getInventory().setItemInMainHand(null);
        } else {
            hand.setAmount(newAmount);
        }
    }

    private Component prefix() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("Lowy", TextColor.color(0x6A0DAD)).decoration(TextDecoration.BOLD, true))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY));
    }
}
