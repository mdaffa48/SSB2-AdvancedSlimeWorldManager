package me.aglerr.ssbslimeworldmanager.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeChunkSection;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class SlimeUtils {

    private final Map<String, SlimeWorld> islandWorlds = new HashMap<>();

    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;

    private final SSBSlimeWorldManager plugin;
    public SlimeUtils(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    public void initialize(){
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        String finalFileType = SSBSlimeWorldManager.FILE_TYPE == null ? "file" : SSBSlimeWorldManager.FILE_TYPE;
        this.slimeLoader = slimePlugin.getLoader(finalFileType);
    }

    public void unloadAllWorlds(){
        try{
            this.slimeLoader.listWorlds().forEach(worldName -> {
                if(this.isIslandsWorld(worldName) && Bukkit.getWorld(worldName) != null)
                    this.unloadWorld(worldName);
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public SlimeWorld loadAndGetWorld(Island island, World.Environment environment){
        return this.loadAndGetWorld(island.getUniqueId(), environment);
    }

    public SlimeWorld loadAndGetWorld(UUID islandUUID, World.Environment environment){
        return this.loadAndGetWorld(getWorldName(islandUUID, environment), environment);
    }

    public SlimeWorld loadAndGetWorld(String worldName, World.Environment environment){
        SlimeWorld slimeWorld = this.islandWorlds.get(worldName);
        if(slimeWorld == null){
            try {
                if(this.slimeLoader.worldExists(worldName)){
                    slimeWorld = this.slimePlugin.loadWorld(this.slimeLoader, worldName, false, this.defaultSlimePropertyMap(environment));
                } else {
                    slimeWorld = this.slimePlugin.createEmptyWorld(this.slimeLoader, worldName, false, this.defaultSlimePropertyMap(environment));
                }
                this.islandWorlds.put(worldName, slimeWorld);
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }
        if(Bukkit.getWorld(worldName) == null){
            this.slimePlugin.generateWorld(slimeWorld);
        }
        return slimeWorld;
    }

    public void deleteWorld(Island island, World.Environment environment){
        String worldName = getWorldName(island, environment);
        this.unloadWorld(worldName);
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

    private SlimePropertyMap defaultSlimePropertyMap(World.Environment environment){
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        slimePropertyMap.setValue(SlimeProperties.ENVIRONMENT, environment.name().toLowerCase());
        return slimePropertyMap;
    }

}
