package CustomsTnTFill;

import CustomsTnTFill.Commands.TnTFill;
import CustomsTnTFill.Utils.FileUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomsTnTFillPlugin extends JavaPlugin {
	
	private static CustomsTnTFillPlugin instance;
	private FileUtil fileutil;
	
	public void onEnable(){
		instance = this;
		fileutil = new FileUtil();
		getFileUtil().setup(getDataFolder());
		getCommand("tntfill").setExecutor(new TnTFill());
	}
	
	public void onDisable() {
		instance = null;	
	}
	
	public static CustomsTnTFillPlugin getInstance(){
		return instance;
	}
	public FileUtil getFileUtil() {
		return fileutil;	
	}

}
