package me.beniomazi.spiderhunt.managers;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Odpowiada za tworzenie pajeczej pulapki 3x3 z pajeczyn (COBWEB)
 * na warstwie tuz nad blokiem, na ktory patrzy gracz.
 */
public class TrapManager {

    private static final int RADIUS = 1; // promien 1 => obszar 3x3

    /** Wynik szczegolny: gracz nie patrzy na zaden blok w zasiegu. */
    public static final int RESULT_NO_TARGET = -1;

    /**
     * Tworzy pulapke 3x3.
     *
     * @param player   gracz uzywajacy przedmiotu
     * @param maxRange maksymalny zasieg w blokach
     * @return RESULT_NO_TARGET gdy brak bloku w zasiegu, 0 gdy brak miejsca,
     *         lub liczbe postawionych pajeczyn (&gt;0) przy sukcesie
     */
    public int createTrap(Player player, int maxRange) {
        Block target = player.getTargetBlockExact(maxRange);
        if (target == null) {
            return RESULT_NO_TARGET;
        }

        World world = target.getWorld();
        int baseX = target.getX();
        int layerY = target.getY() + 1; // warstwa nad wskazanym blokiem
        int baseZ = target.getZ();

        int placed = 0;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                Block block = world.getBlockAt(baseX + dx, layerY, baseZ + dz);
                if (isAir(block)) {
                    block.setType(Material.COBWEB, false);
                    placed++;
                }
            }
        }

        if (placed > 0) {
            playEffects(world, baseX, layerY, baseZ);
        }
        return placed;
    }

    /** Pajeczyna moze powstac wylacznie w miejscu powietrza. */
    private boolean isAir(Block block) {
        Material type = block.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }

    private void playEffects(World world, int x, int y, int z) {
        var center = new org.bukkit.Location(world, x + 0.5, y + 0.5, z + 0.5);
        world.playSound(center, Sound.ENTITY_SPIDER_STEP, 1.0f, 0.7f);
        world.playSound(center, Sound.BLOCK_WOOL_PLACE, 1.0f, 0.8f);
        world.spawnParticle(Particle.CLOUD, center, 25, 1.0, 0.3, 1.0, 0.01);
        world.spawnParticle(Particle.CRIT, center, 20, 1.0, 0.3, 1.0, 0.05);
    }
}
