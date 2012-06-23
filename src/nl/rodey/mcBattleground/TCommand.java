package nl.rodey.mcBattleground;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.apache.commons.lang.StringUtils;

public class TCommand implements CommandExecutor {
	private final mcBattlegroundMain plugin;
	
	public TCommand(mcBattlegroundMain plugin){
		this.plugin = plugin;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (sender instanceof Player){
			if (!((Player) sender).getWorld().getName().equals(plugin.WorldName)){
				sender.sendMessage("You must be in the Battleground world to do this.");
				return true;
			}
			if (plugin.playerlist.containsKey(sender.getName())){
				Integer team = plugin.playerlist.get(sender.getName());
				String message = ChatColor.GREEN + sender.getName() + ": " + StringUtils.join(split, " ");
				for (String name : plugin.playerlist.keySet()){
					if (plugin.playerlist.get(name)==team){
						plugin.getServer().getPlayer(name).sendMessage(message);
					}
				}
			} else {
				sender.sendMessage("You cannot team chat if you're not on a team.");
			}
			return true;
		} else {
			return false;
		}
    }
}
