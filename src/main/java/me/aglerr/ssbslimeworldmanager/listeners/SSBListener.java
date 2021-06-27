package me.aglerr.ssbslimeworldmanager.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class SSBListener implements Listener {

    private final SlimeUtils slimeUtils;
    public SSBListener(SlimeUtils slimeUtils){
        this.slimeUtils = slimeUtils;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDelete(IslandDisbandEvent event){
        Arrays.stream(World.Environment.values()).forEach(environment ->
                this.slimeUtils.deleteWorld(event.getIsland(), environment));
    }

}
