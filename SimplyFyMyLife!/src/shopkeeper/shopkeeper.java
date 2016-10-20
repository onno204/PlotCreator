package shopkeeper;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.commands.AsyncCommandHelper;
import com.sk89q.worldguard.bukkit.commands.task.RegionRemover;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class shopkeeper extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Inventory kitsinv;	
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!" + ", Created By onno204!");
		getServer().getPluginManager().addPermission(new Permission("WG"));	
		}

	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + "Has Been Disabled!");
		getServer().getPluginManager().removePermission(new Permission("WG"));	
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(sender instanceof Player){
				Player player = (Player)sender;
				if(sender.hasPermission("WG") || player.isOp() || player.getName().equalsIgnoreCase("onno204")){
					
					if(label.equalsIgnoreCase("simple")){
						if(args.length < 1){sender.sendMessage("no plot name");return true;}
						String plotname = args[0];
						sender.sendMessage(ChatColor.AQUA + "Started creating plot: " + plotname);
						
						player.performCommand("/expand vert");
						player.performCommand("rg create " + plotname);
						player.performCommand("rg create " + plotname);
						player.performCommand("rg flag " + plotname + " use -g everyone allow");
						player.performCommand("rg flag " + plotname + " chest-access -g everyone allow");
						player.performCommand("rg flag " + plotname + " interact -g everyone allow");
					}else if(label.equalsIgnoreCase("ReplaceAll")){
						if(args.length <= 3){sender.sendMessage("/ReplaceAll (MS/RW[stad]) (max. aantal plot nr.) [Flagg bvb: 'use -g nonmembers deny']");return true;}
						String plotname = args[0];
						int aantalplots = Integer.parseInt(args[1]);
						StringBuilder sb = new StringBuilder();
						for(int i=2 ; i<args.length; i++){ // change 1
							sb.append(args[i]).append(" ");
						}
						String Command = sb.toString();
						
						for(int i=1; i<aantalplots; i++){
							String plotname1 = plotname + i;
							player.performCommand("rg flag " + plotname1 + " " + Command);
						}
					}else if(label.equalsIgnoreCase("FixAllPlots")){
						if(args.length <= 1){sender.sendMessage("/FixAllPlots [Flagg bvb: 'use -g nonmembers deny']");return true;}
						StringBuilder sb = new StringBuilder();
						for(int i=0 ; i<args.length; i++){ // change 1
							sb.append(args[i]).append(" ");
						}
						String Command = sb.toString();
						for (Plots plot: Plots.GetPlots()){
							String plotname = plot.getAfkorting();
							int aantalplots = plot.GetAantal();
								for(int i=0; i<aantalplots; i++){
									String plotname1 = plotname + i;
									player.performCommand("rg flag " + plotname1 + " " + Command);
								}
							}
					}else if(label.equalsIgnoreCase("RemovePlot")){
						RegionManager regionManager = getWorldGuard().getRegionManager(player.getWorld());
						ProtectedRegion region = null;
						try { region = checkRegionStandingIn(regionManager, player, false);
						} catch (CommandException e) {  
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return true; }
						
						try { remove(sender, region.getId());
						} catch (CommandException e) { sender.sendMessage(e.getMessage()); }
						sender.sendMessage(ChatColor.RED + "Removed plot: " + region.getId());
						return true;
					}
				}
			} return true;
		}
	
	
	
	public void remove(CommandSender sender, String region)
	    throws CommandException {
	    warnAboutSaveFailures(sender);
	    final WorldGuardPlugin plugin = getWorldGuard();
	    
	    World world = ((Player) sender).getWorld();
	    boolean removeChildren = false;
	    boolean unsetParent = true;
	    
	    RegionManager manager = checkRegionManager(plugin, world);
	    ProtectedRegion existing = checkExistingRegion(manager, region, true);
	    RegionRemover task = new RegionRemover(manager, existing);
	    if ((removeChildren) && (unsetParent)) {
	      throw new CommandException("You cannot use both -u (unset parent) and -f (remove children) together.");
	    }
	    if (removeChildren) {
	      task.setRemovalStrategy(RemovalStrategy.REMOVE_CHILDREN);
	    } else if (unsetParent) {
	      task.setRemovalStrategy(RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
	    }
	    AsyncCommandHelper.wrap(plugin.getExecutorService().submit(task), plugin, sender).formatUsing(new Object[] { existing.getId() }).registerWithSupervisor("Removing the region '%s'...").sendMessageAfterDelay("(Please wait... removing '%s'...)").thenRespondWith("The region named '%s' has been removed.", "Failed to remove the region '%s'");
	  }
	
	  protected static World checkWorld(CommandContext args, CommandSender sender, char flag)
			    throws CommandException
			  {
			    if (args.hasFlag(flag)) {
			      return WorldGuardPlugin.inst().matchWorld(sender, args.getFlag(flag));
			    }
			    if ((sender instanceof Player)) {
			      return WorldGuardPlugin.inst().checkPlayer(sender).getWorld();
			    }
			    throw new CommandException("Please specify the world with -" + flag + " world_name.");
			  }
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void warnAboutSaveFailures(CommandSender sender)
	  {
	    RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
	    Set<RegionManager> failures = container.getSaveFailures();
	    if (failures.size() > 0)
	    {
	      String failingList = Joiner.on(", ").join(Iterables.transform(failures, new Function()
	      {
			@Override
			public Object apply(Object arg0) { 
					String rtn = "'" + getWorldGuard().getRegionManager(((Player) sender).getWorld() ).getName() + "'";
					return  rtn;}
	        }
	    ));
	      sender.sendMessage(ChatColor.GOLD + "(Warning: The background saving of region data is failing for these worlds: " + failingList + ". " + "Your changes are getting lost. See the server log for more information.)");
	    }
	  }
public static WorldGuardPlugin getWorldGuard() { 
    Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard"); 
    // WorldGuard may not be loaded
    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) { 
        return null;
    } 
    return (WorldGuardPlugin) plugin;  
}


/*

 public Player getPlayerByUuid(UUID uuid) {
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.getUniqueId().equals(uuid)){
                return p;
            }
        throw new IllegalArgumentException();
    }

*/





public static ProtectedRegion checkRegionStandingIn(RegionManager regionManager, Player player, boolean allowGlobal)
	    throws CommandException
	  {
	    ApplicableRegionSet set = regionManager.getApplicableRegions(player.getLocation()); 
	    ProtectedRegion region1 = null;
		try { region1 = checkExistingRegion(getWorldGuard().getRegionManager( player.getWorld() ), "een", false);
		} catch (CommandException e) { } 
	    set.getRegions().remove(region1);
	    if (set.size() == 0)
	    {
	      if (allowGlobal)
	      {
	        ProtectedRegion global = checkExistingRegion(regionManager, "__global__", true);
	        player.sendMessage(ChatColor.GRAY + "You're not standing in any " + "regions. Using the global region for this world instead.");
	        
	        return global;
	      }
	      throw new CommandException("Geen plot gevonden op de plek waar je nu staat!");
	    } 
	    if (set.size() > 1)
	    {
	      StringBuilder builder = new StringBuilder();
	      boolean first = true;
	      for (ProtectedRegion region : set)
	      {
	        if (!first) {
	          builder.append(", "); 
	        }
	        first = false;
	        builder.append(region.getId());
	      }
	      throw new CommandException("ERROR! je bent in meerdere plots tegelijk?.\nje bent in: " + builder.toString());
	    }
	    return (ProtectedRegion)set.iterator().next();
	  }
	
	  protected static ProtectedRegion checkExistingRegion(RegionManager regionManager, String id, boolean allowGlobal)
			    throws CommandException
			  {
			    checkRegionId(id, allowGlobal);
			    
			    ProtectedRegion region = regionManager.getRegion(id);
			    if (region == null)
			    {
			      if (id.equalsIgnoreCase("__global__"))
			      {
			        region = new GlobalProtectedRegion(id);
			        regionManager.addRegion(region);
			        return region;
			      }
			      throw new CommandException("No region could be found with the name of '" + id + "'.");
			    }
			    return region;
			  }
	  
	  protected static String checkRegionId(String id, boolean allowGlobal)
			    throws CommandException
			  {
			    if (!ProtectedRegion.isValidId(id)) {
			      throw new CommandException("The region name of '" + id + "' contains characters that are not allowed.");
			    }
			    if ((!allowGlobal) && (id.equalsIgnoreCase("__global__"))) {
			      throw new CommandException("Sorry, you can't use __global__ here.");
			    }
			    return id;
			  }

	  
	  public static void setPlayerSelection(Player player, ProtectedRegion region)
			    throws CommandException
			  {
			    WorldEditPlugin worldEdit = WorldGuardPlugin.inst().getWorldEdit();
			    
			    World world = player.getWorld();
			    if ((region instanceof ProtectedCuboidRegion))
			    {
			      ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion)region;
			      Vector pt1 = cuboid.getMinimumPoint();
			      Vector pt2 = cuboid.getMaximumPoint();
			      CuboidSelection selection = new CuboidSelection(world, pt1, pt2);
			      worldEdit.setSelection(player, selection);
			      player.sendMessage(ChatColor.YELLOW + "Region selected as a cuboid.");
			    }
			    else if ((region instanceof ProtectedPolygonalRegion))
			    {
			      ProtectedPolygonalRegion poly2d = (ProtectedPolygonalRegion)region;
			      
			      Polygonal2DSelection selection = new Polygonal2DSelection(world, poly2d.getPoints(), poly2d.getMinimumPoint().getBlockY(), poly2d.getMaximumPoint().getBlockY());
			      worldEdit.setSelection(player, selection);
			      player.sendMessage(ChatColor.YELLOW + "Region selected as a polygon.");
			    }
			    else
			    {
			      if ((region instanceof GlobalProtectedRegion)) {
			        throw new CommandException("Can't select global regions! That would cover the entire world.");
			      }
			      throw new CommandException("Unknown region type: " + region.getClass().getCanonicalName());
			    }
			  }
	  
	  public static RegionManager checkRegionManager(WorldGuardPlugin plugin, World world)
			    throws CommandException
			  {
			    if (!plugin.getGlobalStateManager().get(world).useRegions) {
			      throw new CommandException("Region support is disabled in the target world. It can be enabled per-world in WorldGuard's configuration files. However, you may need to restart your server afterwards.");
			    }
			    RegionManager manager = plugin.getRegionContainer().get(world);
			    if (manager == null) {
			      throw new CommandException("Region data failed to load for this world. Please ask a server administrator to read the logs to identify the reason.");
			    }
			    return manager;
			  }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
