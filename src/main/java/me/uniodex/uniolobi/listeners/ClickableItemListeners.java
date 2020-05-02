package me.uniodex.uniolobi.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.utils.Utils;

public class ClickableItemListeners implements Listener {

	private Main plugin;

	public ClickableItemListeners(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		p.getInventory().clear();
		p.updateInventory();

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.getInventory().setItem(0, plugin.getManager().clickableItemManager.getServerSelectorItem());
				p.getInventory().setItem(1, plugin.getManager().clickableItemManager.getProfileItem(p.getName()));
				if (p.hasPermission("uniolobi.fly")) {
					p.getInventory().setItem(7, plugin.getManager().clickableItemManager.getFlyToggler(p));
				}
				p.getInventory().setItem(8, plugin.getManager().clickableItemManager.getGameMarketItem());
				p.updateInventory();
			}
		}, 1L);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.PHYSICAL)) return;
		Player p = e.getPlayer();

		if(Utils.compareItem(p.getItemInHand(), plugin.getManager().clickableItemManager.getServerSelectorItem())) {
			e.setCancelled(true);
			p.performCommand("server");
			return;
		}

		if(Utils.isSimilar(p.getItemInHand(), plugin.getManager().clickableItemManager.getProfileItem(p.getName()))) {
			e.setCancelled(true);
			p.openInventory(plugin.getManager().menuManager.getProfile(p));
			return;
		}

		if(Utils.isSimilar(p.getItemInHand(), plugin.getManager().clickableItemManager.getFlyToggler(p))) {
			e.setCancelled(true);
			p.performCommand("fly");
			return;
		}

		if(Utils.compareItem(p.getItemInHand(), plugin.getManager().clickableItemManager.getGameMarketItem())) {
			e.setCancelled(true);
			p.openInventory(plugin.getManager().menuManager.getMarket());
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getSlotType().equals(SlotType.QUICKBAR)) return;
		Player p = (Player) e.getWhoClicked();

		if(Utils.compareItem(e.getCurrentItem(), plugin.getManager().clickableItemManager.getServerSelectorItem())) {
			e.setCancelled(true);
			p.closeInventory();
			p.performCommand("server");
			return;
		}

		if(Utils.isSimilar(e.getCurrentItem(), plugin.getManager().clickableItemManager.getProfileItem(p.getName()))) {
			e.setCancelled(true);
			p.openInventory(plugin.getManager().menuManager.getProfile(p));
			return;
		}

		if(Utils.isSimilar(e.getCurrentItem(), plugin.getManager().clickableItemManager.getFlyToggler(p))) {
			e.setCancelled(true);
			p.closeInventory();
			p.performCommand("fly");
			return;
		}

		if(Utils.compareItem(e.getCurrentItem(), plugin.getManager().clickableItemManager.getGameMarketItem())) {
			e.setCancelled(true);
			p.openInventory(plugin.getManager().menuManager.getMarket());
			return;
		}
	}
}
