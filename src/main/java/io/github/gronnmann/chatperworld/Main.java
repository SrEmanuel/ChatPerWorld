package io.github.gronnmann.chatperworld;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	@Override
	public void onEnable(){
		ConfigManager.setup(this);
		GroupsManager.getManager().setup();
		GUI.getInstance().setup();
		
		
		Bukkit.getPluginManager().registerEvents(new ChatManager(), this);
		Bukkit.getPluginManager().registerEvents(GUI.getInstance(), this);
		
		
		CommandManager cmdMng = new CommandManager();
		this.getCommand("chatperworld").setExecutor(cmdMng);
	}
	
	
	@Override
	public void onDisable(){
		GroupsManager.getManager().saveGroups();
		ConfigManager.save();
	}
	
}
