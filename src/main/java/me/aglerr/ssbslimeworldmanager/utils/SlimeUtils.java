package me.aglerr.ssbslimeworldmanager.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.internal.com.mongodb.lang.Nullable;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import me.aglerr.ssbslimeworldmanager.ConfigValue;
import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.tasks.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class SlimeUtils {

    private final Map<String, SlimeWorld> islandWorlds = new HashMap<>();

    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;
    private TaskManager taskManager;

    public void initialize(TaskManager taskManager){
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.slimeLoader = slimePlugin.getLoader(ConfigValue.FILE_TYPE);
        this.taskManager = taskManager;
    }

    public void unloadAllWorlds(){
        try{
            this.islandWorlds.keySet().forEach(worldName -> {
                if(this.isIslandsWorld(worldName) && Bukkit.getWorld(worldName) != null){
                    this.unloadWorld(worldName);
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public SlimeWorld loadAndGetWorld(Island island, World.Environment environment){
        return loadAndGetWorld(island.getUniqueId(), environment);
    }

    public SlimeWorld loadAndGetWorld(UUID islandUUID, World.Environment environment){
        return loadAndGetWorld(getWorldName(islandUUID, environment), environment);
    }

    public SlimeWorld loadAndGetWorld(String worldName, World.Environment environment){
        SlimeWorld slimeWorld = islandWorlds.get(worldName);
        // If the slime world is not cached
        if(slimeWorld == null){
            // Try to create or load the world
            try {
                // World was found, load the world and cached it
                if (slimeLoader.worldExists(worldName)) {
                    slimeWorld = slimePlugin.loadWorld(slimeLoader, worldName, false, slimePropertyMap(environment));
                } else {
                    // If there is no world, create the world
                    slimeWorld = slimePlugin.createEmptyWorld(slimeLoader, worldName, false, slimePropertyMap(environment));
                }
                // Put the world to the cache
                islandWorlds.put(worldName, slimeWorld);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        // If there is no world with that name, generate the world
        if(Bukkit.getWorld(worldName) == null){
            slimePlugin.generateWorld(slimeWorld);
        }
        // Return the desired world
        return slimeWorld;
    }

    public void deleteWorld(Island island, World.Environment environment){
        String worldName = this.getWorldName(island, environment);
        if(islandWorlds.get(worldName) == null){
            return;
        }
        try {
            this.slimeLoader.deleteWorld(worldName);
            this.islandWorlds.remove(worldName);
            this.taskManager.stopTask(worldName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean isIslandsWorld(String worldName){
        String[] nameSections = worldName.split("_");
        try{
            return SuperiorSkyblockAPI.getGrid().getIslandByUUID(UUID.fromString(nameSections[1])) != null;
        }catch (Exception ex) {
            return false;
        }
    }

    public void unloadWorld(String worldName){
        SlimeWorld slimeWorld = this.islandWorlds.get(worldName);
        if(slimeWorld != null){
            try{
                byte[] serializedWorld = ((CraftSlimeWorld) slimeWorld).serialize();
                this.slimeLoader.saveWorld(slimeWorld.getName(), serializedWorld, true);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        if(Bukkit.getWorld(worldName) != null){
            Bukkit.unloadWorld(worldName, true);
        }
    }

    public String getWorldName(Island island, World.Environment environment){
        return getWorldName(island.getUniqueId(), environment);
    }

    public String getWorldName(UUID islandUUID, World.Environment environment){
        return "island_" + islandUUID + "_" + environment.name().toLowerCase();
    }

    private boolean isIslandWorldName(String worldName){
        String[] nameSections = worldName.split("_");
        try{
            UUID.fromString(nameSections[0]);
            World.Environment.valueOf(nameSections[1]);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    private SlimeWorld asyncLoadWorld(String worldName, World.Environment environment){
        final SlimeWorld[] slimeWorlds = new SlimeWorld[1];
        slimePlugin.asyncLoadWorld(slimeLoader, worldName, false, slimePropertyMap(environment)).thenAccept(world -> {
            if(world.isEmpty()){
                // Do something else
                throw new IllegalStateException("Failed to load world (" + worldName + ") on environment " + environment.name());
            }
            slimeWorlds[0] = world.get();
        });
        return slimeWorlds[0];
    }

    private SlimeWorld asyncCreateEmptyWorld(String worldName, World.Environment environment){
        final SlimeWorld[] slimeWorlds = new SlimeWorld[1];
        slimePlugin.asyncCreateEmptyWorld(slimeLoader, worldName, false, slimePropertyMap(environment)).thenAcceptAsync(world -> {
            if(world.isEmpty()){
                // Do something else
                throw new IllegalStateException("Failed to create an empty world (" + worldName + ") on environment " + environment.name());
            }
            slimeWorlds[0] = world.get();
        });
        return slimeWorlds[0];
    }

    public SlimePropertyMap slimePropertyMap(World.Environment environment){
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        slimePropertyMap.setValue(SlimeProperties.ENVIRONMENT, environment.name());
        return slimePropertyMap;
    }
}
