package nl.rodey.mcBattleground;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {
	mcBattlegroundMain plugin = null;

	
	public WorldListener(mcBattlegroundMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
		for (String value : plugin.objectivelocations.values()){
    		plugin.objectivestates.put(value, null);
    	}
		plugin.playerhasjoined.clear();
		plugin.playerlist.clear();
		plugin.playerkills.clear();
		plugin.playerdeaths.clear();
		plugin.playerdeathitems.clear();
		plugin.playerdeatharmor.clear();
		plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " mcBattleground world is restarting!");
	}
	
	@EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
		plugin.GameInProgress = true;
		
		plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " mcBattleground world has restarted!");
	}
}
