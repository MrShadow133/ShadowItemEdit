package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainEditorGUI extends DynamicGUI {

    public MainEditorGUI(Player player) {
        super(player, null);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Shadow Item Editor</dark_gray>");
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void render() {
        inventory.clear();
        
        // Item Display in Center
        inventory.setItem(4, item);

        // Edit Name
        inventory.setItem(10, new ItemBuilder(Material.NAME_TAG)
                .name("<yellow>Edit Name</yellow>")
                .addLore("<gray>Click to set a new name.</gray>")
                .build());

        // Edit Lore
        boolean hasLore = item.hasItemMeta() && item.getItemMeta().hasLore();
        inventory.setItem(11, new ItemBuilder(Material.BOOK)
                .name("<yellow>Edit Lore</yellow>")
                .addLore(hasLore ? "<green>Active</green>" : "<gray>Click to manage lore.</gray>")
                .build());

        // Enchantments
        boolean hasEnchants = item.hasItemMeta() && item.getItemMeta().hasEnchants();
        inventory.setItem(12, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("<yellow>Edit Enchantments</yellow>")
                .addLore(hasEnchants ? "<green>Active</green>" : "<gray>Click to manage enchants.</gray>")
                .build());

        // Attributes
        boolean hasAttributes = item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers();
        inventory.setItem(13, new ItemBuilder(Material.IRON_CHESTPLATE)
                .name("<yellow>Edit Attributes</yellow>")
                .addLore(hasAttributes ? "<green>Active</green>" : "<gray>Click to manage attributes.</gray>")
                .build());

        // Item Flags
        boolean hasFlags = item.hasItemMeta() && !item.getItemMeta().getItemFlags().isEmpty();
        inventory.setItem(14, new ItemBuilder(Material.WHITE_BANNER)
                .name("<yellow>Edit Flags</yellow>")
                .addLore(hasFlags ? "<green>Active</green>" : "<gray>Click to manage flags.</gray>")
                .build());

        // Durability / Unbreakable
        boolean unbreakable = item.hasItemMeta() && item.getItemMeta().isUnbreakable();
        inventory.setItem(15, new ItemBuilder(Material.ANVIL)
                .name("<yellow>Durability / Unbreakable</yellow>")
                .addLore("<gray>Unbreakable: " + (unbreakable ? "<green>Yes</green>" : "<red>No</red>") + "</gray>")
                .addLore("<gray>Click to toggle unbreakable.</gray>")
                .build());

        // Custom Model Data
        boolean hasCMD = item.hasItemMeta() && item.getItemMeta().hasCustomModelData();
        inventory.setItem(16, new ItemBuilder(Material.COMMAND_BLOCK)
                .name("<yellow>Custom Model Data</yellow>")
                .addLore(hasCMD ? "<gray>Current: <white>" + item.getItemMeta().getCustomModelData() + "</white></gray>" : "<gray>Click to set CMD.</gray>")
                .build());

        // Potions
        boolean hasPotion = item.hasItemMeta() && item.getItemMeta() instanceof org.bukkit.inventory.meta.PotionMeta;
        inventory.setItem(19, new ItemBuilder(Material.POTION)
                .name("<yellow>Edit Potions</yellow>")
                .addLore(hasPotion ? "<green>Active</green>" : "<gray>Click to manage effects.</gray>")
                .build());

        // Skulls
        boolean isSkull = item.getType() == Material.PLAYER_HEAD;
        inventory.setItem(20, new ItemBuilder(Material.PLAYER_HEAD)
                .name("<yellow>Edit Skull</yellow>")
                .addLore(isSkull ? "<green>Active</green>" : "<gray>Hold a player head to edit.</gray>")
                .build());

        // NBT
        inventory.setItem(25, new ItemBuilder(Material.NAME_TAG)
                .name("<red>Advanced NBT</red>")
                .addLore("<gray>Click to edit raw NBT data.</gray>")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        ShadowItemEditPlugin plugin = ShadowItemEditPlugin.getInstance();

        switch (slot) {
            case 10: // Name
                plugin.getPromptManager().prompt(player, "<yellow>Enter the new name for the item:</yellow>", (input) -> {
                    ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                    builder.name(input);
                    player.getInventory().setItemInMainHand(builder.build());
                    new MainEditorGUI(player).open(player);
                });
                break;
            case 11: // Lore
                new LoreGUI(player, this).open(player);
                break;
            case 12: // Enchantments
                new EnchantmentListGUI(player, this).open(player);
                break;
            case 13: // Attributes
                new AttributeGUI(player, this).open(player);
                break;
            case 14: // Item Flags
                new ItemFlagGUI(player, this).open(player);
                break;
            case 15: // Toggle Unbreakable
                boolean isUnbreakable = item.hasItemMeta() && item.getItemMeta().isUnbreakable();
                new ItemBuilder(item).setUnbreakable(!isUnbreakable).build();
                saveItem();
                refresh();
                break;
            case 16: // Custom Model Data
                plugin.getPromptManager().prompt(player, "<yellow>Enter the new Custom Model Data (integer):</yellow>", (input) -> {
                    try {
                        int cmd = Integer.parseInt(input);
                        ItemBuilder builder = new ItemBuilder(player.getInventory().getItemInMainHand());
                        builder.setCustomModelData(cmd);
                        player.getInventory().setItemInMainHand(builder.build());
                    } catch (NumberFormatException e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid integer.</red>"));
                    }
                    new MainEditorGUI(player).open(player);
                });
                break;
            case 19: // Potions
                new PotionGUI(player, this).open(player);
                break;
            case 20: // Skulls
                new SkullGUI(player, this).open(player);
                break;
            case 25: // NBT
                new NBTGUI(player, this).open(player);
                break;
        }
    }
}
