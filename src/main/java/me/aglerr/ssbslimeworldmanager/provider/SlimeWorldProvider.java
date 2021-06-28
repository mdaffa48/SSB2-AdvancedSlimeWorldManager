package me.aglerr.ssbslimeworldmanager.provider;

import com.bgsoftware.superiorskyblock.api.hooks.WorldsProvider;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.grinderwolf.swm.api.world.SlimeWorld;
import me.aglerr.ssbslimeworldmanager.managers.CacheManager;
import me.aglerr.ssbslimeworldmanager.managers.TaskManager;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class SlimeWorldProvider implements WorldsProvider {

    private final SlimeUtils slimeUtils;
    private final TaskManager taskManager;
    private final CacheManager cacheManager;
    public SlimeWorldProvider(SlimeUtils slimeUtils, TaskManager taskManager, CacheManager cacheManager){
        this.slimeUtils = slimeUtils;
        this.taskManager = taskManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public void prepareWorlds() {

    }

    @Override
    public World getIslandsWorld(Island island, World.Environment environment) {
        SlimeWorld slimeWorld = this.slimeUtils.loadAndGetWorld(island, World.Environment.NORMAL);
        this.taskManager.putAndGetTask(slimeWorld.getName()).updateLastTime();
        return Bukkit.getWorld(slimeWorld.getName());
    }

    @Override
    public boolean isIslandsWorld(World world) {
        return this.slimeUtils.isIslandsWorld(world.getName());
    }

    @Override
    public Location getNextLocation(Location previousLocation, int islandsHeight, int maxIslandSize, UUID islandOwner, UUID islandUUID) {
        SlimeWorld slimeWorld = this.cacheManager.createWorld(islandUUID);
        this.taskManager.putAndGetTask(slimeWorld.getName());
        return new Location(Bukkit.getWorld(slimeWorld.getName()), 0, islandsHeight, 0);
    }

    @Override
    public void finishIslandCreation(Location islandsLocation, UUID islandOwner, UUID islandUUID) {

    }

    @Override
    public void prepareTeleport(Island island, Location location, Runnable finishCallback) {
        if(!island.isSpawn()) {
            SlimeWorld slimeWorld = this.cacheManager.createWorld(island);
            this.taskManager.putAndGetTask(slimeWorld.getName()).updateLastTime();
        }
        finishCallback.run();
    }

    public boolean isNormalEnabled(){
        return true;
    }

    public boolean isNormalUnlocked(){
        return true;
    }

    @Override
    public boolean isNetherEnabled() {
        return false;
    }

    @Override
    public boolean isNetherUnlocked() {
        return false;
    }

    @Override
    public boolean isEndEnabled() {
        return false;
    }

    @Override
    public boolean isEndUnlocked() {
        return false;
    }
}
