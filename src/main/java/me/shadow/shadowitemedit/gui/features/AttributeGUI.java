package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AttributeGUI extends DynamicGUI {

    public AttributeGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Current Attributes</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        if (item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers()) {
            int slot = 0;
            // Iterate over all attributes
            for (Attribute attribute : Attribute.values()) {
                Collection<AttributeModifier> modifiers = item.getItemMeta().getAttributeModifiers(attribute);
                if (modifiers != null) {
                    for (AttributeModifier modifier : modifiers) {
                        if (slot >= 45) break;

                        inventory.setItem(slot, new ItemBuilder(Material.IRON_CHESTPLATE)
                                .name("<yellow>" + attribute.name() + "</yellow>")
                                .addLore("<gray>Amount: <white>" + modifier.getAmount() + "</white></gray>")
                                .addLore("<gray>Operation: <white>" + modifier.getOperation().name() + "</white></gray>")
                                .addLore("<red>Right-Click to Remove</red>")
                                .build());
                        slot++;
                    }
                }
            }
        }

        // Add Attribute Button
        inventory.setItem(49, new ItemBuilder(Material.EMERALD)
                .name("<green>Add New Attribute</green>")
                .addLore("<gray>Click to select an attribute to add.</gray>")
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
            new AttributeAddGUI(player, this).open(player);
            return;
        }

        if (slot >= 0 && slot < 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
            if (event.isRightClick()) {
                if (item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers()) {
                    ItemMeta meta = item.getItemMeta();
                    // Just removing all modifiers to simplify for now, ideally remove specific modifier
                    // This is simplified. Proper implementation requires tracking the exact modifier per slot.
                    // To do it correctly, we rebuild the modifiers list excluding the clicked one.
                    int currentIndex = 0;
                    for (Attribute attribute : Attribute.values()) {
                        Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(attribute);
                        if (modifiers != null) {
                            for (AttributeModifier modifier : modifiers) {
                                if (currentIndex == slot) {
                                    meta.removeAttributeModifier(attribute, modifier);
                                    item.setItemMeta(meta);
                                    saveItem();
                                    refresh();
                                    return;
                                }
                                currentIndex++;
                            }
                        }
                    }
                }
            }
        }
    }
}
