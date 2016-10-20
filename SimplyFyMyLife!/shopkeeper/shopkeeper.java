package me.onno204.shopkeeper;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class shopkeeper extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Inventory kitsinv;	
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!" + ", Created By onno204!");
		PluginManager pm = this.getServer().getPluginManager();
        Bukkit.getPluginManager().registerEvents(this, this);
		pm.addPermission(new Premissions().bad);
		kitsinv = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Select a kit");
		
	}

	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + "Has Been Disabled!");
		getServer().getPluginManager();
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(sender instanceof Player){
			Player player = (Player)sender;
			//
			if(label.equalsIgnoreCase("tdm")){
				CommandMethods.SetPass(player, command, label, args);
			}else if (label.equalsIgnoreCase("tdm1")){
				CommandMethods.SetPass(player, command, label, args);
			}
			//
			} return true;}
	
	
	@EventHandler
	public void invclick(PlayerMoveEvent e){ WalkEvent.walk(e); }
	
	public Configuration config = getConfig();
	public static void saveconfig(){saveconfig();}
	t
	
}
