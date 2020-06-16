package io.github.gronnmann.chatperworld;

import java.util.ArrayList;

import org.bukkit.World;

public class Group {
	
	private ArrayList<String> worlds;
	private int id;
	
	
	public Group(int id){
		worlds = new ArrayList<String>();
		this.id = id;
	}
	
	public void addWorld(World w){
		try{
			if (worlds.contains(w.getName()))return;
			worlds.add(w.getName());
			System.out.println(w.getName());
		}catch(Exception e){}
		
	}
	
	public void addWorld(String world){
		if (worlds.contains(world))return;
		worlds.add(world);
	}
	
	public void removeWorld(World w){
		if (!worlds.contains(w.getName()))return;
		worlds.remove(w.getName());
	}
	
	public void removeWorld(String w){
		if (!worlds.contains(w))return;
		worlds.remove(w);
	}
	
	public ArrayList<String> getWorlds(){
		return worlds;
	}
	
	public int getID(){
		return id;
	}
	
	public boolean containsWorld(String w){
		if (worlds.contains(w))return true;
		else return false;
	}
	
	public boolean checkIfContainsBothWorlds(World w1, World w2){
		if (worlds.contains(w1.getName()) && worlds.contains(w2.getName()))return true;
		else return false;
	}
}
