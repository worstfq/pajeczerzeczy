package me.beniomazi.spiderhunt;

import me.beniomazi.spiderhunt.commands.SpiderSwordCommand;
import me.beniomazi.spiderhunt.commands.SpiderTrapCommand;
import me.beniomazi.spiderhunt.items.ItemFactory;
import me.beniomazi.spiderhunt.listeners.ItemUseListener;
import me.beniomazi.spiderhunt.managers.CooldownManager;
import me.beniomazi.spiderhunt.managers.TrapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Glowna klasa pluginu SpiderHunt.
 * Dodaje dwa tematyczne przedmioty: Kiel Arachny (miecz) oraz Siec Usidlenia (rzucana mikstura).
 */
public final class SpiderHuntPlugin extends JavaPlugin {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final String DEFAULT_PREFIX =
            "<dark_gray>[</dark_gray><#6A0DAD><bold>Lowy</bold></#6A0DAD><dark_gray>]</dark_gray> ";

    private ItemFactory itemFactory;
    private CooldownManager cooldownManager;
    private TrapManager trapManager;

    @Override
    public void onEnable() {
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

    /** Prefix wiadomosci, czytany dynamicznie z configu i parsowany przez MiniMessage. */
    public Component prefix() {
        String raw = getConfig().getString("messages.prefix", DEFAULT_PREFIX);
        return MINI.deserialize(raw);
    }

    /** Cooldown miecza (sekundy) czytany dynamicznie z configu. */
    public int getSwordCooldownSeconds() {
        return Math.max(0, getConfig().getInt("spider_sword.cooldown_seconds", 10));
    }

    /** Cooldown mikstury (sekundy) czytany dynamicznie z configu. */
    public int getTrapCooldownSeconds() {
        return Math.max(0, getConfig().getInt("spider_trap.cooldown_seconds", 15));
    }
}
