package me.beniomazi.spiderhunt.commands;

import me.beniomazi.spiderhunt.items.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Obsluga komendy: /spidersword give &lt;gracz&gt;
 */
public class SpiderSwordCommand implements CommandExecutor, TabCompleter {

    private final ItemFactory items;

    public SpiderSwordCommand(ItemFactory items) {
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("Uzycie: /spidersword give <gracz>", NamedTextColor.YELLOW));
            return true;
        }
        if (!sender.hasPermission("spidersword.give")) {
            sender.sendMessage(Component.text("Nie masz uprawnien do tej komendy.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Component.text("Podaj gracza: /spidersword give <gracz>", NamedTextColor.YELLOW));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Gracz '" + args[1] + "' jest offline lub nie istnieje.", NamedTextColor.RED));
            return true;
        }

        target.getInventory().addItem(items.createSword());
        sender.sendMessage(Component.text("Wreczono Kiel Arachny graczowi " + target.getName() + ".", NamedTextColor.GREEN));
        target.sendMessage(Component.text("Otrzymales Kiel Arachny!", NamedTextColor.LIGHT_PURPLE));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if ("give".startsWith(args[0].toLowerCase())) {
                suggestions.add("give");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            String prefix = args[1].toLowerCase();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(prefix)) {
                    suggestions.add(online.getName());
                }
            }
        }
        return suggestions;
    }
}
