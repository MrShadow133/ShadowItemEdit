package me.shadow.shadowitemedit.commands;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.ConfigGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(ShadowItemEditPlugin plugin) {
        plugin.getCommand("itemedit").setExecutor(this);
        plugin.getCommand("itemedit").setTabCompleter(this);
        
        // Add subcommands here
        subcommands.add(new NameCommand());
        subcommands.add(new ClearNameCommand());
        subcommands.add(new LoreCommand());
        subcommands.add(new EnchantCommand());
        subcommands.add(new FlagCommand());
        subcommands.add(new UnbreakableCommand());
        subcommands.add(new CustomModelDataCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("gui")) {
                if (sender instanceof Player player) {
                    if (player.hasPermission("shadowitemedit.gui") || player.hasPermission("shadowitemedit.admin")) {
                        new me.shadow.shadowitemedit.gui.features.MainEditorGUI(player).open(player);
                    } else {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission.</red>"));
                    }
                } else {
                    sender.sendMessage("Only players can use the GUI.");
                }
                return true;
            }

            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    if (sender.hasPermission(subcommand.getPermission()) || sender.hasPermission("shadowitemedit.admin")) {
                        subcommand.perform(sender, args);
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission.</red>"));
                    }
                    return true;
                }
            }
        }

        // Default logic or Help Menu
        Component help = MiniMessage.miniMessage().deserialize("<dark_gray>--- <yellow>ShadowItemEdit</yellow> ---</dark_gray>\n<gray>/ie gui - Open GUI Editor\n/ie help - Show help</gray>");
        sender.sendMessage(help);
        
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        
        if (args.length == 1) {
            results.add("gui");
            for (SubCommand sub : subcommands) {
                if (sender.hasPermission(sub.getPermission()) || sender.hasPermission("shadowitemedit.admin")) {
                    results.add(sub.getName());
                }
            }
            return results.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        } else if (args.length > 1) {
            for (SubCommand sub : subcommands) {
                if (args[0].equalsIgnoreCase(sub.getName())) {
                    if (sender.hasPermission(sub.getPermission()) || sender.hasPermission("shadowitemedit.admin")) {
                        List<String> subArgs = sub.getSubcommandArguments(sender, args);
                        if (subArgs != null) {
                            return subArgs.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
                        }
                    }
                }
            }
        }
        
        return results;
    }
}
