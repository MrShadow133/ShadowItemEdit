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
import java.util.List;
import java.util.UUID;

public class AttributeAddGUI extends DynamicGUI {

    private final List<Attribute> attributes;

    public AttributeAddGUI(Player player, DynamicGUI parent) {
        super(player, parent);
        attributes = new ArrayList<>();
        for (Attribute attr : Attribute.values()) {
            attributes.add(attr);
        }
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Add Attribute (Page " + (page + 1) + ")</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, attributes.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Attribute attr = attributes.get(i);

            inventory.setItem(slot, new ItemBuilder(Material.IRON_CHESTPLATE)
                    .name("<yellow>" + attr.name() + "</yellow>")
                    .addLore("<gray>Click to add this attribute.</gray>")
                    .build());
            slot++;
        }

        if (page > 0) {
            inventory.setItem(48, new ItemBuilder(Material.ARROW)
                    .name("<green>Previous Page</green>")
                    .build());
        }

        if (endIndex < attributes.size()) {
            inventory.setItem(50, new ItemBuilder(Material.ARROW)
                    .name("<green>Next Page</green>")
                    .build());
        }

        inventory.setItem(45, new ItemBuilder(Material.BARRIER)
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

        if (slot == 48 && page > 0) {
            page--;
            init();
            return;
        }

        if (slot == 50 && (page + 1) * maxItemsPerPage < attributes.size()) {
            page++;
            init();
            return;
        }

        if (slot >= 0 && slot < maxItemsPerPage && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
            int index = (page * maxItemsPerPage) + slot;
            if (index < attributes.size()) {
                Attribute selected = attributes.get(index);
                ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter the amount for " + selected.name() + ":</yellow>", (input) -> {
                    try {
                        double amount = Double.parseDouble(input);
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(ShadowItemEditPlugin.getInstance(), "modifier_" + UUID.randomUUID().toString().replace("-", ""));
                        meta.addAttributeModifier(selected, new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Attribute added.</green>"));
                    } catch (NumberFormatException e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid amount.</red>"));
                    }
                    if (parent != null) parent.open(player);
                });
            }
        }
    }
}
