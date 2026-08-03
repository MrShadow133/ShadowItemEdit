package me.shadow.shadowitemedit.managers;

import me.shadow.shadowitemedit.ShadowItemEditPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final ShadowItemEditPlugin plugin;
    private File guiFile;
    private FileConfiguration guiConfig;
    
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public FileManager(ShadowItemEditPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        plugin.saveDefaultConfig();
        
        guiFile = new File(plugin.getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void saveGuiConfig() {
        try {
            guiConfig.save(guiFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save gui.yml!");
        }
    }
}
