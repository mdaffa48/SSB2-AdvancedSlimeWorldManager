package me.aglerr.ssbslimeworldmanager.listeners;

import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.handlers.ProvidersManager;
import me.aglerr.ssbslimeworldmanager.SSBSlimeWorldManager;
import me.aglerr.ssbslimeworldmanager.provider.SlimeWorldProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InitializeListener implements Listener {

    private final SSBSlimeWorldManager plugin;
    public InitializeListener(SSBSlimeWorldManager plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSuperiorSkyblockInit(PluginInitializeEvent event){
        // Get the providers manager
        ProvidersManager providersManager = event.getPlugin().getProviders();
        // Set the world provider to this plugin
        providersManager.setWorldsProvider(new SlimeWorldProvider(plugin.getTaskManager(), plugin.getSlimeUtils()));
    }

}
