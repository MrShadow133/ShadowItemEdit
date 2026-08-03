package me.shadow.shadowitemedit.managers;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PromptManager implements Listener {

    private final Map<UUID, Consumer<String>> pendingPrompts = new HashMap<>();

    public PromptManager(ShadowItemEditPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void prompt(Player player, String message, Consumer<String> callback) {
        player.closeInventory();
        player.sendMessage(MiniMessage.miniMessage().deserialize(message));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Type 'cancel' to abort.</gray>"));
        pendingPrompts.put(player.getUniqueId(), callback);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (pendingPrompts.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String input = event.getMessage();
            Consumer<String> callback = pendingPrompts.remove(player.getUniqueId());
            
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Input cancelled.</red>"));
                // Need a way to reopen previous GUI, but callback can handle it or we just do nothing.
                return;
            }

            Bukkit.getScheduler().runTask(ShadowItemEditPlugin.getInstance(), () -> callback.accept(input));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pendingPrompts.remove(event.getPlayer().getUniqueId());
    }
}
