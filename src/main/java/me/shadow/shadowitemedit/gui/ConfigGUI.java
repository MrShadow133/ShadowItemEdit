package me.shadow.shadowitemedit.gui;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConfigGUI implements GUI {

    private final String menuId;
    private final Inventory inventory;
    private final Map<Integer, String> actions = new HashMap<>();

    public ConfigGUI(String menuId) {
        this.menuId = menuId;
        ConfigurationSection section = ShadowItemEditPlugin.getInstance().getFileManager().getGuiConfig().getConfigurationSection("menus." + menuId);
        
        if (section == null) {
            throw new IllegalArgumentException("Menu " + menuId + " not found!");
        }

        String title = section.getString("title", "<dark_gray>Menu</dark_gray>");
        int rows = section.getInt("rows", 6);
        
        this.inventory = Bukkit.createInventory(this, rows * 9, MiniMessage.miniMessage().deserialize(title));

        for (Map<?, ?> itemMap : section.getMapList("items")) {
            int slot = (Integer) itemMap.get("slot");
            String materialStr = (String) itemMap.get("material");
            String name = (String) itemMap.get("name");
            java.util.List<String> lore = (java.util.List<String>) itemMap.get("lore");
            String action = (String) itemMap.get("action");

            Material material = Material.matchMaterial(materialStr);
            if (material == null) material = Material.STONE;

            ItemBuilder builder = new ItemBuilder(material);
            if (name != null) builder.name(name);
            if (lore != null) {
                for (String line : lore) {
                    builder.addLore(line);
                }
            }

            inventory.setItem(slot, builder.build());
            if (action != null) {
                actions.put(slot, action);
            }
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true); // Prevent item moving
        
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inventory)) {
            return;
        }

        int slot = event.getSlot();
        if (actions.containsKey(slot)) {
            String action = actions.get(slot);
            handleAction((Player) event.getWhoClicked(), action);
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        // Handle cleanup if necessary
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void handleAction(Player player, String action) {
        String[] split = action.split(" ", 2);
        String type = split[0];
        String arg = split.length > 1 ? split[1] : "";

        switch (type) {
            case "[OPEN]":
                try {
                    ConfigGUI nextMenu = new ConfigGUI(arg);
                    nextMenu.open(player);
                } catch (Exception e) {
                    player.sendMessage("Menu not found.");
                }
                break;
            case "[CLOSE]":
                player.closeInventory();
                break;
            case "[COMMAND]":
                player.performCommand(arg);
                player.closeInventory();
                break;
            case "[MESSAGE]":
                player.sendMessage(MiniMessage.miniMessage().deserialize(arg));
                break;
            // Additional actions like [EDIT_NAME] will be handled by sending a message and prompting chat input.
            // A comprehensive GUI system would use conversation API or a prompt manager.
            default:
                player.sendMessage("Unhandled action: " + action);
                break;
        }
    }
}
