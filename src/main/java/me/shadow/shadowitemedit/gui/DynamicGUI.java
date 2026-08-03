package me.shadow.shadowitemedit.gui;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class DynamicGUI implements GUI {

    protected final Player player;
    protected ItemStack item;
    protected Inventory inventory;
    protected final DynamicGUI parent;
    
    // Pagination fields
    protected int page = 0;
    protected int maxItemsPerPage = 45;

    public DynamicGUI(Player player, DynamicGUI parent) {
        this.player = player;
        this.parent = parent;
        this.item = player.getInventory().getItemInMainHand();
    }

    public void init() {
        if (item == null || item.getType().isAir()) {
            player.sendMessage("§cYou must hold an item to edit.");
            if (parent != null) {
                parent.open(player);
            } else {
                player.closeInventory();
            }
            return;
        }
        
        Component title = getTitle();
        int rows = getRows();
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        
        render();
    }

    public abstract Component getTitle();

    public abstract int getRows();

    public abstract void render();

    protected void saveItem() {
        player.getInventory().setItemInMainHand(item);
    }

    protected void refresh() {
        this.item = player.getInventory().getItemInMainHand();
        render();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inventory)) {
            return;
        }
        handleClick(event);
    }

    public abstract void handleClick(InventoryClickEvent event);

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        // Cleanup if needed
    }

    @Override
    public void open(Player player) {
        if (this.inventory == null) {
            init();
        } else {
            refresh();
        }
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
