package nl.rodey.mcBattleground;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EntityListener implements Listener {
	mcBattlegroundMain plugin;
	private Location playerTeleportLocation = null;
	
	public EntityListener(mcBattlegroundMain plugin){
		this.plugin = plugin;
	}
	

	@EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (!event.getEntity().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		
		if (event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.playerlist.remove(player.getName());
		plugin.playerkills.remove(player.getName());
		plugin.playerdeaths.remove(player.getName());
		if (!player.getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		if (!plugin.playerlist.containsKey( player.getName() ))
		{
			player.teleport(plugin.ReturnSpawn);
		}
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20);
		player.setFoodLevel(20);
    }
	
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		Location loc = event.getPlayer().getLocation();
		Integer x = loc.getBlockX();
		Integer z = loc.getBlockZ();
		String name = event.getPlayer().getName();
		event.getPlayer().setFoodLevel(20);
		if (plugin.playerlist.containsKey(name)){
			if (plugin.playerlist.get(name)==1){
				if (plugin.BlueSpawn.contains(x,z)){
					event.getPlayer().damage(7);
				}
			} else if (plugin.playerlist.get(name)==2){
				if (plugin.RedSpawn.contains(x,z)){
					event.getPlayer().damage(7);
				}
			}
		}
    }
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().getWorld().getName().equals(plugin.WorldName) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			String p1 = ((Player) event.getEntity()).getName();
			String p2 = ((Player) event.getDamager()).getName();
			if (plugin.playerlist.containsKey(p1) && plugin.playerlist.containsKey(p2)){
				if (plugin.playerlist.get(p1) == plugin.playerlist.get(p2)){
					event.setCancelled(true);
					 ((Player) event.getDamager()).sendMessage(plugin.PrefixPluginName + " " + p1 +" is in your team!");
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		plugin.playerlist.remove(player.getName());
		plugin.playerkills.remove(player.getName());
		plugin.playerdeaths.remove(player.getName());
		if (player.getWorld().getName().equals(plugin.WorldName)){
			
			player.teleport(getPlayerTeleportLoc(player));
		}
    }
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(plugin.WorldName)){
			
			return;
		}
		if (plugin.PlayersKeepItems) {
			DamageCause cause = event.getEntity().getLastDamageCause().getCause();
		
			if (cause.toString()== DamageCause.DROWNING.toString()) {
				event.getEntity().sendMessage("[CONQUEST] You died in the pirate cave, so you lost your items!");
			} else if (cause.toString()==DamageCause.LAVA.toString()){
				event.getEntity().sendMessage("[CONQUEST] You died in the lava cave, so you lost your items!");
			} else {
				plugin.playerdeathitems.put(event.getEntity().getName(),event.getEntity().getInventory().getContents());
				plugin.playerdeatharmor.put(event.getEntity().getName(),event.getEntity().getInventory().getArmorContents());
//				event.getEntity().getInventory().clear();
//				for (ItemStack e : event.getDrops()) {
//					System.out.println(e.getType().name());
//				}
//				System.out.println(event.getDrops().size());
				event.getDrops().clear();
			}
		}
		event.setKeepLevel(plugin.PlayersKeepLevel);
		String name = event.getEntity().getName();
		if (plugin.playerdeaths.containsKey(name)){
			plugin.playerdeaths.put(name,plugin.playerdeaths.get(name)+1);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		String name = event.getPlayer().getName();
		if (plugin.playerdeathitems.containsKey(name)){
			PlayerInventory inven = event.getPlayer().getInventory();
			Integer count = 0;
			for (ItemStack istack : plugin.playerdeathitems.get(name)){
				if (istack instanceof ItemStack){
					inven.setItem(count, istack);
				}
				count++;
			}
			//inven.setContents(plugin.playerdeathitems.get(name));
			inven.setArmorContents(plugin.playerdeatharmor.get(name));
			plugin.playerdeathitems.remove(name);
			plugin.playerdeatharmor.remove(name);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		if (!event.getEntity().getWorld().getName().equals(plugin.WorldName)){			
			return;
		}
		if (event.getEntityType() != EntityType.PLAYER) {
			if (event.getEntity().getKiller() instanceof Player){
				Player player = event.getEntity().getKiller();
				
				player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			}
			event.getDrops().clear();
		} else {
			if (event.getEntity().getKiller() instanceof Player){
				String name = event.getEntity().getKiller().getName();
				if (plugin.playerkills.containsKey(name)){
					plugin.playerkills.put(name,plugin.playerkills.get(name)+1);
				}
			}
			
		}
	}
	
	/*
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		String name = event.getPlayer().getName();
		if (plugin.playerlist.containsKey(name)){
			if (plugin.playerlist.get(name)==1){
				if (event.getItem().getLocation().distance(plugin.BluePoint) < 70.0){
					event.setCancelled(true);
				}
			} else if (plugin.playerlist.get(name)==2){
				if (event.getItem().getLocation().distance(plugin.RedPoint) < 70.0){
					event.setCancelled(true);
				}
			}
		}
	}
	*/
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()){
			
			if (event.getClickedBlock().getType() == Material.DISPENSER){
				event.setCancelled(true);
			} else if (event.getClickedBlock().getType() == Material.TRAP_DOOR){
				event.setCancelled(true);
			} else if (event.getClickedBlock().getType() == Material.LEVER){
				if (plugin.RedCount == 0 || plugin.BlueCount == 0){
					event.setCancelled(true);
					event.getPlayer().sendMessage(plugin.PrefixPluginName + " Waiting for opposing players to join.");
					event.getPlayer().sendMessage(plugin.PrefixPluginName + " " + ChatColor.BLUE +"Blue:" + ChatColor.WHITE + " " + plugin.BlueCount + ChatColor.RED +"  Red:" + ChatColor.WHITE + " " + plugin.RedCount);
				}
				else if (plugin.RedCount != plugin.BlueCount)
				{
					event.setCancelled(true);
					event.getPlayer().sendMessage(plugin.PrefixPluginName + " Waiting for opposing players to join.");
					event.getPlayer().sendMessage(plugin.PrefixPluginName + " " + ChatColor.BLUE +"Blue:" + ChatColor.WHITE + " " + plugin.BlueCount + ChatColor.RED +"  Red:" + ChatColor.WHITE + " " + plugin.RedCount);
				}
			} else if (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.SIGN)){
				
				Sign block = (Sign) event.getClickedBlock().getState();
				
				if(block.getLine(0).equalsIgnoreCase("[mcbg]"))
				{					
					String split[];
					split = new String[2];
					
					if(block.getLine(1).equalsIgnoreCase("join"))
					{
						split[0] = "join";
						
						if(block.getLine(2).equalsIgnoreCase("red") || block.getLine(2).equalsIgnoreCase("blue"))
						{
							split[1] = block.getLine(2).toString();

							event.getPlayer().performCommand("mcbg "+split[0]+" "+split [1]);
						}
						else
						{
							event.getPlayer().performCommand("mcbg "+split[0]);
						}
						
					}
					else if(block.getLine(1).equalsIgnoreCase("leave"))
					{
						split[0] = "leave";

						event.getPlayer().performCommand("mcbg "+split[0]+" "+split [1]);
					}
					else if(block.getLine(1).equalsIgnoreCase("score"))
					{
						split[0] = "score";

						event.getPlayer().performCommand("mcbg "+split[0]+" "+split [1]);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		if (plugin.GameInProgress && event.getLocation().getWorld().getName().equals(plugin.WorldName)){
			Location exloc = event.getLocation();
			for (Location objloc : plugin.objectivelocations.keySet()){
				int evtRadius = 10;
				
				double evtRadiusxX = objloc.getX() + evtRadius;
				double evtRadiusXx = objloc.getX() - evtRadius;
				
				double evtRadiusyY = objloc.getY() + evtRadius;
				double evtRadiusYy = objloc.getY() - evtRadius;
				
				double evtRadiuszZ = objloc.getZ() + evtRadius;
				double evtRadiusZz = objloc.getZ() - evtRadius;
				
				if( evtRadiusxX > exloc.getX() && exloc.getX() > evtRadiusXx && evtRadiusyY > exloc.getY() && exloc.getY() > evtRadiusYy && evtRadiuszZ > exloc.getZ() && exloc.getZ() > evtRadiusZz )
				{
					System.out.println("[CONQUEST] mcBattleground Explosion!");
					
					String objectivename = plugin.objectivelocations.get(objloc);
					if (plugin.objectivestates.get(objectivename) == null){

						for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
							player.sendMessage(plugin.PrefixPluginName + " " + objectivename + " has been destroyed.");
						}
						
						plugin.objectivestates.put(objectivename, "true");
						if (objectivename.equals("Red Lighthouse")){

							plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " " + ChatColor.BLUE+"Blue Team has won!");
							
							for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){								
								player.sendMessage(plugin.PrefixPluginName + " Teleporting back in 10 seconds!");
							}
							
							plugin.GameInProgress = false;
							
							// Teleport players back after 10 seconds
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() 
								{
									for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
										player.teleport(getPlayerTeleportLoc(player));
		    				   		}
								}
				    		}, 200L);
							
							plugin.GameInProgress = false;
						} else if (objectivename.equals("Blue Lighthouse")){
							
							plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " " + ChatColor.RED+"Red Team has won!");
							
							for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
								player.sendMessage(plugin.PrefixPluginName + " Teleporting back in 10 seconds!");
							}
							
							plugin.GameInProgress = false;
							
							// Teleport players back after 10 seconds
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() 
								{
									for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
										player.teleport(getPlayerTeleportLoc(player));
		    				   		}
								}
				    		}, 200L);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event){
		final String playerFromWorld = event.getFrom().getWorld().getName();
    	String playerToWorld = event.getTo().getWorld().getName();
		
    	if(playerToWorld.equals(plugin.WorldName) && !plugin.GameInProgress)
    	{
    		event.getPlayer().sendMessage(plugin.PrefixPluginName + " World not ready yet!");
			
    		event.setCancelled(true);
    	}
    	
		if (!playerFromWorld.equals(plugin.WorldName) && playerToWorld.equals(plugin.WorldName))
		{
			setPlayerTeleportLoc(event.getPlayer(), event.getFrom());
		}
		else if(playerFromWorld.equals(plugin.WorldName) && !playerToWorld.equals(plugin.WorldName))
		{
			if (plugin.playerlist.containsKey(event.getPlayer().getName())){
				if (plugin.playerlist.get(event.getPlayer().getName())==1){
					plugin.RedCount--;
				} else if (plugin.playerlist.get(event.getPlayer().getName())==2){
					plugin.BlueCount--;
				}

				plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " " + event.getPlayer().getName() + " left the battleground");
			}
			
			Player player = event.getPlayer();

			String name = player.getName();
			
			player.setBedSpawnLocation(plugin.playerbedspawn.get(name));
		
			plugin.playerbedspawn.remove(name);
			plugin.playerhasjoined.remove(name);
			plugin.playerlist.remove(name);
			plugin.playerdeaths.remove(name);
			plugin.playerkills.remove(name);
			plugin.playerdeathitems.remove(name);
			plugin.playerdeatharmor.remove(name);
		}
	}
	
	public void setPlayerTeleportLoc(Player player, Location fromLocation) 
	{				
		File locDataFolder = new File(plugin.getDataFolder().getAbsolutePath(), "playerLocations");
		locDataFolder.mkdirs();
		
		try {
			File playerLocFile = new File(locDataFolder , player.getName() + ".loc");
			playerLocFile.createNewFile();
	
			final BufferedWriter out = new BufferedWriter(new FileWriter(playerLocFile));

        	out.write(fromLocation.getWorld().getName()+"#"+fromLocation.getX()+"#"+fromLocation.getY()+"#"+fromLocation.getZ());
        	
			out.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Location getPlayerTeleportLoc(Player player) 
	{		
		File locDataFolder = new File(plugin.getDataFolder().getAbsolutePath(), "playerLocations");

		File playerLocFile = new File(locDataFolder , player.getName() + ".loc");
		
		try {
			
			final BufferedReader in = new BufferedReader(new FileReader(playerLocFile));

			String line;
			line = in.readLine();
			
			final String[] parts = line.split("#");
			in.close();		
			
			World world = plugin.getServer().getWorld(parts[0]);
			
			playerTeleportLocation = new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));		
			
			return playerTeleportLocation;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
