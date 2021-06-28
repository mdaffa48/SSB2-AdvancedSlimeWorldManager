package me.aglerr.ssbslimeworldmanager.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.handlers.ProvidersManager;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.provider.SlimeWorldProvider;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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
        providersManager.setWorldsProvider(new SlimeWorldProvider(plugin.getSlimeUtils(), plugin.getTaskManager(), plugin.getCacheManager()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDelete(IslandDisbandEvent event){
        SlimeUtils slimeUtils = plugin.getSlimeUtils();
        // Teleport player first so the world will properly get deleted
        for(SuperiorPlayer superiorPlayer : event.getIsland().getAllPlayersInside()){
            Player player = superiorPlayer.asPlayer();
            player.teleport(SSBSlimeWorldManager.spawnLocation);
        }
        // Need to delay this after the player are being teleported
        // otherwise the world will not be deleted if player still inside the world
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            long time = System.currentTimeMillis();
            slimeUtils.deleteWorld(event.getIsland());
            long timePassed = System.currentTimeMillis() - time;
            System.out.println("[SlimeWorldManager] Successfully deleted island {islandUUID} in {ms}ms"
                    .replace("{islandUUID}", event.getIsland().getUniqueId().toString())
                    .replace("{ms}", timePassed + ""));
        }, 20L);
    }

}
