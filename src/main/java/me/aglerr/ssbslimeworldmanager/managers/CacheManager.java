package me.aglerr.ssbslimeworldmanager.managers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import me.aglerr.ssbslimeworldmanager.provider.SlimeWorldProvider;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CacheManager implements Listener {

    private final Map<String, SlimeWorld> cachedWorld = new HashMap<>();
    private final Set<String> loadedStatus = new HashSet<>();

    private final SlimeUtils slimeUtils;
    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;
    public CacheManager(SlimeUtils slimeUtils){
        this.slimeUtils = slimeUtils;
    }

    public void initialize(){
        this.slimePlugin = slimeUtils.getSlimePlugin();
        this.slimeLoader = slimeUtils.getSlimeLoader();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);

        Island island = superiorPlayer.getIsland();
        // Return if player doesn't have island
        if(island == null) return;
        // Get the world's name
        String worldName = slimeUtils.getWorldName(island, World.Environment.NORMAL);
        // If the world is loading, stop the code
        if(this.loadedStatus.contains(worldName)) return;
        // Return if the world is cached
        if(this.cachedWorld.containsKey(worldName)) return;
        // Cache the world use async
        System.out.println("[SuperiorSkyblock-ASWM] Trying to load " + player.getName() + " island...");
        this.loadWorldAsync(worldName);
    }

    private void loadWorldAsync(String worldName) {
        CompletableFuture.supplyAsync(() -> {
            SlimeWorld slimeWorld = null;
            try{
                slimeWorld = slimePlugin.loadWorld(slimeLoader, worldName, false, slimeUtils.defaultSlimePropertyMap());
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return slimeWorld;
        }).whenComplete((slimeWorld, throwable) -> {
            System.out.println("[SuperiorSkyblock-ASWM] Successfully cached world " + worldName);
            this.cachedWorld.put(worldName, slimeWorld);
            this.loadedStatus.remove(worldName);
        });
    }

    public SlimeWorld createWorld(Island island){
        return createWorld(island.getUniqueId());
    }

    public SlimeWorld createWorld(UUID islandUUID){
        return this.createWorld(slimeUtils.getWorldName(islandUUID, World.Environment.NORMAL));
    }

    public SlimeWorld createWorld(String worldName){
        SlimeWorld slimeWorld = this.cachedWorld.get(worldName);

        if(slimeWorld != null) return slimeWorld;

        try{
            slimeWorld = slimePlugin.createEmptyWorld(slimeLoader, worldName, false, slimeUtils.defaultSlimePropertyMap());
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

        if(Bukkit.getWorld(worldName) == null){
            slimePlugin.generateWorld(slimeWorld);
        }
        return slimeWorld;
    }

}
