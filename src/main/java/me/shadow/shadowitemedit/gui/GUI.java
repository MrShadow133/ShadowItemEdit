package me.shadow.shadowitemedit.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface GUI extends InventoryHolder {

    void onInventoryClick(InventoryClickEvent event);

    void onInventoryDrag(InventoryDragEvent event);

    void onInventoryClose(InventoryCloseEvent event);

    void open(Player player);
}
