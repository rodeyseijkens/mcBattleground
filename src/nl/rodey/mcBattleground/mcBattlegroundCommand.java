package nl.rodey.mcBattleground;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mcBattlegroundCommand implements CommandExecutor {
	private final mcBattlegroundMain plugin;
	private final EntityListener entListener;
	
	public mcBattlegroundCommand(mcBattlegroundMain plugin, EntityListener entListener){
		this.plugin = plugin;
		this.entListener = entListener;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {		
		
		if (sender instanceof Player){
			if (!((Player) sender).getWorld().getName().equals(plugin.WorldName)){
				sender.sendMessage(plugin.PrefixPluginName + " You must be in the mcBattleground world to do this.");
				return true;
			}
			if (split.length == 0){
				return false;
			}
			if (split[0].equalsIgnoreCase("join")){
				if (!plugin.playerlist.containsKey(sender.getName())){
					Integer jointeam = 0;
					Player player = (Player) sender;
					player.setGameMode(GameMode.SURVIVAL);
					player.getInventory().setArmorContents(null);
					player.setHealth(20);
					player.setFoodLevel(20);
					if (split.length==1) {
						if (plugin.BlueCount < plugin.RedCount){
							jointeam = 2;
						} else if (plugin.BlueCount > plugin.RedCount){
							jointeam = 1;
						} else {
							jointeam = 1;
						}
					} else {
						if (player.hasPermission("mcBattleground.chooseteam")) {
							if (split[1].toLowerCase().equals("blue")) {
								jointeam = 2;
							} else if (split[1].toLowerCase().equals("red")) {
								jointeam = 1;
							} else {
								player.sendMessage(plugin.PrefixPluginName + " What team is that? Please choose either "+ChatColor.RED+"red"+ChatColor.RESET+" or "+ChatColor.BLUE+"blue.");
								return true;
							}
						} else {
							player.sendMessage(plugin.PrefixPluginName + " You do not have permission to choose a team.");
							return true;
						}
					}
					if (plugin.playerhasjoined.containsKey(player.getName())){
						player.sendMessage(plugin.PrefixPluginName + " You have previously joined this round before, so you do not get starting items.");
					} else {
						player.getInventory().clear();
						player.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
						//player.getInventory().addItem(new ItemStack(Material.RAW_CHICKEN, 1));
						//player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
					}
					plugin.playerkills.put(player.getName(),0);
					plugin.playerdeaths.put(player.getName(),0);
					plugin.playerbedspawn.put(player.getName(),player.getBedSpawnLocation());
					if (jointeam == 2){
						plugin.BlueCount++;
						plugin.playerlist.put(player.getName(), 2);
						player.teleport(plugin.BluePoint);
						player.setBedSpawnLocation(plugin.BlueBed);
						plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " " + player.getName() + " has joined team "+ChatColor.BLUE+"Blue.");
					} else if (jointeam == 1){
						plugin.RedCount++;
						plugin.playerlist.put(player.getName(), 1);
						player.teleport(plugin.RedPoint);
						player.setBedSpawnLocation(plugin.RedBed);
						plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " " + player.getName() + " has joined team "+ChatColor.RED+"Red.");
					}
					plugin.playerhasjoined.put(player.getName(), true);
				} else {
					sender.sendMessage(plugin.PrefixPluginName + " You're already on a team!");
				}
			} else if (split[0].equalsIgnoreCase("leave")){
				Player player = (Player) sender;
				
				// Teleport back from the place the player teleported from to this world.
				player.teleport(entListener.getPlayerTeleportLoc(player));
			} else if (split[0].equalsIgnoreCase("restart")){
				if (sender.hasPermission("mcBattleground.restart")){
					if (!plugin.GameInProgress){
						sender.sendMessage(plugin.PrefixPluginName + " A game is aready in progress! There is no need to reset the map!");
					} else {
						for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
							player.teleport(entListener.getPlayerTeleportLoc(player));
							/*
							player.teleport(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
							player.setBedSpawnLocation(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
							player.getInventory().clear();
							player.getInventory().setArmorContents(null);
							player.setHealth(20);
							player.setFoodLevel(20);
							*/
						}
						for (String value : plugin.objectivelocations.values()){
				    		plugin.objectivestates.put(value, null);
				    	}
						plugin.playerhasjoined.clear();
						plugin.playerlist.clear();
						plugin.playerkills.clear();
						plugin.playerdeaths.clear();
						plugin.playerdeathitems.clear();
						plugin.playerdeatharmor.clear();
						plugin.getServer().broadcastMessage(plugin.PrefixPluginName + " mcBattleground world is being restarted!");
						//MVWorldManager MVWM = plugin.MVCorePlugin.getMVWorldManager();
						//MVWM.unloadWorld(plugin.WorldName);
						//MVWM.loadWorld(plugin.WorldName);
				    	//plugin.RedPoint.getChunk().load();
				    	//plugin.BluePoint.getChunk().load();
					}
				} else {
					sender.sendMessage(plugin.PrefixPluginName + " You do not have permission to reset the map.");
				}
			} else if (split[0].equalsIgnoreCase("score")){
				for (String name : plugin.playerlist.keySet()){
					String message = "";
					if (plugin.playerlist.get(name)==1){
						message = ChatColor.RED.toString();
					} else if (plugin.playerlist.get(name)==2){
						message = ChatColor.BLUE.toString();
					}
					message += name + "    Kills: ";
					message += plugin.playerkills.get(name).toString() + "    Deaths: ";
					message += plugin.playerdeaths.get(name).toString();
					sender.sendMessage(message);
				}
			} else {
				return false;
			}
		}
		
		return true;
    }
}
