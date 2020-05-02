package me.uniodex.uniolobi.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.uniodex.uniolobi.Main;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class NPCListeners implements Listener {

	private Main plugin;

	public NPCListeners(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCClick(NPCClickEvent event)
	{
		NPC npc = event.getNPC();
		Integer npcId = npc.getId();
		Player p = event.getClicker();

		if (plugin.getManager().npcCommands.containsKey(npcId)) {
			p.performCommand(plugin.getManager().npcCommands.get(npcId));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCLeftClick(NPCLeftClickEvent event)
	{
		NPC npc = event.getNPC();
		Integer npcId = npc.getId();
		Player p = event.getClicker();

		if (plugin.getManager().npcCommands.containsKey(npcId)) {
			p.performCommand(plugin.getManager().npcCommands.get(npcId));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCRightClick(NPCRightClickEvent event)
	{
		NPC npc = event.getNPC();
		Integer npcId = npc.getId();
		Player p = event.getClicker();

		if (plugin.getManager().npcCommands.containsKey(npcId)) {
			p.performCommand(plugin.getManager().npcCommands.get(npcId));
		}
	}
}
