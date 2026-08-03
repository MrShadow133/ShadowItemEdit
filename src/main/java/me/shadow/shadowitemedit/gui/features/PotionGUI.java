package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PotionGUI extends DynamicGUI {

    public PotionGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Potion Effects</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();

        if (item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta potionMeta) {
            List<PotionEffect> effects = potionMeta.getCustomEffects();
            int slot = 0;
            for (PotionEffect effect : effects) {
                if (slot >= 45) break;

                inventory.setItem(slot, new ItemBuilder(Material.POTION)
                        .name("<yellow>" + effect.getType().getName() + "</yellow>")
                        .addLore("<gray>Duration: <white>" + effect.getDuration() + " ticks</white></gray>")
                        .addLore("<gray>Amplifier: <white>" + effect.getAmplifier() + "</white></gray>")
                        .addLore("<red>Right-Click to Remove</red>")
                        .build());
                slot++;
            }
        }

        // Add Effect Button
        inventory.setItem(49, new ItemBuilder(Material.GLASS_BOTTLE)
                .name("<green>Add New Effect</green>")
                .addLore("<gray>Click to open the effect list.</gray>")
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
            new PotionAddGUI(player, this).open(player);
            return;
        }

        if (slot >= 0 && slot < 45 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.POTION) {
            if (event.isRightClick()) {
                if (item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta potionMeta) {
                    List<PotionEffect> effects = potionMeta.getCustomEffects();
                    if (slot < effects.size()) {
                        PotionEffect effectToRemove = effects.get(slot);
                        potionMeta.removeCustomEffect(effectToRemove.getType());
                        item.setItemMeta(potionMeta);
                        saveItem();
                        refresh();
                    }
                }
            }
        }
    }
}
