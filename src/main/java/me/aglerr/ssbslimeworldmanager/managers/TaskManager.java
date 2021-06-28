package me.aglerr.ssbslimeworldmanager.managers;

import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.tasks.WorldUnloadTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {

    private final SSBSlimeWorldManager plugin;
    public TaskManager(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    private final Map<String, WorldUnloadTask> worldTasks = new ConcurrentHashMap<>();

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
