package me.aglerr.ssbslimeworldmanager;

import me.aglerr.ssbslimeworldmanager.listeners.InitializeListener;
import me.aglerr.ssbslimeworldmanager.listeners.SSBListener;
import me.aglerr.ssbslimeworldmanager.tasks.TaskManager;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SSBSlimeWorldManager extends JavaPlugin {

    private final SlimeUtils slimeUtils = new SlimeUtils(this);
    private final TaskManager taskManager = new TaskManager(this);

    @Override
    public void onEnable() {
        this.slimeUtils.initialize();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        this.slimeUtils.unloadAllWorlds();
    }

    private void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InitializeListener(this), this);
        Bukkit.getScheduler().runTask(this, () ->
                pm.registerEvents(new SSBListener(this.slimeUtils), this));
    }

    public SlimeUtils getSlimeUtils(){
        return this.slimeUtils;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
