package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class LoreGUI extends DynamicGUI {

    public LoreGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Edit Lore</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        boolean hasLore = item.hasItemMeta() && item.getItemMeta().hasLore();
        List<Component> lore = hasLore ? item.getItemMeta().lore() : null;

        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                if (i >= 45) break; // Limit to 45 lines for now
                inventory.setItem(i, new ItemBuilder(Material.PAPER)
                        .name("<yellow>Line " + (i + 1) + "</yellow>")
                        .addLore("<gray>Current: </gray>") // We append the actual component in a bit
                        .addLore("")
                        .addLore("<green>Left-Click to Edit</green>")
                        .addLore("<red>Right-Click to Remove</red>")
                        .build());
            }
        }

        // Add Lore Button
        inventory.setItem(49, new ItemBuilder(Material.SUNFLOWER)
                .name("<green>Add New Line</green>")
                .addLore("<gray>Click to type a new line of lore.</gray>")
                .build());

        // Back Button
        inventory.setItem(45, new ItemBuilder(Material.ARROW)
                .name("<red>Back</red>")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        ShadowItemEditPlugin plugin = ShadowItemEditPlugin.getInstance();

        if (slot == 45) {
            if (parent != null) parent.open(player);
            return;
        }

        if (slot == 49) {
            plugin.getPromptManager().prompt(player, "<yellow>Type the new lore line in chat:</yellow>", (input) -> {
                ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                builder.addLore(input);
                player.getInventory().setItemInMainHand(builder.build());
                new LoreGUI(player, parent).open(player);
            });
            return;
        }

        if (slot >= 0 && slot < 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER) {
            int lineIndex = slot;
            
            if (event.isRightClick()) {
                ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                builder.removeLore(lineIndex);
                player.getInventory().setItemInMainHand(builder.build());
                refresh();
            } else if (event.isLeftClick()) {
                plugin.getPromptManager().prompt(player, "<yellow>Type the replacement text for line " + (lineIndex + 1) + ":</yellow>", (input) -> {
                    ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                    builder.setLore(lineIndex, input);
                    player.getInventory().setItemInMainHand(builder.build());
                    new LoreGUI(player, parent).open(player);
                });
            }
        }
    }
}
