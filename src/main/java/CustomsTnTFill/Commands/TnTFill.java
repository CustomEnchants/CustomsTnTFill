package CustomsTnTFill.Commands;

import java.util.ArrayList;

import CustomsTnTFill.CustomsTnTFillPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;



public class TnTFill implements CommandExecutor {

	private final CustomsTnTFillPlugin instance = CustomsTnTFillPlugin.getInstance();
	
	private ArrayList<Dispenser> dispensers(final Location loc,final int yradius,final int xradius,final int zradius){
		ArrayList<Dispenser> result = new ArrayList<>();
		World w = loc.getWorld();
		for(int x = loc.getBlockX() - xradius; x<loc.getBlockX()+xradius; x++){
			for(int z = loc.getBlockZ() - zradius; z<loc.getBlockZ()+zradius; z++){
				for(int y = loc.getBlockY() - yradius; y<loc.getBlockY()+yradius; y++){
					Block b = w.getBlockAt(x,y,z);
					if(b.getType() == Material.AIR) continue;
					if(b.getType() != Material.DISPENSER) continue;
					if(b.getState() instanceof Dispenser){
						result.add(((Dispenser)b.getState()));
					}
				}
			}
		}
		return result;
	}
	
	private int getTnTInInventory(Inventory inv){
		int result = 0;
		for(int i = 0; i<inv.getSize(); i++){
			if(inv.getItem(i) == null) continue;
			ItemStack is = inv.getItem(i);
			if(is.getType() == Material.AIR) continue;
			if(is.getType() != Material.TNT) continue;
			result += (is.getAmount());
		}
		return result;
	}
			
	public boolean isInventoryFull(Inventory inv){
		return inv.firstEmpty() == -1;
	}
	
	private void scanArea(final Player p,final Location loc,final int yradius,final int xradius,final int zradius){
        final ArrayList<Dispenser> dispensers = dispensers(loc,yradius,xradius,zradius);
		new BukkitRunnable(){
			public void run(){
				if(dispensers.isEmpty()){
				    instance.getFileUtil().noDispensersFound.forEach(p::sendMessage);
					cancel();
					return;
				}
				if(p.hasPermission(instance.getFileUtil().tntFillCreativePermission) && p.getGameMode() == GameMode.CREATIVE){
					dispensers.forEach(dispenser -> dispenser.getInventory().addItem(new ItemStack(Material.TNT,576)));
					dispensers.forEach(dispenser -> dispenser.update(true));
					instance.getFileUtil().filledDispensers.forEach(p::sendMessage);
					cancel();
					return;
				}
				new BukkitRunnable(){
					public void run(){
						int tntInInventory = getTnTInInventory(p.getInventory());
						while (tntInInventory >= 1) {
							int perdis = tntInInventory/dispensers.size();
							if (perdis > 64) perdis = 64;
							for(Dispenser dispenser : dispensers){
								for (int i = 0; i<=8; i++) {
									if((dispenser.getInventory().getItem(i) == null || dispenser.getInventory().getItem(i).getType() == Material.AIR || dispenser.getInventory().getItem(i).getAmount() != 64)) {
										if ((perdis > 0 && (dispenser.getInventory().getItem(i) == null || dispenser.getInventory().getItem(i).getType() == Material.AIR || dispenser.getInventory().getItem(i).getAmount() <= 64-perdis))) {
											if (dispenser.getInventory().getItem(i) == null || dispenser.getInventory().getItem(i).getType() == Material.AIR) {
											    dispenser.getInventory().setItem(i, new ItemStack(Material.TNT,perdis));
											} else {
											    dispenser.getInventory().addItem(new ItemStack(Material.TNT,perdis));
											}
											tntInInventory -= perdis;
											break;
										} else if ((tntInInventory >= 1) && (dispenser.getInventory().getItem(i) == null || dispenser.getInventory().getItem(i).getType() == Material.AIR  || dispenser.getInventory().getItem(i).getAmount() != 64)) {
											dispenser.getInventory().addItem(new ItemStack(Material.TNT,1));
											tntInInventory -=1;
											break;
										}
									}
								}
							}
							if (tntInInventory < 1) {
								break;
							}
						}
						p.getInventory().remove(Material.TNT);
						if (tntInInventory >=1) {
							p.getInventory().addItem(new ItemStack(Material.TNT,tntInInventory));
						}
						p.updateInventory();
						instance.getFileUtil().filledDispensers.forEach(p::sendMessage);
						cancel();
					}
				}.runTask(instance);
			}
		}.runTaskAsynchronously(instance);
		p.updateInventory();
	}

	public boolean onCommand(CommandSender cs, Command cmd, String lab, String[] args) {
		if(cmd.getName().equalsIgnoreCase("tntfill")){
			if(!(cs instanceof Player)) {
				return false;	
			}
			Player p = (Player) cs;
			if(!p.hasPermission(instance.getFileUtil().tntFillPermission)){
			    instance.getFileUtil().noPermission.forEach(p::sendMessage);
				return false;
			}
			scanArea(p,p.getLocation(),instance.getFileUtil().yradius,instance.getFileUtil().xradius,instance.getFileUtil().zradius);
			return false;
		}
		return false;
	}
}