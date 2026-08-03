package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomModelDataCommand extends SubCommand {

    @Override
    public String getName() {
        return "cmd";
    }

    @Override
    public String getDescription() {
        return "Manage custom model data.";
    }

    @Override
    public String getSyntax() {
        return "/ie cmd <set|clear> [id]";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.cmd";
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
            case "set":
                if (args.length < 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /ie cmd set <id></red>"));
                    return;
                }
                try {
                    int id = Integer.parseInt(args[2]);
                    builder.setCustomModelData(id);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Custom model data set to " + id + ".</green>"));
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid ID.</red>"));
                }
                break;
            case "clear":
                builder.clearCustomModelData();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Custom model data cleared.</green>"));
                break;
            default:
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown action. Use set, clear.</red>"));
                return;
        }

        player.getInventory().setItemInMainHand(builder.build());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            results.add("set");
            results.add("clear");
        }
        return results;
    }
}
