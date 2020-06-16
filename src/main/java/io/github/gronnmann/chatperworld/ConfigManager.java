package io.github.gronnmann.chatperworld;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	private ConfigManager(){}
	
	private static File configF, groupsF;
	private static FileConfiguration config, groups;
	
	public static void setup(Plugin p){
		File dataFolder = p.getDataFolder();
		if (!dataFolder.exists()){
			if (!dataFolder.mkdir()){
				System.out.println("[ChatPerWorld] Failed to create files folder. Plugin exiting");
				//Bukkit.getPluginManager().disablePlugin(p);
			}
		}
		
		configF = new File(p.getDataFolder(), "config.yml");
		groupsF = new File(p.getDataFolder(), "groups.yml");
		
		if (!configF.exists()){
			p.saveDefaultConfig();
		}
		
		if (!groupsF.exists()){
			try{
				groupsF.createNewFile();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("[ChatPerWorld] Failed to create groups.yml. Plugin exiting");
				Bukkit.getPluginManager().disablePlugin(p);
			}
		}
		
		config = YamlConfiguration.loadConfiguration(configF);
		groups = YamlConfiguration.loadConfiguration(groupsF);
		
		
		save();
		
	}
	
	public static void save(){
		try {
			config.save(configF);
			groups.save(groupsF);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[ChatPerWorld] Failed saving files.");
		}
	}
	
	
	
	public static FileConfiguration getConfig(){
		return config;
	}
	
	public static FileConfiguration getGroups(){
		return groups;
	}
}
