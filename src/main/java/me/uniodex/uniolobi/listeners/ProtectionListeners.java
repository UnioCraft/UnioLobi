package me.uniodex.uniolobi.listeners;

import me.uniodex.uniolobi.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ProtectionListeners implements Listener {

	private Main plugin;

	public ProtectionListeners(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public boolean isPlayerBypassing(Player p) {
		if (p.hasPermission("uniolobi.bypass") || p.isOp()) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;

		Player player = event.getPlayer();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;

		Player player = event.getPlayer();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) return;
		if (event.getSpawnReason().equals(SpawnReason.NATURAL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (!isPlayerBypassing(player)) {
				event.setCancelled(true);
			}
		}else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.isCancelled()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) return;
		if (event.getClickedInventory() == null) return;
		Player player = (Player) event.getWhoClicked();
		if (!isPlayerBypassing(player)) {
			if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled()) return;
		Player p = e.getPlayer();
		if(p.hasPermission("uniolobi.unblockcmds")) {
			return;
		}

		if(!plugin.getConfig().getStringList("allowedCommands").contains(e.getMessage().split(" ")[0].replace("/", "").toLowerCase())) {
			e.setCancelled(true);
			p.sendMessage(plugin.getConfig().getString("Messages.Blocked-Command"));
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.isCancelled()) return;
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getRemover() instanceof Player)) {
			event.setCancelled(true);
			return;
		}

		Player player = (Player) event.getRemover();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		if (event.isCancelled()) return;

		Player player = event.getPlayer();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickUpItems(PlayerPickupItemEvent event) {
		if (event.isCancelled()) return;

		Player player = event.getPlayer();
		if (!isPlayerBypassing(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		e.setCancelled(true);
	}
}
