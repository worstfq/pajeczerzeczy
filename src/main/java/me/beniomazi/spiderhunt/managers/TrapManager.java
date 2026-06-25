package me.beniomazi.spiderhunt.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Tworzy pajecza pulapke 3x3 z pajeczyn (COBWEB).
 *  - z miecza: na warstwie nad blokiem, na ktory patrzy gracz,
 *  - z mikstury: w miejscu, gdzie wyladowala.
 */
public class TrapManager {

    private static final int RADIUS = 1; // promien 1 => obszar 3x3

    /** Wynik szczegolny: gracz nie patrzy na zaden blok w zasiegu. */
    public static final int RESULT_NO_TARGET = -1;

    /**
     * Pulapka z miecza: na bloku, na ktory patrzy gracz.
     *
     * @return RESULT_NO_TARGET gdy brak bloku w zasiegu, 0 gdy brak miejsca,
     *         lub liczbe postawionych pajeczyn (&gt;0)
     */
    public int createTrapFromSight(Player player, int maxRange) {
        Block target = player.getTargetBlockExact(maxRange);
        if (target == null) {
            return RESULT_NO_TARGET;
        }
        return placeGrid(target.getWorld(), target.getX(), target.getY() + 1, target.getZ());
    }

    /**
     * Pulapka z rzuconej mikstury: w miejscu jej rozbicia.
     *
     * @return 0 gdy brak miejsca, lub liczbe postawionych pajeczyn (&gt;0)
     */
    public int createTrapAt(Location location) {
        Block block = location.getBlock();
        int centerY = isAir(block) ? block.getY() : block.getY() + 1;
        return placeGrid(block.getWorld(), block.getX(), centerY, block.getZ());
    }

    private int placeGrid(World world, int cx, int cy, int cz) {
        int placed = 0;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                Block block = world.getBlockAt(cx + dx, cy, cz + dz);
                if (isAir(block)) {
                    block.setType(Material.COBWEB, false);
                    placed++;
                }
            }
        }
        if (placed > 0) {
            playEffects(world, cx, cy, cz);
        }
        return placed;
    }

    /** Pajeczyna moze powstac wylacznie w miejscu powietrza. */
    private boolean isAir(Block block) {
        Material type = block.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }

    private void playEffects(World world, int x, int y, int z) {
        Location center = new Location(world, x + 0.5, y + 0.5, z + 0.5);
        world.playSound(center, Sound.ENTITY_SPIDER_STEP, 1.0f, 0.7f);
        world.playSound(center, Sound.BLOCK_WOOL_PLACE, 1.0f, 0.8f);
        world.spawnParticle(Particle.CLOUD, center, 25, 1.0, 0.3, 1.0, 0.01);
        world.spawnParticle(Particle.CRIT, center, 20, 1.0, 0.3, 1.0, 0.05);
    }
}
