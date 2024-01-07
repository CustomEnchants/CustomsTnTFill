package CustomsTnTFill.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileUtil {
	
	public File conf;
	
	public int xradius;
	public int yradius;
	public int zradius;
	
	public ArrayList<String> unableToFillDispensers = new ArrayList<>();
	public ArrayList<String> filledDispensers = new ArrayList<>();
	
	public ArrayList<String> noTnTInInventory = new ArrayList<>();
	public ArrayList<String> noDispensersFound = new ArrayList<>();
	
	public String tntFillPermission;
	public String tntFillCreativePermission;
	public ArrayList<String> noPermission = new ArrayList<>();

		
	private String fixColour(String input) {
		return ChatColor.translateAlternateColorCodes('&',input);	
	}

	private ArrayList<String> fixColours(List<String> input) {
		ArrayList<String> result = new ArrayList<>();
		input.forEach(string -> result.add(fixColour(string)));
		return result;
	}
	
	public void loadValues(){
		FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
		xradius = config.getInt("Radius.X");
		yradius = config.getInt("Radius.Y");
		zradius = config.getInt("Radius.Z");
		unableToFillDispensers = fixColours(config.getStringList("Messages.unable-to-fill"));
		filledDispensers = fixColours(config.getStringList("Messages.filled-tnt"));
		noTnTInInventory = fixColours(config.getStringList("Messages.no-tnt-in-inventory"));
		noDispensersFound = fixColours(config.getStringList("Messages.no-dispensers-found"));
		noPermission = fixColours(config.getStringList("Messages.no-permission"));
		tntFillPermission = config.getString("Options.permission");
		tntFillCreativePermission = config.getString("Options.creative-permission");
	}
	
	public void setup(File dir){
		if(!dir.exists()){
			dir.mkdirs();	
		}
		conf = new File(dir + File.separator + "Config.yml");
		if(!conf.exists()){
			FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
			ConfigurationSection radius = config.createSection("Radius");
			radius.set("X",24);
			radius.set("Y",16);
			radius.set("Z",24);
			ConfigurationSection messages = config.createSection("Messages");

			messages.set("unable-to-fill", Collections.singletonList("&6&lCustoms&a&lTnTFill &cWe were unable to fill your dispensers"));
			messages.set("filled-tnt", Collections.singletonList("&6&lCustoms&a&lTnTFill &aYou have filled your Dispensers"));
			messages.set("no-tnt-in-inventory", Collections.singletonList("&6&lCustoms&a&lTnTFill &cYou do not have tnt in your inventory !"));
			messages.set("no-dispensers-found", Collections.singletonList("&6&lCustoms&a&lTnTFill &cDid not detect any dispensers near your location !"));
			messages.set("no-permission", Collections.singletonList("&6&lCustoms&a&lTnTFill &cYou do not have permission for this command !."));

			ConfigurationSection options = config.createSection("Options");
			options.set("permission","CustomEnchants.TnTFill");
			options.set("creative-permission","CustomEnchants.TnTFill.Creative");
			try{
				config.save(conf);	
			}catch(Exception e){
				Bukkit.getLogger().log(Level.SEVERE,e.getLocalizedMessage());
			}
		}
		loadValues();
	}
}