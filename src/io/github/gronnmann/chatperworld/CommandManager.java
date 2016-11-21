package io.github.gronnmann.chatperworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandManager implements CommandExecutor{
	
	private GroupsManager mng;
	
	public CommandManager(){
		mng = GroupsManager.getManager();
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args) {
		
		
		if (args.length == 0){
			syntax(sender);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("global")||args[0].equalsIgnoreCase("group")||args[0].equalsIgnoreCase("groups")||
				args[0].equalsIgnoreCase("gui")||args[0].equalsIgnoreCase("spy")){}else{
					syntax(sender);
				}
		
		if (!hasPermission(sender, "chatperworld" + args[0])){
			return true;
		}
		
		if (args[0].equalsIgnoreCase("global")){
			
			if (args.length < 2){
				sender.sendMessage(ChatColor.RED + "Usage: /chatperworld global [message]");
				return true;
			}
			StringBuilder sl = new StringBuilder();
			for (int i = 1; i < args.length; i++){
				sl.append(args[i] + " ");
			}
			
			String msg = sl.toString();
			for (Player oPl : Bukkit.getOnlinePlayers()){
				oPl.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfig().getString("global_format"))
						.replaceAll("%PLAYER%", sender.getName())
						.replaceAll("%MESSAGE%", msg));
			}
		}
		
		
		if (args[0].equalsIgnoreCase("group")){
			///chatperworld group [id] [add/del] [worldname]
			
			if (!(args.length == 4)){
				sender.sendMessage(ChatColor.RED + "Usage: /chatperworld group [id] [add/del] [worldname]");
				return true;
			}
			
			int id = -1;
			try{
				id = Integer.parseInt(args[1]);
			}catch(Exception e){
				sender.sendMessage(ChatColor.RED + "ID has be a number.");
				return true;
			}
			
			if (!mng.groupExists(id)){
				sender.sendMessage(ChatColor.RED + "No group with ID " + id + " found.");
				return true;
			}
			
			Group g = mng.getGroup(id);
			
			if (args[2].equalsIgnoreCase("add")){
				if (g.containsWorld(args[3])){
					sender.sendMessage(ChatColor.RED + "The group with the ID " + id + " already contains the world " + args[3]);
					return true;
				}
				g.addWorld(args[3]);
				sender.sendMessage(ChatColor.GREEN + "Successfully added world " + ChatColor.YELLOW + args[3] 
						+ ChatColor.GREEN + " to group with ID " + ChatColor.YELLOW + id);
				return true;
				
			}else if (args[2].equalsIgnoreCase("del")){
				if (g.containsWorld(args[3])){
					g.removeWorld(args[3]);
					sender.sendMessage(ChatColor.GREEN + "Successfully deleted world " + ChatColor.YELLOW + args[3] 
							+ ChatColor.GREEN + " from group with ID " + ChatColor.YELLOW + id);
					return true;
				}
				sender.sendMessage(ChatColor.RED + "The group with the ID " + id + " doesn't contain the world " + args[3]);
				return true;
				
			}else{
				sender.sendMessage(ChatColor.RED + "Usage: /chatperworld group [id] [add/del] [worldname]");
			}
			
		}
		
		
		
		//Groups Command
		if (args[0].equalsIgnoreCase("groups")){
			if (args.length == 3){
				int id = 0;
				try{
					id = Integer.parseInt(args[2]);
				}catch(Exception e){
					sender.sendMessage(ChatColor.RED + "ID has to be a number.");
					return true;
				}
				
				if (args[1].equalsIgnoreCase("add")){
					if (mng.groupExists(id)){
						sender.sendMessage(ChatColor.RED + "A group with the ID " + id + " already exists.");
					}else{
						mng.createGroup(id);
						sender.sendMessage(ChatColor.GREEN + "Created group with ID " + ChatColor.YELLOW + id);
						
					}
				}else if (args[1].equalsIgnoreCase("del")){
					if (mng.groupExists(id)){
						mng.removeGroup(id);
						sender.sendMessage(ChatColor.GREEN + "Successfully removed group with ID " + ChatColor.YELLOW + id);
					}else{
						sender.sendMessage(ChatColor.RED + "No group with ID " + id + " found.");
					}
				}
				
				else{
					sender.sendMessage(ChatColor.RED + "Usage: /chatperworld groups [add/del/list] [id if del]");
				}
			}else if (args.length == 2){
				if (args[1].equalsIgnoreCase("add")){
					int id = mng.createGroup().getID();
					sender.sendMessage(ChatColor.GREEN + "Created group with ID " + ChatColor.YELLOW + id);
						
				}else if (args[1].equals("list")){
					StringBuilder list = new StringBuilder();
					list.append(ChatColor.DARK_GREEN + "World groups:\n");
					for (Group g : mng.getGroups()){
						StringBuilder worlds =  new StringBuilder();
						for (String world : g.getWorlds()){
							worlds.append(world + ", ");
						}
						list.append(ChatColor.GOLD + ""+g.getID() + ChatColor.WHITE + " - " + ChatColor.YELLOW + worlds.toString() + "\n");
					}
					
					sender.sendMessage(list.toString());
					
					
					
				}
				
				else{
					sender.sendMessage(ChatColor.RED + "Usage: /chatperworld groups [add/del/list] [id if del]");
				}
			}else{
				sender.sendMessage(ChatColor.RED + "Usage: /chatperworld groups [add/del/list] [id if del]");
			}
		}
		
		
		if (args[0].equalsIgnoreCase("gui")){
			if (this.notPlayer(sender))return true;
			GUI.getInstance().openGUI(((Player)sender));
		}
		
		
		//Spy
		if (args[0].equalsIgnoreCase("spy")){
			if (notPlayer(sender))return true;
			if (ChatManager.isSpy(sender.getName())){
				ChatManager.removeSpy(sender.getName());
				sender.sendMessage(ChatColor.YELLOW + "" + "Spy mode is now " + ChatColor.RED + "DISABLED");
			}else{
				ChatManager.addSpy(sender.getName());
				sender.sendMessage(ChatColor.YELLOW + "" + "Spy mode is now " + ChatColor.GREEN + "ENABLED");
			}
			
		}
		
		
		
		return true;
	}
	
	public void syntax(CommandSender sender){
		if (!hasPermission(sender, "chatperworld.help"))return;
			
			sender.sendMessage(ChatColor.DARK_GREEN + "Avaible commands:");
			StringBuilder sl = new StringBuilder();
			if (sender.hasPermission("chatperworld.global")){
				sl.append(cmdDescriptionByID(0)+"\n");
			}if (sender.hasPermission("chatperworld.group")){
				sl.append(cmdDescriptionByID(1) + "\n");
			}if (sender.hasPermission("chatperworld.groups")){
				sl.append(cmdDescriptionByID(2)+"\n");
			}if (sender.hasPermission("chatperworld.gui")){
				sl.append(cmdDescriptionByID(3) + "\n");
			}if (sender.hasPermission("chatperworld.spy")){
				sl.append(cmdDescriptionByID(4));
			}
			
			sender.sendMessage(sl.toString());
	}
	
	public boolean notPlayer(CommandSender sender){
		if (!(sender instanceof Player)){
			sender.sendMessage("This command is for players only.");
			return true;
		}
		else return false;
		
	}
	
	public boolean hasPermission(CommandSender sender, String permission){
		if (!sender.hasPermission(permission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
			return false;
		}else return true;
	}
	
	private String cmdDescriptionByID(int id){
		switch(id){
		case 0:
			return
			ChatColor.YELLOW + "/chatperworld global [message]"
			+ ChatColor.WHITE + " - " 
			+ ChatColor.GOLD + "Sends a message to all worlds.";
		case 1:
			return
			ChatColor.YELLOW + "/chatperworld group [id] [add/del] [worldname]"
			+ ChatColor.WHITE + " - " 
			+ ChatColor.GOLD + "Adds or removes a world in a group.";
		case 2:
			return
			ChatColor.YELLOW + "/chatperworld groups [add/del/list] (id if del)"
			+ ChatColor.WHITE + " - " 
			+ ChatColor.GOLD + "Adds or removes a group, ID optional when creating.";
		case 3:
			return
			ChatColor.YELLOW + "/chatperworld gui"
			+ ChatColor.WHITE + " - " 
			+ ChatColor.GOLD + "Opens plugin GUI.";
		case 4:
			return
			ChatColor.YELLOW + "/chatperworld spy"
			+ ChatColor.WHITE + " - " 
			+ ChatColor.GOLD + "Allows you to see messages from all worlds.";
		default: return null;
		}
		
		
		
		
	}
	
	
	
	
}
