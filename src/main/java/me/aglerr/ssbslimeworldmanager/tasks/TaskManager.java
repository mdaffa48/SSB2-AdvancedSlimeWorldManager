package me.aglerr.ssbslimeworldmanager.tasks;

import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {

    private final SSBSlimeWorldManager plugin;
    public TaskManager(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    private final Map<String, WorldUnloadTask> worldTasks = new ConcurrentHashMap<>();

    public WorldUnloadTask putAndGetTask(String worldName){
        return worldTasks.computeIfAbsent(worldName, w -> new WorldUnloadTask(plugin, worldName));
    }

    public void stopTask(String worldName){
        WorldUnloadTask worldUnloadTask = this.worldTasks.remove(worldName);
        if(worldUnloadTask != null){
            worldUnloadTask.cancel();
        }
    }

}
