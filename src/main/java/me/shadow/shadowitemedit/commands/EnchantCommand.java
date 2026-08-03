package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantCommand extends SubCommand {

    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String getDescription() {
        return "Manage enchantments on an item.";
    }

    @Override
    public String getSyntax() {
        return "/ie enchant <add|remove|clear> [enchant] [level]";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.enchant";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: " + getSyntax() + "</red>"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must hold an item.</red>"));
            return;
        }

        ItemBuilder builder = new ItemBuilder(item);
        String action = args[1].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 4) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie enchant add <enchant> <level></red>"));
                    return;
                }
                Enchantment enchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(args[2].toLowerCase()));
                if (enchant == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid enchantment.</red>"));
                    return;
                }
                try {
                    int level = Integer.parseInt(args[3]);
                    builder.addEnchant(enchant, level);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantment added.</green>"));
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid level.</red>"));
                }
                break;
            case "remove":
                if (args.length < 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie enchant remove <enchant></red>"));
                    return;
                }
                Enchantment enchantToRemove = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(args[2].toLowerCase()));
                if (enchantToRemove == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid enchantment.</red>"));
                    return;
                }
                builder.removeEnchant(enchantToRemove);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantment removed.</green>"));
                break;
            case "clear":
                builder.clearEnchants();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantments cleared.</green>"));
                break;
            default:
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown enchant action. Use add, remove, clear.</red>"));
                return;
        }

        player.getInventory().setItemInMainHand(builder.build());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            results.add("add");
            results.add("remove");
            results.add("clear");
            return results;
        } else if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            for (Enchantment enchant : Registry.ENCHANTMENT) {
                results.add(enchant.getKey().getKey());
            }
            return results;
        } else if (args.length == 4 && args[1].equalsIgnoreCase("add")) {
            results.add("<level>");
            return results;
        }
        return results;
    }
}
