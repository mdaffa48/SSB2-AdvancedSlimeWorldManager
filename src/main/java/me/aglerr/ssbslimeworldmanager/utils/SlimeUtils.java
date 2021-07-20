package me.aglerr.ssbslimeworldmanager.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SlimeUtils {

    private final Map<String, SlimeWorld> islandWorlds = new HashMap<>();

    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;

    public void initialize(){
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        String finalFileType = SSBSlimeWorldManager.FILE_TYPE == null ? "file" : SSBSlimeWorldManager.FILE_TYPE;
        this.slimeLoader = slimePlugin.getLoader(finalFileType);
    }

    public void unloadAllWorlds(){
        try{
            this.islandWorlds.keySet().forEach(worldName -> {
                if(this.isIslandsWorld(worldName) && Bukkit.getWorld(worldName) != null)
                    this.unloadWorld(worldName);
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
                    slimeWorld = slimePlugin.loadWorld(slimeLoader, worldName, false, defaultSlimePropertyMap(environment));
                    // If there is no world, create the world
                } else {
                    slimeWorld = slimePlugin.createEmptyWorld(slimeLoader, worldName, false, defaultSlimePropertyMap(environment));
                }
                // Put the world to the cache
                islandWorlds.put(worldName, slimeWorld);
            }catch (Exception ex){
                throw new RuntimeException(ex);
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
        try {
            this.slimeLoader.deleteWorld(worldName);
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
        SlimeWorld slimeWorld = this.islandWorlds.remove(worldName);
        if(slimeWorld != null){
            try{
                byte[] serializedWorld = ((CraftSlimeWorld) slimeWorld).serialize();
                this.slimeLoader.saveWorld(slimeWorld.getName(), serializedWorld, true);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        Bukkit.unloadWorld(worldName, true);
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

    public SlimePropertyMap defaultSlimePropertyMap(World.Environment environment){
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        slimePropertyMap.setValue(SlimeProperties.ENVIRONMENT, environment.name());
        return slimePropertyMap;
    }
}
