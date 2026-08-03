package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UnbreakableCommand extends SubCommand {

    @Override
    public String getName() {
        return "unbreakable";
    }

    @Override
    public String getDescription() {
        return "Toggles the unbreakable state.";
    }

    @Override
    public String getSyntax() {
        return "/ie unbreakable <true|false>";
    }

    @Override
    public String getPermission() {
        return "shadowitemedit.unbreakable";
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

        boolean state = Boolean.parseBoolean(args[1]);
        ItemBuilder builder = new ItemBuilder(item);
        builder.setUnbreakable(state);
        player.getInventory().setItemInMainHand(builder.build());

        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Unbreakable state set to " + state + ".</green>"));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            results.add("true");
            results.add("false");
        }
        return results;
    }
}
