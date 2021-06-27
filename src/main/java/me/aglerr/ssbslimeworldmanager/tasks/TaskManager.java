package me.aglerr.ssbslimeworldmanager.tasks;

import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private final SSBSlimeWorldManager plugin;
    public TaskManager(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    private final Map<String, WorldUnloadTask> worldTasks = new HashMap<>();

    public WorldUnloadTask putAndGetTask(String worldName){
        return this.worldTasks.computeIfAbsent(worldName, v -> new WorldUnloadTask(plugin, worldName));
    }

    public void stopTask(String worldName){
        WorldUnloadTask worldUnloadTask = this.worldTasks.remove(worldName);
        if(worldUnloadTask != null){
            worldUnloadTask.cancel();
        }
    }

}
