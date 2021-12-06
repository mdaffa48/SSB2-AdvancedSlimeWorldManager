package me.aglerr.ssbslimeworldmanager.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.handlers.ProvidersManager;
import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.provider.SlimeWorldProvider;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class SuperiorListener implements Listener {

    private final SSBSlimeWorldManager plugin;
    public SuperiorListener(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSuperiorSkyblockInit(PluginInitializeEvent event){
        // Get the providers manager
        ProvidersManager providersManager = event.getPlugin().getProviders();
        // Set the world provider to this plugin
        providersManager.setWorldsProvider(new SlimeWorldProvider(plugin.getSlimeUtils(), plugin.getTaskManager()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDelete(IslandDisbandEvent event){
        SlimeUtils slimeUtils = plugin.getSlimeUtils();
        // Delete all island worlds
        for (World.Environment environment : World.Environment.values()) {
            slimeUtils.deleteWorld(event.getIsland(), environment);
        }
    }

}
