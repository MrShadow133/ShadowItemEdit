package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoreCommand extends SubCommand {

    @Override
    public String getName() {
        return "lore";
    }

    @Override
    public String getDescription() {
        return "Edits the lore of the held item.";
    }

    @Override
    public String getSyntax() {
        return "/ie lore <add|insert|set|remove|clear> [args]";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.lore";
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
                if (args.length < 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie lore add <text></red>"));
                    return;
                }
                builder.addLore(buildString(args, 2));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Lore line added.</green>"));
                break;
            case "insert":
                if (args.length < 4) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie lore insert <line> <text></red>"));
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]) - 1;
                    builder.insertLore(line, buildString(args, 3));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Lore line inserted.</green>"));
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid line number.</red>"));
                }
                break;
            case "set":
                if (args.length < 4) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie lore set <line> <text></red>"));
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]) - 1;
                    builder.setLore(line, buildString(args, 3));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Lore line set.</green>"));
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid line number.</red>"));
                }
                break;
            case "remove":
                if (args.length < 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie lore remove <line></red>"));
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]) - 1;
                    builder.removeLore(line);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Lore line removed.</green>"));
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid line number.</red>"));
                }
                break;
            case "clear":
                builder.clearLore();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Lore cleared.</green>"));
                break;
            default:
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown lore action. Use add, insert, set, remove, clear.</red>"));
                return;
        }

        player.getInventory().setItemInMainHand(builder.build());
    }

    private String buildString(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            results.add("add");
            results.add("insert");
            results.add("set");
            results.add("remove");
            results.add("clear");
            return results;
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("insert") || args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove")) {
                results.add("<line>");
                return results;
            } else if (args[1].equalsIgnoreCase("add")) {
                results.add("<text>");
                return results;
            }
        }
        return results;
    }
}
