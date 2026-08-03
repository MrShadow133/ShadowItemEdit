package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ClearNameCommand extends SubCommand {

    @Override
    public String getName() {
        return "clearname";
    }

    @Override
    public String getDescription() {
        return "Clears the name of the held item.";
    }

    @Override
    public String getSyntax() {
        return "/ie clearname";
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

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must hold an item.</red>"));
            return;
        }

        ItemBuilder builder = new ItemBuilder(item);
        builder.clearName();
        player.getInventory().setItemInMainHand(builder.build());

        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item name cleared!</green>"));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
