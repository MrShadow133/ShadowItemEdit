package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class NBTGUI extends DynamicGUI {

    public NBTGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>NBT Data (PDC)</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        if (item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            List<NamespacedKey> keys = new ArrayList<>(container.getKeys());

            int slot = 0;
            for (NamespacedKey key : keys) {
                if (slot >= 45) break;

                // For simplicity, we assume strings to display them
                String valueStr = "Unknown Type";
                try {
                    String strVal = container.get(key, PersistentDataType.STRING);
                    if (strVal != null) valueStr = strVal;
                } catch (IllegalArgumentException ignored) {}

                inventory.setItem(slot, new ItemBuilder(Material.NAME_TAG)
                        .name("<yellow>" + key.toString() + "</yellow>")
                        .addLore("<gray>Value (String): <white>" + valueStr + "</white></gray>")
                        .addLore("<red>Right-Click to Remove</red>")
                        .build());
                slot++;
            }
        }

        // Add Tag Button
        inventory.setItem(49, new ItemBuilder(Material.EMERALD)
                .name("<green>Add String Tag</green>")
                .addLore("<gray>Click to add a new custom tag.</gray>")
                .build());

        // Back Button
        inventory.setItem(45, new ItemBuilder(Material.ARROW)
                .name("<red>Back</red>")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 45) {
            if (parent != null) parent.open(player);
            return;
        }

        if (slot == 49) {
            ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter tag format: <namespace:key> <value></yellow>", (input) -> {
                try {
                    String[] parts = input.split(" ", 2);
                    if (parts.length < 2) throw new IllegalArgumentException("Invalid format.");
                    String[] keyParts = parts[0].split(":");
                    NamespacedKey key = new NamespacedKey(keyParts[0], keyParts[1]);
                    String value = parts[1];

                    org.bukkit.inventory.meta.ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>NBT Tag added.</green>"));
                } catch (Exception e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to parse tag.</red>"));
                }
                if (parent != null) parent.open(player);
            });
            return;
        }

        if (slot >= 0 && slot < 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NAME_TAG) {
            if (event.isRightClick()) {
                if (item.hasItemMeta()) {
                    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                    List<NamespacedKey> keys = new ArrayList<>(container.getKeys());
                    if (slot < keys.size()) {
                        NamespacedKey keyToRemove = keys.get(slot);
                        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                        meta.getPersistentDataContainer().remove(keyToRemove);
                        item.setItemMeta(meta);
                        saveItem();
                        refresh();
                    }
                }
            }
        }
    }
}
