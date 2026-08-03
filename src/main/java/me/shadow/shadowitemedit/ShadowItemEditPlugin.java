package me.shadow.shadowitemedit;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class ShadowItemEditPlugin extends JavaPlugin {

    private static ShadowItemEditPlugin instance;
    private Logger log;
    private me.shadow.shadowitemedit.managers.FileManager fileManager;
    private me.shadow.shadowitemedit.managers.PromptManager promptManager;

    @Override
    public void onEnable() {
        instance = this;
        this.log = getLogger();
        
        log.info("ShadowItemEdit is starting...");
        
        // Setup config
        fileManager = new me.shadow.shadowitemedit.managers.FileManager(this);
        promptManager = new me.shadow.shadowitemedit.managers.PromptManager(this);
        
        // Initialize Managers
        
        // Register Commands
        new me.shadow.shadowitemedit.commands.CommandManager(this);
        
        // Register Listeners
        getServer().getPluginManager().registerEvents(new me.shadow.shadowitemedit.listeners.GUIListener(), this);
        
        log.info("ShadowItemEdit has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        log.info("ShadowItemEdit is shutting down...");
        
        // Cleanup Managers
        
        log.info("ShadowItemEdit has been successfully disabled!");
    }

    public static ShadowItemEditPlugin getInstance() {
        return instance;
    }

    public me.shadow.shadowitemedit.managers.FileManager getFileManager() {
        return fileManager;
    }

    public me.shadow.shadowitemedit.managers.PromptManager getPromptManager() {
        return promptManager;
    }
}
