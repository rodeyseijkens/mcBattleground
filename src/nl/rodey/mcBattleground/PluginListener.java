package nl.rodey.mcBattleground;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class PluginListener implements Listener {

	private mcBattlegroundMain plugin;

	public PluginListener(mcBattlegroundMain plug, EntityListener entListener, WorldListener woListener){
		
		this.plugin = plug;
		
			if (plugin.getServer().getWorld(plugin.WorldName)==null){
	    		System.out.println("Specified world does not exist!");
	    		plugin.Enabled = false;
	    		return;
	    	}

	    	plugin.world = plugin.getServer().getWorld(plugin.WorldName);	
	    	
	    	//define locations
	    	plugin.RedPoint = new Location(plugin.world,492.,36.,311.);
	    	plugin.BluePoint = new Location(plugin.world,492.,36.,529.);
	    	
	    	plugin.RedBed = new Location(plugin.world,482.,35.,307.);
	    	plugin.BlueBed = new Location(plugin.world,482.,35.,528.);
	    	
	    	plugin.ReturnSpawn = new Location(plugin.getServer().getWorld("world"),-421.,64.,-39.);
	    	
	    	plugin.RedPoint.getChunk().load();
	    	plugin.BluePoint.getChunk().load();
	    	plugin.objectivelocations.put(new Location(plugin.world,465,48,326), "Red Lighthouse");
	    	plugin.objectivelocations.put(new Location(plugin.world,463,48,513), "Blue Lighthouse");
	    	plugin.objectivelocations.put(new Location(plugin.world,447,52,420), "Central Tower");
	    	plugin.objectivelocations.put(new Location(plugin.world,539,52,373), "North Tower");
	    	plugin.objectivelocations.put(new Location(plugin.world,539,52,466), "South Tower");
	    	plugin.objectivelocations.put(new Location(plugin.world,493,15,435), "Hill Door");
	    	plugin.objectivelocations.put(new Location(plugin.world,493,15,427), "Tower Door");
	    	plugin.objectivelocations.put(new Location(plugin.world,493,15,416), "Dungeon Door");
	    	plugin.objectivelocations.put(new Location(plugin.world,490,18,403), "Hill Door");
	    	plugin.objectivelocations.put(new Location(plugin.world,499,18,403), "Tower Door");
	    	plugin.objectivelocations.put(new Location(plugin.world,508,18,406), "Dungeon Door");
	    	
	    	for (String value : plugin.objectivelocations.values()){
	    		plugin.objectivestates.put(value, null);
	    	}
	    	
	    	plugin.world.setAutoSave(false);
	    	plugin.world.setPVP(true);
	        plugin.pm.registerEvents(new EntityListener(plugin), plugin);
	        plugin.pm.registerEvents(new WorldListener(plugin), plugin);
	        plugin.pm.registerEvents(new BlockListener(plugin), plugin);
	        plugin.getCommand("mcbg").setExecutor(new mcBattlegroundCommand(plugin, entListener));
	        plugin.getCommand("t").setExecutor(new TCommand(plugin));
			System.out.println("["+plugin.getDescription().getName()+"] version "+plugin.getDescription().getVersion()+" is enabled");
		
		
	}
}
