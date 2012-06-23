package nl.rodey.mcBattleground;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.Listener;
import org.bukkit.material.Wool;

public class BlockListener implements Listener {
	mcBattlegroundMain plugin = null;

	
	public BlockListener(mcBattlegroundMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		if (plugin.GameInProgress && event.getBlock().getLocation().getWorld().getName().equals(plugin.WorldName)){
			Location exloc = event.getBlock().getLocation();
			for (Location objloc : plugin.objectivelocations.keySet()){
				int evtRadius = 5;
				
				double evtRadiusxX = objloc.getX() + evtRadius;
				double evtRadiusXx = objloc.getX() - evtRadius;
				
				double evtRadiusyY = objloc.getY() + evtRadius;
				double evtRadiusYy = objloc.getY() - evtRadius;
				
				double evtRadiuszZ = objloc.getZ() + evtRadius;
				double evtRadiusZz = objloc.getZ() - evtRadius;
				
				if( evtRadiusxX > exloc.getX() && exloc.getX() > evtRadiusXx && evtRadiusyY > exloc.getY() && exloc.getY() > evtRadiusYy && evtRadiuszZ > exloc.getZ() && exloc.getZ() > evtRadiusZz )
				{
					String objectivename = plugin.objectivelocations.get(objloc);
										
					Block capturedBlock = event.getBlock().getRelative(BlockFace.UP, 1);
					
					//System.out.println(event.getBlock().getX() + " # " + event.getBlock().getY() + " # " + event.getBlock().getZ() + " = " + capturedBlock.getType().toString());
					
					if(capturedBlock.getType() == Material.WOOL){						
						Wool wool = new Wool(capturedBlock.getType(), capturedBlock.getData());
						if(wool.getColor() == DyeColor.RED)
						{
							if (plugin.objectivestates.get(objectivename) != "red"){
								for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
									player.sendMessage(plugin.PrefixPluginName + " " + ChatColor.RED+"Red Team has captured " + ChatColor.WHITE + objectivename +"!");
								}
								
								plugin.objectivestates.put(objectivename, "red");
							}
						}
						else if(wool.getColor() == DyeColor.BLUE)
						{
							if (plugin.objectivestates.get(objectivename) != "blue"){
								for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
									player.sendMessage(plugin.PrefixPluginName + " " + ChatColor.BLUE+"Blue Team has captured " + ChatColor.WHITE + objectivename +"!");
								}
								
								plugin.objectivestates.put(objectivename, "blue");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}
}
