package me.aglerr.ssbslimeworldmanager.tasks;

import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.managers.TaskManager;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public final class WorldUnloadTask extends BukkitRunnable {

    private static final long UNLOAD_DELAY = 24000;

    private final String worldName;
    private final SSBSlimeWorldManager plugin;
    private long lastTimeUpdate = System.currentTimeMillis() / 1000;

    public WorldUnloadTask(SSBSlimeWorldManager plugin, String worldName){
        this.plugin = plugin;
        this.worldName = worldName;
        this.runTaskTimer(plugin, UNLOAD_DELAY, UNLOAD_DELAY);
    }

    public void updateLastTime(){
        lastTimeUpdate = System.currentTimeMillis() / 1000;
    }

    @Override
    public void run() {
        TaskManager taskManager = plugin.getTaskManager();
        SlimeUtils slimeUtils = plugin.getSlimeUtils();
        World world = Bukkit.getWorld(this.worldName);

        if(world == null) {
            taskManager.stopTask(this.worldName);
            return;
        }

        long currentTime = System.currentTimeMillis() / 1000;

        if(currentTime - lastTimeUpdate > UNLOAD_DELAY && world.getPlayers().isEmpty()){
            slimeUtils.unloadWorld(this.worldName);
        } else {
            this.updateLastTime();
        }
    }

}
