package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class NameCommand extends SubCommand {

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public String getDescription() {
        return "Sets the name of the held item.";
    }

    @Override
    public String getSyntax() {
        return "/ie name <text>";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.name";
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

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            nameBuilder.append(args[i]).append(" ");
        }
        String name = nameBuilder.toString().trim();

        ItemBuilder builder = new ItemBuilder(item);
        builder.name(name);
        player.getInventory().setItemInMainHand(builder.build());

        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item name updated!</green>"));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("<name>");
        }
        return Collections.emptyList();
    }
}
