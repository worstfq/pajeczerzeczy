package me.beniomazi.spiderhunt.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zarzadza niezaleznymi cooldownami dla kazdego typu przedmiotu i kazdego gracza osobno.
 * Typ to dowolny identyfikator (np. "spider_sword", "spider_trap").
 */
public class CooldownManager {

    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    /** Zwraca pozostaly czas cooldownu w milisekundach (0 = gotowe). */
    public long getRemainingMillis(String type, UUID player) {
        Map<UUID, Long> map = cooldowns.get(type);
        if (map == null) {
            return 0L;
        }
        Long readyAt = map.get(player);
        if (readyAt == null) {
            return 0L;
        }
        return Math.max(0L, readyAt - System.currentTimeMillis());
    }

    /** Uruchamia cooldown na podana liczbe sekund. */
    public void apply(String type, UUID player, int seconds) {
        if (seconds <= 0) {
            return;
        }
        cooldowns.computeIfAbsent(type, k -> new HashMap<>())
                .put(player, System.currentTimeMillis() + (seconds * 1000L));
    }
}
