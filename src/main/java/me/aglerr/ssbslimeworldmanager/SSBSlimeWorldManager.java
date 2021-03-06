package me.aglerr.ssbslimeworldmanager;

import me.aglerr.ssbslimeworldmanager.listeners.SuperiorListener;
import me.aglerr.ssbslimeworldmanager.tasks.TaskManager;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SSBSlimeWorldManager extends JavaPlugin {

    private final SlimeUtils slimeUtils = new SlimeUtils();
    private final TaskManager taskManager = new TaskManager(this);

    @Override
    public void onEnable() {
        // Initialize config.yml
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        // Initialize config values
        ConfigValue.initialize(this.getConfig());
        // Initialize the slime utils
        this.slimeUtils.initialize(taskManager);
        // Register listener
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        this.slimeUtils.unloadAllWorlds();
    }

    private void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SuperiorListener(this), this);
    }

    public SlimeUtils getSlimeUtils(){
        return this.slimeUtils;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
