package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionAddGUI extends DynamicGUI {

    private final List<PotionEffectType> effects;

    public PotionAddGUI(Player player, DynamicGUI parent) {
        super(player, parent);
        effects = new ArrayList<>();
        for (PotionEffectType type : Registry.POTION_EFFECT_TYPE) {
            effects.add(type);
        }
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Add Potion Effect (Page " + (page + 1) + ")</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, effects.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            PotionEffectType type = effects.get(i);

            inventory.setItem(slot, new ItemBuilder(Material.GLASS_BOTTLE)
                    .name("<yellow>" + type.getName() + "</yellow>")
                    .addLore("<gray>Click to add this effect.</gray>")
                    .build());
            slot++;
        }

        if (page > 0) {
            inventory.setItem(48, new ItemBuilder(Material.ARROW)
                    .name("<green>Previous Page</green>")
                    .build());
        }

        if (endIndex < effects.size()) {
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

        if (slot == 50 && (page + 1) * maxItemsPerPage < effects.size()) {
            page++;
            init();
            return;
        }

        if (slot >= 0 && slot < maxItemsPerPage && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.GLASS_BOTTLE) {
            int index = (page * maxItemsPerPage) + slot;
            if (index < effects.size()) {
                PotionEffectType selected = effects.get(index);
                ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter duration (in ticks) and amplifier separated by a space (e.g. 600 1):</yellow>", (input) -> {
                    try {
                        String[] parts = input.split(" ");
                        int duration = Integer.parseInt(parts[0]);
                        int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                        
                        if (player.getInventory().getItemInMainHand().getItemMeta() instanceof PotionMeta potionMeta) {
                            potionMeta.addCustomEffect(new PotionEffect(selected, duration, amplifier), true);
                            player.getInventory().getItemInMainHand().setItemMeta(potionMeta);
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Potion effect added.</green>"));
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Item is not a potion/tipped arrow.</red>"));
                        }
                    } catch (Exception e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid format. Expected: <duration> <amplifier></red>"));
                    }
                    if (parent != null) parent.open(player);
                });
            }
        }
    }
}
