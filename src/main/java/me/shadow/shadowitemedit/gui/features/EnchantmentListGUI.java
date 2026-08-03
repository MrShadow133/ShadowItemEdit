package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentListGUI extends DynamicGUI {

    public EnchantmentListGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Current Enchantments</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
            int slot = 0;
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                if (slot >= 45) break;
                
                String enchantName = entry.getKey().getKey().getKey();
                int level = entry.getValue();

                inventory.setItem(slot, new ItemBuilder(Material.ENCHANTED_BOOK)
                        .name("<yellow>" + enchantName + " <gray>[" + level + "]</gray></yellow>")
                        .addLore("<red>Right-Click to Remove</red>")
                        .build());
                slot++;
            }
        }

        // Add Enchantment Button
        inventory.setItem(49, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .name("<green>Add New Enchantment</green>")
                .addLore("<gray>Click to open the enchantment list.</gray>")
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
            new EnchantmentAddGUI(player, this).open(player);
            return;
        }

        if (slot >= 0 && slot < 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
            if (event.isRightClick()) {
                if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
                    List<Enchantment> enchants = new ArrayList<>(item.getItemMeta().getEnchants().keySet());
                    if (slot < enchants.size()) {
                        Enchantment enchantToRemove = enchants.get(slot);
                        ItemBuilder builder = new ItemBuilder(item);
                        builder.removeEnchant(enchantToRemove);
                        player.getInventory().setItemInMainHand(builder.build());
                        refresh();
                    }
                }
            }
        }
    }
}
