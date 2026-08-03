package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentAddGUI extends DynamicGUI {

    private final List<Enchantment> allEnchants;

    public EnchantmentAddGUI(Player player, DynamicGUI parent) {
        super(player, parent);
        this.allEnchants = new ArrayList<>();
        for (Enchantment enchant : Registry.ENCHANTMENT) {
            allEnchants.add(enchant);
        }
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Add Enchantment (Page " + (page + 1) + ")</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, allEnchants.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Enchantment enchant = allEnchants.get(i);
            String name = enchant.getKey().getKey();

            inventory.setItem(slot, new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name("<yellow>" + name + "</yellow>")
                    .addLore("<gray>Click to add this enchantment.</gray>")
                    .build());
            slot++;
        }

        // Pagination Buttons
        if (page > 0) {
            inventory.setItem(48, new ItemBuilder(Material.ARROW)
                    .name("<green>Previous Page</green>")
                    .build());
        }

        if (endIndex < allEnchants.size()) {
            inventory.setItem(50, new ItemBuilder(Material.ARROW)
                    .name("<green>Next Page</green>")
                    .build());
        }

        // Back Button
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
            init(); // Re-init to update title
            return;
        }

        if (slot == 50 && (page + 1) * maxItemsPerPage < allEnchants.size()) {
            page++;
            init(); // Re-init to update title
            return;
        }

        if (slot >= 0 && slot < maxItemsPerPage && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
            int index = (page * maxItemsPerPage) + slot;
            if (index < allEnchants.size()) {
                Enchantment selected = allEnchants.get(index);
                ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter level for " + selected.getKey().getKey() + ":</yellow>", (input) -> {
                    try {
                        int level = Integer.parseInt(input);
                        ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                        builder.addEnchant(selected, level);
                        player.getInventory().setItemInMainHand(builder.build());
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantment added.</green>"));
                    } catch (NumberFormatException e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid level.</red>"));
                    }
                    if (parent != null) parent.open(player);
                });
            }
        }
    }
}
