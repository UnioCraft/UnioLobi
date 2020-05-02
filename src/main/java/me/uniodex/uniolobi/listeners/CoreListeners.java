package me.uniodex.uniolobi.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.managers.Cooldown;
import me.uniodex.uniolobi.utils.Utils;
import net.blackscarx.betterchairs.ChairsPlugin;

public class CoreListeners implements Listener {

	private Main plugin;

	public CoreListeners(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();

		event.setJoinMessage("");

		// Prepare Player
		p.setMaxHealth(20);
		p.setHealth(p.getMaxHealth());
		p.setExp(0.0F);
		p.setSaturation(0F);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.setLevel(0);
		p.setGameMode(GameMode.SURVIVAL);

		if(p.getVehicle() != null) {
			p.getVehicle().eject();
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999999, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, 1));

		for (String message : plugin.getConfig().getStringList("Messages.MOTD")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', message).replace("%player%", plugin.getManager().playerManager.getDisplayName(p)));
		}

		//TODO UNCOMMENT FOR OPEN BOOK
		/*
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (!plugin.getManager().playerManager.bookOpens.containsKey(p.getName())) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ib open onjoin " + p.getName());
					plugin.getManager().playerManager.bookOpens.put(p.getName(), 1);
				}else {
					Integer current = plugin.getManager().playerManager.bookOpens.get(p.getName());
					if (current == 5) {
						plugin.getManager().playerManager.bookOpens.remove(p.getName());
						plugin.getManager().playerManager.bookOpens.put(p.getName(), 1);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ib open onjoin " + p.getName());
					} else {
						plugin.getManager().playerManager.bookOpens.remove(p.getName());
						plugin.getManager().playerManager.bookOpens.put(p.getName(), (current + 1));
					}
				}
			}			
		}, 1L);
		*/

		if (plugin.getManager().getSpawn() != null) {
			p.teleport(plugin.getManager().getSpawn());
		}

		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (plugin.getManager().hidPlayers.contains(pl))
			{
				pl.hidePlayer(p);
			}
		}

		plugin.getManager().sqlManager.createPlayer(p.getName(), true);

		plugin.getManager().rewardManager.loadRewards(p);

		if (plugin.getManager().sqlManager.getSettingStatus(p.getName(), "closeChat")) {
			plugin.getManager().closeChat.add(p);
		}

		if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "closeStacking")) {
			plugin.getManager().stackers.add(p);
		}

		if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "sittingEnabled")) {
			if (plugin.getManager().betterChairs != null) {
				ChairsPlugin.disableList.add(p.getUniqueId());
			}
		}

		if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "doubleJump")) {
			plugin.getManager().closeDoubleJump.add(p);
		}

		if (plugin.getManager().sqlManager.getSettingStatus(p.getName(), "hidePlayers")) {
			plugin.getManager().hidPlayers.add(p);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (plugin.getManager().hidPlayers.contains(pl))
				{
					p.hidePlayer(pl);
				}
			}
		}

		if (p.hasPermission("uniolobi.rank.vip")) {
			String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getManager().getChat().getPlayerPrefix(p));
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(prefix + p.getDisplayName() + ChatColor.GOLD + " oyuna giriş yaptı!");
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		event.setQuitMessage("");
		player.getInventory().clear();
		for (PotionEffect pot : player.getActivePotionEffects()) {
			player.removePotionEffect(pot.getType());
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
	{
		if (!event.isCancelled())
		{
			Player p = event.getPlayer();
			if (plugin.getManager().closeChat.contains(p)) {
				p.sendMessage(Main.prefix + ChatColor.RED + "Sohbeti kapattığınız için mesaj yazamazsınız.");
				p.sendMessage(Main.prefix + ChatColor.RED + "Sohbeti açmak için elinizdeki ayarlar eşyasını kullanınız.");
				event.setCancelled(true);
			}
			String prefix = plugin.getManager().getChat().getPlayerPrefix(p);
			String message = event.getMessage().replaceAll("%", "%%");
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (plugin.getManager().closeChat.contains(pl)) {
					event.getRecipients().remove(pl);
				}
			}

			if (p.hasPermission("uniolobi.renk.konusma")) {
				message = ChatColor.translateAlternateColorCodes('&', message);
			}

			event.setFormat(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', p.getName()) + " §f" + message);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setDeathMessage("");
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		if (plugin.getManager().getSpawn() != null) {
			p.teleport(plugin.getManager().getSpawn());
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		event.setLeaveMessage("");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();

		if (plugin.getManager().getSpawn() == null) {
			return;
		}

		if ((event.getTo().getBlockY() < -5)) {
			player.setFallDistance(0.0F);
			player.teleport(plugin.getManager().getSpawn());
			return;
		}

		if ((player.getGameMode() != GameMode.CREATIVE) && 
				(player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR) && 
				(!player.isFlying() && (!plugin.getManager().flying.contains(player)) && (!plugin.getManager().closeDoubleJump.contains(player)))) {
			player.setAllowFlight(true);
		}

		if ((player.getLocation().getBlock().getType() == Material.STONE_PLATE) && 
				(player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.DIAMOND_BLOCK))
		{
			Vector v = player.getLocation().getDirection().multiply(15D).setY(1.0D);
			if (Utils.yawToFace(player.getLocation().getYaw(), false) == BlockFace.NORTH) {
				v.setX(0D);
			}
			player.setVelocity(v);
			player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 1.0F);
		}
		if ((player.getLocation().getBlock().getType() == Material.WOOD_PLATE) && 
				(player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.WOOD))
		{
			Vector v = player.getLocation().getDirection().multiply(4D).setY(1.0D);
			if (Utils.yawToFace(player.getLocation().getYaw(), false) == BlockFace.NORTH) {
				v.setX(0D);
			}
			player.setVelocity(v);
			player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 1.0F);
		}
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || plugin.getManager().flying.contains(player)) {
			return;
		}

		if (plugin.getManager().closeDoubleJump.contains(player)) {
			return;
		}

		event.setCancelled(true);
		player.setAllowFlight(false);
		player.setFlying(false);
		Vector v = player.getLocation().getDirection().multiply(1.8D);
		if (v.getY() < 1.0D || v.getY() > 1.5D) {
			v.setY(1.1D);
		}
		player.setVelocity(v);
		player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if ((e.getAction() == Action.LEFT_CLICK_AIR) && 
				(p.getPassenger() != null) && 
				(p.getPassenger().getType().equals(EntityType.PLAYER)))
		{
			final Player pas = (Player)p.getPassenger();
			p.playSound(p.getLocation(), Sound.valueOf("BAT_TAKEOFF"), 1.0F, 1.0F);
			pas.leaveVehicle();
			pas.setVelocity(p.getLocation().getDirection().multiply(1.6).setY(2));
			plugin.getManager().riders.remove(p);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		if (p != null) {
			if (e.getRightClicked().getType().equals(EntityType.PLAYER))
			{
				if (plugin.getManager().stackers.contains(p))
				{
					if (plugin.getManager().cooldowns.containsKey(p) && plugin.getManager().cooldowns.get(p).contains("stack")) {
						p.sendMessage(Main.prefix + "§cTaşıma modunu 2 saniyede bir kullanabilirsiniz.");
					}
					else
					{
						final Player clicked = (Player)e.getRightClicked();
						if (clicked.getName().startsWith("NPC-")) {
							return;
						}
						if (plugin.getManager().stackers.contains(clicked))
						{
							if (!plugin.getManager().riders.contains(p))
							{
								p.setPassenger(clicked);
								plugin.getManager().riders.add(p.getPlayer());
								p.sendMessage(Main.prefix + "§7Şu anda §a" + clicked.getName() + " §7adlı oyuncuyu taşıyorsun. Fırlatmak için sol tıkla.");
								clicked.sendMessage(Main.prefix + "§7Şu anda §a" + p.getName() + " §7adlı oyuncu seni taşıyor. İnmek için Sol SHIFT tuşuna bas.");
								plugin.getManager().cooldowns.put(p, new Cooldown("stack", System.currentTimeMillis()));
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

									public void run() {
										plugin.getManager().cooldowns.get(p).removeCooldown("stack");
									}
								}, 40L);
							}
						}else {
							p.sendMessage(Main.prefix + "§cOyuncu, taşıma modunu kapattığından bu oyuncuyu taşıyamazsınız.");
						}
					}
				}
			}
		}

	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event)
	{
		String CHAR = "&";
		char[] ch = CHAR.toCharArray();
		if (event.getPlayer().hasPermission("uniolobi.coloredsign")) {
			for (int i = 0; i <= 3; i++)
			{
				String line = event.getLine(i);
				line = ChatColor.translateAlternateColorCodes(ch[0], line);
				event.setLine(i, line);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange (FoodLevelChangeEvent event) {
		if (event.getEntityType () != EntityType.PLAYER) return;
		event.setCancelled (true);
	}
}
