package me.aglerr.ssbslimeworldmanager;

import me.aglerr.ssbslimeworldmanager.listeners.SuperiorListener;
import me.aglerr.ssbslimeworldmanager.managers.CacheManager;
import me.aglerr.ssbslimeworldmanager.managers.TaskManager;
import me.aglerr.ssbslimeworldmanager.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SSBSlimeWorldManager extends JavaPlugin {

    private final SlimeUtils slimeUtils = new SlimeUtils();
    private final TaskManager taskManager = new TaskManager(this);
    private final CacheManager cacheManager = new CacheManager(slimeUtils);
    public static Location spawnLocation;

    public static String FILE_TYPE;

    @Override
    public void onEnable() {
        // Initialize config.yml
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        // Initialize file type
        FILE_TYPE = this.getConfig().getString("file-type");
        // Initialize the slime utils
        this.slimeUtils.initialize();
        // Register listener
        this.registerListeners();
        // Initialize cache manager
        this.cacheManager.initialize();
        // Initialize location
        Bukkit.getScheduler().runTask(this, this::initializeLocation);
    }

    @Override
    public void onDisable() {
        this.slimeUtils.unloadAllWorlds();
    }

    private void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SuperiorListener(this), this);
        Bukkit.getScheduler().runTask(this, () ->
                pm.registerEvents(cacheManager, this));
    }

    private void initializeLocation(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2");
        String locationInString = plugin.getConfig().getString("spawn.location").replace(" ", "");
        String[] locationSection = locationInString.split(",");

        World world = Bukkit.getWorld(locationSection[0]);
        double x = Double.parseDouble(locationSection[1]);
        double y = Double.parseDouble(locationSection[2]);
        double z = Double.parseDouble(locationSection[3]);
        float yaw = (float) Double.parseDouble(locationSection[4]);
        float pitch = (float) Double.parseDouble(locationSection[5]);

        spawnLocation = new Location(world, x, y, z, yaw, pitch);
    }

    public SlimeUtils getSlimeUtils(){
        return this.slimeUtils;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
