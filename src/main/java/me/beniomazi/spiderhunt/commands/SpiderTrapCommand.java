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
 * Obsluga komendy: /spidertrap give &lt;gracz&gt; [ilosc]
 * Kazda Siec Usidlenia jest nieskonczona; [ilosc] okresla liczbe wydanych sztuk.
 */
public class SpiderTrapCommand implements CommandExecutor, TabCompleter {

    private final ItemFactory items;

    public SpiderTrapCommand(ItemFactory items) {
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("Uzycie: /spidertrap give <gracz> [ilosc]", NamedTextColor.YELLOW));
            return true;
        }
        if (!sender.hasPermission("spidertrap.give")) {
            sender.sendMessage(Component.text("Nie masz uprawnien do tej komendy.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Component.text("Podaj gracza: /spidertrap give <gracz> [ilosc]", NamedTextColor.YELLOW));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Gracz '" + args[1] + "' jest offline lub nie istnieje.", NamedTextColor.RED));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("'" + args[2] + "' nie jest poprawna liczba.", NamedTextColor.RED));
                return true;
            }
            if (amount < 1) {
                amount = 1;
            }
            if (amount > 64) {
                amount = 64;
            }
        }

        // Mikstury sie nie stackuja - kazda sztuka trafia do osobnego slotu.
        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(items.createTrap());
        }

        sender.sendMessage(Component.text("Wreczono Siec Usidlenia (" + amount + " szt.) graczowi "
                + target.getName() + ".", NamedTextColor.GREEN));
        target.sendMessage(Component.text("Otrzymales Siec Usidlenia x" + amount + "!", NamedTextColor.LIGHT_PURPLE));
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
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            suggestions.add("1");
            suggestions.add("4");
            suggestions.add("8");
        }
        return suggestions;
    }
}
