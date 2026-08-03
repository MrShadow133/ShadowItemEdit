package me.shadow.shadowitemedit.gui.features;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import me.shadow.shadowitemedit.gui.DynamicGUI;
import me.shadow.shadowitemedit.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullGUI extends DynamicGUI {

    public SkullGUI(Player player, DynamicGUI parent) {
        super(player, parent);
    }

    @Override
    public Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>Edit Player Head</dark_gray>");
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void render() {
        inventory.clear();

        if (item.getType() != Material.PLAYER_HEAD) {
            inventory.setItem(13, new ItemBuilder(Material.BARRIER)
                    .name("<red>Not a Player Head</red>")
                    .addLore("<gray>You must hold a player head to use this menu.</gray>")
                    .build());
            inventory.setItem(22, new ItemBuilder(Material.ARROW).name("<red>Back</red>").build());
            return;
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        String ownerName = meta.getOwningPlayer() != null && meta.getOwningPlayer().getName() != null 
                ? meta.getOwningPlayer().getName() 
                : "Unknown / Custom Texture";

        inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD)
                .name("<yellow>Set Player Owner</yellow>")
                .addLore("<gray>Current: <white>" + ownerName + "</white></gray>")
                .addLore("<gray>Click to enter a player's name.</gray>")
                .build());

        inventory.setItem(15, new ItemBuilder(Material.COMMAND_BLOCK)
                .name("<yellow>Set Custom Texture</yellow>")
                .addLore("<gray>Click to enter a Base64 skin texture.</gray>")
                .build());

        inventory.setItem(22, new ItemBuilder(Material.ARROW)
                .name("<red>Back</red>")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 22) {
            if (parent != null) parent.open(player);
            return;
        }

        if (item.getType() != Material.PLAYER_HEAD) return;

        if (slot == 11) {
            ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter the player's name:</yellow>", (input) -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(input);
                SkullMeta meta = (SkullMeta) player.getInventory().getItemInMainHand().getItemMeta();
                meta.setOwningPlayer(target);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Skull owner updated.</green>"));
                if (parent != null) parent.open(player);
            });
        }

        if (slot == 15) {
            ShadowItemEditPlugin.getInstance().getPromptManager().prompt(player, "<yellow>Enter the Base64 texture string or URL:</yellow>", (input) -> {
                // To set custom textures via Paper API:
                SkullMeta meta = (SkullMeta) player.getInventory().getItemInMainHand().getItemMeta();
                
                try {
                    com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                    try {
                        URL url = new URL(input);
                        org.bukkit.profile.PlayerTextures textures = profile.getTextures();
                        textures.setSkin(url);
                        profile.setTextures(textures);
                    } catch (MalformedURLException e) {
                        profile.setProperty(new com.destroystokyo.paper.profile.ProfileProperty("textures", input));
                    }
                    meta.setPlayerProfile(profile);
                } catch (Exception ex) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid texture format.</red>"));
                }
                
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Skull texture updated.</green>"));
                if (parent != null) parent.open(player);
            });
        }
    }
}
