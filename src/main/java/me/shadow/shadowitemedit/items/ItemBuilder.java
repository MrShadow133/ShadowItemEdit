package me.shadow.shadowitemedit.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.displayName(parse(name));
        }
        return this;
    }

    public ItemBuilder clearName() {
        if (meta != null) {
            meta.displayName(null);
        }
        return this;
    }

    public ItemBuilder addLore(String line) {
        if (meta != null) {
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();
            lore.add(parse(line));
            meta.lore(lore);
        }
        return this;
    }

    public ItemBuilder setLore(int index, String line) {
        if (meta != null) {
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();
            if (index >= 0 && index < lore.size()) {
                lore.set(index, parse(line));
                meta.lore(lore);
            }
        }
        return this;
    }

    public ItemBuilder insertLore(int index, String line) {
        if (meta != null) {
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();
            if (index >= 0 && index <= lore.size()) {
                lore.add(index, parse(line));
                meta.lore(lore);
            }
        }
        return this;
    }

    public ItemBuilder removeLore(int index) {
        if (meta != null) {
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();
            if (index >= 0 && index < lore.size()) {
                lore.remove(index);
                meta.lore(lore);
            }
        }
        return this;
    }

    public ItemBuilder clearLore() {
        if (meta != null) {
            meta.lore(null);
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        if (meta != null) {
            meta.addEnchant(enchant, level, true);
        }
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchant) {
        if (meta != null) {
            meta.removeEnchant(enchant);
        }
        return this;
    }

    public ItemBuilder clearEnchants() {
        if (meta != null) {
            for (Enchantment enchant : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchant);
            }
        }
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        if (meta != null) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        if (meta != null) {
            meta.removeItemFlags(flag);
        }
        return this;
    }

    public ItemBuilder clearFlags() {
        if (meta != null) {
            meta.removeItemFlags(ItemFlag.values());
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        if (meta != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public ItemBuilder clearCustomModelData() {
        if (meta != null) {
            meta.setCustomModelData(null);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(int damage) {
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    public static Component parse(String text) {
        if (text == null) return Component.empty();
        // Support legacy colors and hex, but convert to modern mini message
        String legacyReplaced = text.replace("&", "§");
        Component legacyComponent = LegacyComponentSerializer.legacySection().deserialize(legacyReplaced);
        String mmString = MiniMessage.miniMessage().serialize(legacyComponent);
        // And also parse pure MiniMessage if there's any
        return MiniMessage.miniMessage().deserialize(text);
    }
}
