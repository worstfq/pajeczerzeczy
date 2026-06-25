package me.beniomazi.spiderhunt;

import me.beniomazi.spiderhunt.commands.SpiderSwordCommand;
import me.beniomazi.spiderhunt.commands.SpiderTrapCommand;
import me.beniomazi.spiderhunt.items.ItemFactory;
import me.beniomazi.spiderhunt.listeners.ItemUseListener;
import me.beniomazi.spiderhunt.managers.CooldownManager;
import me.beniomazi.spiderhunt.managers.TrapManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Glowna klasa pluginu SpiderHunt.
 * Dodaje dwa tematyczne przedmioty: Kiel Arachny (miecz) oraz Siec Usidlenia (pulapka),
 * ktore zastawiaja pajecza pulapke 3x3 na bloku, na ktory patrzy gracz.
 */
public final class SpiderHuntPlugin extends JavaPlugin {

    private ItemFactory itemFactory;
    private CooldownManager cooldownManager;
    private TrapManager trapManager;

    @Override
    public void onEnable() {
        // Tworzy config.yml z zasobow, jesli jeszcze nie istnieje.
        saveDefaultConfig();

        this.itemFactory = new ItemFactory(this);
        this.cooldownManager = new CooldownManager();
        this.trapManager = new TrapManager();

        getServer().getPluginManager().registerEvents(
                new ItemUseListener(this, itemFactory, cooldownManager, trapManager), this);

        registerCommand("spidersword", new SpiderSwordCommand(itemFactory));
        registerCommand("spidertrap", new SpiderTrapCommand(itemFactory));

        getLogger().info("SpiderHunt wlaczony. Lowy czas zaczac!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpiderHunt wylaczony.");
    }

    private <T extends org.bukkit.command.CommandExecutor & org.bukkit.command.TabCompleter>
    void registerCommand(String name, T handler) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().severe("Nie znaleziono komendy '" + name + "' w plugin.yml!");
            return;
        }
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    /** Cooldown miecza (sekundy) czytany dynamicznie z configu. */
    public int getSwordCooldownSeconds() {
        return Math.max(0, getConfig().getInt("spider_sword.cooldown_seconds", 10));
    }

    /** Cooldown pulapki (sekundy) czytany dynamicznie z configu. */
    public int getTrapCooldownSeconds() {
        return Math.max(0, getConfig().getInt("spider_trap.cooldown_seconds", 15));
    }
}
