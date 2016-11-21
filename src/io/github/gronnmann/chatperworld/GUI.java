package io.github.gronnmann.chatperworld;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI implements Listener{
	private GUI() {}
	private static GUI gui = new GUI();
	public static GUI getInstance(){
		return gui;
	}
	
	private Inventory menu, settings, groups, groupSettings, worlds;
	private ArrayList<Inventory> invs = new ArrayList<Inventory>();
	private ItemStack back;
	
	private enum GroupAction {ADD_WORLD, REMOVE_WORLD};
	private HashMap<String, GroupAction> groupActions = new HashMap<String, GroupAction>(); 
	
	
	public void setup(){
		menu = Bukkit.createInventory(null, 27, "ChatPerWorld GUI");
		
		settings = Bukkit.createInventory(null, 27, "ChatPerWorld Settings");
		
		groups = Bukkit.createInventory(null, 54, "ChatPerWorld Groups");
		groupSettings = Bukkit.createInventory(null, 27, "ChatPerWorld Group Settings ID");
		
		worlds = Bukkit.createInventory(null, 54, "ChatPerWorld Group Edit ID");
		
		invs.add(settings);invs.add(groupSettings);
		
		back = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
		
		ItemStack fill = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)10);
		
		
		ItemMeta backMeta = back.getItemMeta();backMeta.setDisplayName(ChatColor.BOLD.toString() + ChatColor.RED + "BACK");
		back.setItemMeta(backMeta);
		
		for (Inventory i : invs){
			i.setItem(26, back);
		}
		
		//MENU ITEMS
		ItemStack groupsButton = new ItemStack(Material.GRASS);
		ItemMeta groupsMeta = groupsButton.getItemMeta();groupsMeta.setDisplayName(ChatColor.GOLD + "Groups");groupsButton.setItemMeta(groupsMeta);
		menu.setItem(13, groupsButton);
		
		//GROUPS MENU ITEMS
		ItemStack create = new ItemStack(Material.WOOL, 1, (byte)5);
		ItemStack delete = new ItemStack(Material.WOOL, 1, (byte)14);
		ItemMeta createMeta = create.getItemMeta();createMeta.setDisplayName(ChatColor.GREEN + "CREATE GROUP");create.setItemMeta(createMeta);
		ItemMeta deleteMeta = delete.getItemMeta();deleteMeta.setDisplayName(ChatColor.RED + "DELETE GROUP");delete.setItemMeta(deleteMeta);
		
		groups.setItem(53, back);
		groups.setItem(45, create);
		for (int x = 46; x <=52;x++){
			groups.setItem(x, fill);
		}
		this.renderGroupsInventory();
		
		//GROUPS MENU
		
		ItemStack add = new ItemStack(Material.WOOL, 1, (byte)5);
		ItemStack remove = new ItemStack(Material.WOOL, 1, (byte)14);
		ItemMeta addMeta = add.getItemMeta();addMeta.setDisplayName(ChatColor.GREEN + "ADD WORLD");add.setItemMeta(addMeta);
		ItemMeta removeMeta = remove.getItemMeta();removeMeta.setDisplayName(ChatColor.RED + "REMOVE WORLD");remove.setItemMeta(removeMeta);
		
		groupSettings.setItem(12, add);
		groupSettings.setItem(14, remove);
		
		groupSettings.setItem(18, delete);
		
		
		//Worlds Menu
		worlds.setItem(53, back);
		worlds.setItem(46, delete);
		
		for (int x = 45; x <=52;x++){	
			worlds.setItem(x, fill);
		}
		
		
		this.renderWorldsInventory();
	}
	
	private void renderGroupsInventory(){
		for (int i = 0; i<=44; i++){
			groups.setItem(i, new ItemStack(Material.AIR));
		}
		int slot = 0;
		for (Group g : GroupsManager.getManager().getGroups()){
			if (slot >= 45)return;
			ItemStack group = new ItemStack(Material.GRASS);
			ItemMeta groupMeta = group.getItemMeta();
			groupMeta.setDisplayName(ChatColor.GREEN + "" +g.getID());
			StringBuilder sl = new StringBuilder();
			for (String wN : g.getWorlds()){
				sl.append(wN + ", ");
			}
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Worlds in group:");
			lore.add(ChatColor.YELLOW + sl.toString());
			groupMeta.setLore(lore);
			group.setItemMeta(groupMeta);
			groups.setItem(slot, group);
			slot++;
		}
	}
	
	private void renderWorldsInventory(){
		for (int i = 0; i<=44; i++){
			worlds.setItem(i, new ItemStack(Material.AIR));
		}
		int slot = 0;
		for (World w : Bukkit.getWorlds()){
			if (slot >= 45)return;
			ItemStack world = new ItemStack(Material.AIR);
			if (w.getEnvironment().equals(Environment.NETHER)){
				world.setType(Material.NETHERRACK);
			}else if (w.getEnvironment().equals(Environment.THE_END)){
				world.setType(Material.ENDER_STONE);
			}else{
				world.setType(Material.GRASS);
			}
			
			
			ItemMeta worldMeta = world.getItemMeta();
			worldMeta.setDisplayName(ChatColor.GREEN + w.getName());
			world.setItemMeta(worldMeta);
			
			
			
			worlds.setItem(slot, world);
			slot++;
		}
	}
	
	private void launchGroupEdit(Player player, int groupID){
		this.renderWorldsInventory();
		Inventory wEdit = Bukkit.createInventory(null, 54, "ChatPerWorld Group Edit ID " + groupID);
		wEdit.setContents(worlds.getContents());
		player.openInventory(wEdit);
	}
	
	private void launchGroupSettings(Player player, int groupID){
		Inventory gSet = Bukkit.createInventory(null, 27, "ChatPerWorld Group Settings ID " + groupID);
		gSet.setContents(groupSettings.getContents());
		player.openInventory(gSet);
	}
	
	public void openGUI(Player player){
		player.openInventory(menu);
		
	}
	
	@EventHandler
	public void onInventoryPress(InventoryClickEvent e){
		if (!e.getInventory().getName().contains("ChatPerWorld"))return;
		if (e.getCurrentItem() == null)return;
		if (e.getCurrentItem().getItemMeta()==null)return;
		e.setCancelled(true);
		if (e.getClickedInventory().getName().contains("GUI")){
			//Menu
			if (e.getSlot() == 13){
				//Going to groups
				e.getWhoClicked().openInventory(groups);
			}
		}
		
		else if (e.getClickedInventory().getName().contains("Groups")){
			//Groups
			if (e.getSlot() == 53){
				//Back
				e.getWhoClicked().openInventory(menu);
			}else if (e.getSlot() == 45){
				//Create new group
				GroupsManager.getManager().createGroup();
				this.renderGroupsInventory();
			}else{
				if (e.getInventory().getItem(e.getSlot()).getType().equals(Material.STAINED_GLASS_PANE))return;
				ItemStack i = e.getInventory().getItem(e.getSlot());
				int id = Integer.parseInt(i.getItemMeta().getDisplayName().replaceAll(ChatColor.GREEN.toString(), ""));
				this.launchGroupSettings((Player) e.getWhoClicked(), id);
			}
		}
		
		else if (e.getClickedInventory().getName().contains("Settings")){
			//Group settings
			if (groupActions.containsKey(e.getWhoClicked().getName()))groupActions.remove(e.getWhoClicked().getName());
			if (e.getSlot() == 12){
				//Add world
				groupActions.put(e.getWhoClicked().getName(), GroupAction.ADD_WORLD);
			}else if (e.getSlot() == 14){
				//Remove world
				groupActions.put(e.getWhoClicked().getName(), GroupAction.REMOVE_WORLD);
			}else if (e.getSlot() == 18){
				int groupID = Integer.parseInt(e.getClickedInventory().getName().split(" ")[4]);
				GroupsManager.getManager().removeGroup(groupID);
				this.renderGroupsInventory();
				e.getWhoClicked().openInventory(groups);
				return;
				
			}
			else if (e.getSlot() == 26){
				this.renderGroupsInventory();
				e.getWhoClicked().openInventory(groups);
				return;
			}
			else return;
			this.launchGroupEdit(((Player)e.getWhoClicked()), Integer.parseInt(e.getClickedInventory().getName().split(" ")[4]));
		}
		
		else if (e.getClickedInventory().getName().contains("Edit")){
			
			//Add/remove world
			if (e.getSlot() == 53){
				//Back
				int id = Integer.parseInt(e.getClickedInventory().getName().split(" ")[4]);
				this.launchGroupSettings((Player) e.getWhoClicked(), id);
			}
			if (e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE))return;
			String world = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(ChatColor.GREEN.toString(), "");
			int gID = Integer.parseInt(e.getClickedInventory().getName().split(" ")[4]);
			if (!groupActions.containsKey(e.getWhoClicked().getName()))return;
			if (groupActions.get(e.getWhoClicked().getName()).equals(GroupAction.ADD_WORLD)){
				GroupsManager.getManager().getGroup(gID).addWorld(world);
			}else{
				GroupsManager.getManager().getGroup(gID).removeWorld(world);
			}
			
		}
	}
	
	
}
