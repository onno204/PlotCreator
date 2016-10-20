package me.onno204.shopkeeper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CommandMethods {
	
	@SuppressWarnings("deprecation")
	public static void onno204cmd(Player player, Command command, String label, String[] args){
		String title = ChatColor.DARK_BLUE + "[" + ChatColor.DARK_AQUA + "PassTrough" + ChatColor.DARK_BLUE + "]" + ChatColor.YELLOW;
		
		player.sendMessage("" + args.length);
		if(args.length == 0){
			player.sendMessage("" + title + ChatColor.RED + "Pleas Use: </passtrough [1m] [1h]>");
		}else if(args.length == 1){
			player.sendMessage("hahahah");
		}
		

		try {config.save(file2); } catch (IOException e) {e.printStackTrace();}
	}
	
	
	
	
}
