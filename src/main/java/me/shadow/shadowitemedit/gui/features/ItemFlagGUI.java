package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFlagGUI extends DynamicGUI {

    public ItemFlagGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Toggle Item Flags</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        int slot = 0;

        for (ItemFlag flag : ItemFlag.values()) {
            boolean hasFlag = meta != null && meta.hasItemFlag(flag);
            Material mat = hasFlag ? Material.LIME_DYE : Material.RED_DYE;
            String state = hasFlag ? "<green>Enabled</green>" : "<red>Disabled</red>";

            inventory.setItem(slot, new ItemBuilder(mat)
                    .name("<yellow>" + flag.name() + "</yellow>")
                    .addLore("<gray>Status: " + state + "</gray>")
                    .addLore("<gray>Click to toggle.</gray>")
                    .build());
            slot++;
        }

        // Back Button
        inventory.setItem(49, new ItemBuilder(Material.ARROW)
                .name("<red>Back</red>")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 49) {
            if (parent != null) parent.open(player);
            return;
        }

        if (slot >= 0 && slot < ItemFlag.values().length) {
            ItemFlag flag = ItemFlag.values()[slot];
            ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
            
            if (meta != null) {
                if (meta.hasItemFlag(flag)) {
                    meta.removeItemFlags(flag);
                } else {
                    meta.addItemFlags(flag);
                }
                item.setItemMeta(meta);
                saveItem();
                refresh();
            }
        }
    }
}
