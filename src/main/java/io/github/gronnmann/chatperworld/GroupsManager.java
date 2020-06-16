package main.java.io.github.gronnmann.chatperworld;

import java.util.ArrayList;

public class GroupsManager {
	private GroupsManager(){
	}
	private static GroupsManager manager = new GroupsManager();
	public static GroupsManager getManager(){
		return manager;
	}
	
	public static ArrayList<Group> groups;
	
	
	
	public void setup(){
		groups = new ArrayList<Group>();
		this.loadGroups();
		
	}
	
	public Group createGroup(){
		Group newG = new Group(this.getNextAvaibleID());
		groups.add(newG);
		return newG;
	}
	
	public Group createGroup(int id){
		Group newG = new Group(id);
		groups.add(newG);
		return newG;
	}
	
	public int getNextAvaibleID(){
		if (groups.isEmpty()){
			return 1;
		}
		
		int greatestID = 1;
		
		for (Group g : groups){
			if (g.getID() > greatestID){
				greatestID = g.getID();
			}
		}
		
		return greatestID+1;
			
	}
	
	public Group getGroup(int id){
		for (Group g : groups){
			if (g.getID() == id){
				return g;
			}
		}
		
		return null;
	}
	
	public void loadGroups(){
		if (!ConfigManager.getGroups().contains("groups"))return;
		for (String groups : ConfigManager.getGroups().getConfigurationSection("groups").getKeys(false)){
			int id = Integer.parseInt(groups);
			this.createGroup(id);
			for (String worlds : ConfigManager.getGroups().getString("groups."+id).split(",")){
				if (!this.getGroup(id).containsWorld(worlds)){
					this.getGroup(id).addWorld(worlds);
				}
			}
		}
	}
	
	public void saveGroups(){
		ConfigManager.getGroups().set("groups", null);
		for (Group g : groups){
			StringBuilder sl = new StringBuilder();
			for (String worlds : g.getWorlds()){
				sl.append(worlds+",");
			}
			
			ConfigManager.getGroups().set("groups."+g.getID(), sl.toString());
			
		}
		
		ConfigManager.save();
	}
	
	public boolean groupExists(int id){
		for (Group g : groups){
			if (id == g.getID())return true;
		}
		return false;
	}
	
	public void removeGroup(Group g){
		groups.remove(g);
	}
	
	public void removeGroup(int id){
		groups.remove(this.getGroup(id));
	}
	
	public ArrayList<Group> getGroups(){
		return groups;
	}
	
}
