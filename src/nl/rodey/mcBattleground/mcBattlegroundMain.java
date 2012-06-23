package nl.rodey.mcBattleground;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class mcBattlegroundMain extends JavaPlugin {
	public World world;
	public final Map<String, Boolean> playerhasjoined = new HashMap<String, Boolean>();
	public final Map<String,Integer> playerlist = new HashMap<String,Integer>();
	public final Map<String,Integer> playerkills = new HashMap<String,Integer>();
	public final Map<String,Integer> playerdeaths = new HashMap<String,Integer>();
	public final Map<String,Location> playerbedspawn = new HashMap<String,Location>();
	public final Map<String,ItemStack[]> playerdeathitems = new HashMap<String,ItemStack[]>();
	public final Map<String,ItemStack[]> playerdeatharmor = new HashMap<String,ItemStack[]>();
	public final Map<Location, String> objectivelocations = new HashMap<Location, String>();
	public final Map<String, String> objectivestates = new HashMap<String, String>();
	public Integer RedCount = 0;
	public Integer BlueCount = 0;
	public final Rectangle RedSpawn = new Rectangle(488,359,8,11);
	public final Rectangle BlueSpawn = new Rectangle(488,470,8,11);
	public Location RedPoint = null;
	public Location BluePoint = null;
	public Location RedBed = null;
	public Location BlueBed = null;
	public Location ReturnSpawn = null;
	public Boolean GameInProgress = true;
	public Boolean PlayersKeepItems = true;
	public Boolean Enabled = false;
	public Boolean PlayersKeepLevel = true;
	public String WorldName = "world_conquest";
	public String PrefixPluginName = ChatColor.DARK_PURPLE+"[CONQUEST]"+ChatColor.WHITE;
	public Boolean RedReady = false;
	public Boolean BlueReady = false;
	public MultiverseCore MVCorePlugin = null;
	public PluginManager pm = null;

	private EntityListener entListener = new EntityListener(this);
	private WorldListener woListener = new WorldListener(this);
	
	//Red is 1
	//Blue is 2
	
    public void onEnable() {
    	
    	FileConfiguration config = this.getConfig();
    	this.Enabled = config.getBoolean("Enabled", false);
    	config.set("Enabled", this.Enabled);
    	this.WorldName = config.getString("WorldName", this.WorldName);
    	config.set("WorldName", this.WorldName);
    	this.PlayersKeepItems = config.getBoolean("PlayersKeepItems", true);
    	config.set("PlayersKeepItems", this.PlayersKeepItems);
    	this.PlayersKeepLevel = config.getBoolean("PlayersKeepLevel", true);
    	config.set("PlayersKeepLevel", this.PlayersKeepLevel);
    	this.saveConfig();
    	
		this.pm = this.getServer().getPluginManager();
    	
    	if (this.Enabled) {

    		this.pm.registerEvents(new PluginListener(this, entListener, woListener), this);
		
    	} else {
    		System.out.println("mcBattleground must have Enabled set to true in the config.yml file!");
    	}
    }
    
    public void onDisable() {
    	this.getServer().unloadWorld(this.world, false);
    }
}
