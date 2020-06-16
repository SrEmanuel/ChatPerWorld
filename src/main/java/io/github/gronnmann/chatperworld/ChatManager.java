package io.github.gronnmann.chatperworld;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatManager implements Listener{
	
	private static ArrayList<String> spies = new ArrayList<String>();
	
	public static ArrayList<Player> getReceivers(Player p){
		ArrayList<Player> receivers = new ArrayList<Player>();
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			if (p.getWorld().equals(oPl.getWorld())){
				receivers.add(oPl);
			}
			
			for (Group g : GroupsManager.getManager().getGroups()){
				if (g.checkIfContainsBothWorlds(p.getWorld(), oPl.getWorld())){
					if (!receivers.contains(oPl))receivers.add(oPl);
				}
			}
			
		}
		
		return receivers;
	}
	
	
	public static void addSpy(String name){
		spies.add(name);
	}
	
	public static void removeSpy(String name){
		spies.remove(name);
	}
	
	public static boolean isSpy(String name){
		if (spies.contains(name))return true;
		else return false;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		e.getRecipients().clear();
		e.getRecipients().addAll(this.getReceivers(e.getPlayer()));
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			if (spies.contains(oPl.getName())){
				if (!this.getReceivers(e.getPlayer()).contains(oPl)){
					oPl.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfig().getString("spy_format"))
							.replaceAll("%WORLD%", e.getPlayer().getWorld().getName()).replaceAll("%PLAYER%", e.getPlayer().getName())
							.replaceAll("%MESSAGE%", e.getMessage()));
				}
				
			}
		}
		
	}
}
