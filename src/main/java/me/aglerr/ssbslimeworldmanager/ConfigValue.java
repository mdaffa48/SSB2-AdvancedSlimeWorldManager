package me.aglerr.ssbslimeworldmanager;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigValue {
    
    public static String FILE_TYPE;
    public static boolean NORMAL_ENABLED, NETHER_ENABLED, END_ENABLED;
    public static boolean NORMAL_UNLOCKED, NETHER_UNLOCKED, END_UNLOCKED;
    
    public static void initialize(FileConfiguration config){
        FILE_TYPE = config.getString("file-type", "file");

        NORMAL_ENABLED = config.getBoolean("worlds.normal.enabled", true);
        NORMAL_UNLOCKED = config.getBoolean("worlds.normal.unlock", true);

        NETHER_ENABLED = config.getBoolean("worlds.nether.enabled", true);
        NETHER_UNLOCKED = config.getBoolean("worlds.nether.unlock", true);

        END_ENABLED = config.getBoolean("worlds.end.enabled", true);
        END_UNLOCKED = config.getBoolean("worlds.end.unlock", false);
    }
    
}
