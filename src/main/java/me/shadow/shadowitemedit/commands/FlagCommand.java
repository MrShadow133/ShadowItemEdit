package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FlagCommand extends SubCommand {

    @Override
    public String getName() {
        return "flag";
    }

    @Override
    public String getDescription() {
        return "Manage item flags.";
    }

    @Override
    public String getSyntax() {
        return "/ie flag <add|remove|clear> [flag]";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.flag";
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
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie flag add <flag></red>"));
                    return;
                }
                try {
                    ItemFlag flag = ItemFlag.valueOf(args[2].toUpperCase());
                    builder.addFlag(flag);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Flag added.</green>"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid flag.</red>"));
                }
                break;
            case "remove":
                if (args.length < 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie flag remove <flag></red>"));
                    return;
                }
                try {
                    ItemFlag flag = ItemFlag.valueOf(args[2].toUpperCase());
                    builder.removeFlag(flag);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Flag removed.</green>"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid flag.</red>"));
                }
                break;
            case "clear":
                builder.clearFlags();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Flags cleared.</green>"));
                break;
            default:
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown action. Use add, remove, clear.</red>"));
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
            for (ItemFlag flag : ItemFlag.values()) {
                results.add(flag.name());
            }
            return results;
        }
        return results;
    }
}
