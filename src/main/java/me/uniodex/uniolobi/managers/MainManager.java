package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.commands.MainCommands;
import me.uniodex.uniolobi.config.MainConfig;
import me.uniodex.uniolobi.listeners.*;
import me.uniodex.uniolobi.utils.Utils;
import net.blackscarx.betterchairs.ChairsPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class MainManager {

	private Main plugin;

	public Set<Player> flying = new HashSet<>();
	public ArrayList<Player> riders = new ArrayList<Player>();
	public ArrayList<Player> stackers = new ArrayList<Player>();
	public HashMap<Player, Cooldown> cooldowns = new HashMap<Player, Cooldown>();
	public List<Player> hidPlayers = new ArrayList<Player>();
	public List<Player> closeChat = new ArrayList<Player>();
	public List<Player> closeDoubleJump = new ArrayList<Player>();
	public HashMap<Integer, String> npcCommands = new HashMap<Integer, String>();
	public Plugin betterChairs = null;
	public Plugin gadgetsMenu = null;

	public ClickableItemManager clickableItemManager;
	public MenuManager menuManager;
	public SkywarsMarketManager skywarsMarketManager;
	public SQLManager sqlManager;
	public PlayerManager playerManager;
	public RewardManager rewardManager;
	public RconManager rconManager;

	private ProtectionListeners protectionListeners;

	private Location spawn;
	private Location serversSpawn;
	private Chat chat;
	private Permission permission;

	public MainManager(Main plugin) {
		this.plugin = plugin;
		new MainConfig(plugin);
		new MainCommands(plugin);
		protectionListeners = new ProtectionListeners(plugin);
		new CoreListeners(plugin);
		new ClickableItemListeners(plugin);
		if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
			new NPCListeners(plugin);
		}

		if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
			Bukkit.getLogger().log(Level.INFO, "[UNIOCRAFT] Votifier bulundu ve entegre edildi.");
			new VotifierListener(plugin);
		}

		if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			throw new RuntimeException("Could not find PlaceholderAPI!! Plugin can not work without it!");
		}

		sqlManager = new SQLManager(plugin, plugin.getConfig().getString("MySQL.table"), plugin.getConfig().getString("MySQL.host"), plugin.getConfig().getString("MySQL.port"), plugin.getConfig().getString("MySQL.database"), plugin.getConfig().getString("MySQL.username"), plugin.getConfig().getString("MySQL.password"));
		clickableItemManager = new ClickableItemManager(plugin);
		skywarsMarketManager = new SkywarsMarketManager(plugin);
		rewardManager = new RewardManager(plugin);
		menuManager = new MenuManager(plugin);
		playerManager = new PlayerManager(plugin);
		rconManager = new RconManager(plugin);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

		betterChairs = Bukkit.getPluginManager().getPlugin("BetterChairs");
		gadgetsMenu = Bukkit.getPluginManager().getPlugin("GadgetsMenu");

		loadSpawn();
		setupChat();
		setupPermissions();
		cleanLogs();
		alwaysDay();
		loadNPCCommands();

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "remove arrow 100");
			}
		}, 20L, 600*20L);
	}

	public void toggleVanish(Player p) {
		if (hidPlayers.contains(p)) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				p.showPlayer(pl);
			}
			hidPlayers.remove(p);
		} else {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (!pl.hasPermission("uniolobi.vanish.bypass"))
				{
					p.hidePlayer(pl);
				}
			}
			hidPlayers.add(p);
		}
	}

	public void toggleChat(Player p) {
		if (closeChat.contains(p)) {
			closeChat.remove(p);
		} else {
			closeChat.add(p);
		}
	}

	public void toggleDoubleJump(Player p) {
		if (closeDoubleJump.contains(p)) {
			closeDoubleJump.remove(p);
		} else {
			closeDoubleJump.add(p);
		}

		if (!p.isFlying() && p.getGameMode() != GameMode.CREATIVE) {
			p.setAllowFlight(false);
		}
	}

	public void toggleSitting(Player p) {
		if (plugin.getManager().betterChairs != null) {
			if (ChairsPlugin.disableList.contains(p.getUniqueId())) {
				ChairsPlugin.disableList.remove(p.getUniqueId());
			} else {
				ChairsPlugin.disableList.add(p.getUniqueId());
			}
		}
	}

	public void toggleStacking(Player p) {
		if (stackers.contains(p)) {
			stackers.remove(p);
		} else {
			stackers.add(p);
		}
	}

	private void alwaysDay() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (World world : Bukkit.getWorlds()) {
					world.setGameRuleValue("doDaylightCycle", "false");
					world.setTime(1000);
				}
			}
		}, 20L);
	}

	public void loadNPCCommands() {
		for (String key : plugin.getConfig().getConfigurationSection("NPCCommands").getKeys(false)) {
			Integer npcId = Integer.valueOf(key);
			String npcCommand = plugin.getConfig().getString("NPCCommands."+npcId);
			npcCommands.put(npcId, npcCommand);
		}
	}

	private void cleanLogs() {
		Bukkit.getScheduler()
		.scheduleAsyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				long time = new Date().getTime() - 86400000L * 60;

				File folder = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/logs");
				if (!folder.exists()) {
					return;
				}
				File[] files = folder.listFiles();

				@SuppressWarnings("unused")
				int deleted = 0;
				for (File file : files) {
					if ((file.isFile()) && (file.getName().endsWith(".log.gz")) && (time > Utils.parseTime(file.getName().replace(".log.gz", "")).getTime()))
					{
						file.delete();
						deleted++;
					}
				}
				System.out.println("Loglar temizlendi.");
			}
		}, 1L);
	}

	private boolean setupChat()
	{
		RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private void loadSpawn()
	{
		if (plugin.getConfig().getString("spawn.world") != null) {
			this.spawn = new Location(Bukkit.getWorld(plugin.getConfig().getString("spawn.world")), plugin.getConfig().getDouble("spawn.x"), plugin.getConfig().getDouble("spawn.y"), plugin.getConfig().getDouble("spawn.z"), Float.parseFloat(plugin.getConfig().getString("spawn.yaw")), Float.parseFloat(plugin.getConfig().getString("spawn.pitch")));
		}

		if (plugin.getConfig().getString("serversSpawn.world") != null) {
			this.serversSpawn = new Location(Bukkit.getWorld(plugin.getConfig().getString("serversSpawn.world")), plugin.getConfig().getDouble("serversSpawn.x"), plugin.getConfig().getDouble("serversSpawn.y"), plugin.getConfig().getDouble("serversSpawn.z"), Float.parseFloat(plugin.getConfig().getString("serversSpawn.yaw")), Float.parseFloat(plugin.getConfig().getString("serversSpawn.pitch")));
		}
	}

	public Location getSpawn() {
		return spawn;
	}

	public Location getServersSpawn() {
		return serversSpawn;
	}

	public void setSpawn(Location loc)
	{
		this.spawn = loc;
		plugin.getConfig().set("spawn", loc);
		plugin.getConfig().set("spawn.world", loc.getWorld().getName());
		plugin.getConfig().set("spawn.x", Double.valueOf(loc.getX()));
		plugin.getConfig().set("spawn.y", Double.valueOf(loc.getY()));
		plugin.getConfig().set("spawn.z", Double.valueOf(loc.getZ()));
		plugin.getConfig().set("spawn.yaw", Float.valueOf(loc.getYaw()));
		plugin.getConfig().set("spawn.pitch", Float.valueOf(loc.getPitch()));
		plugin.saveConfig();
	}

	public void setServersSpawn(Location loc)
	{
		this.serversSpawn = loc;
		plugin.getConfig().set("serversSpawn", loc);
		plugin.getConfig().set("serversSpawn.world", loc.getWorld().getName());
		plugin.getConfig().set("serversSpawn.x", Double.valueOf(loc.getX()));
		plugin.getConfig().set("serversSpawn.y", Double.valueOf(loc.getY()));
		plugin.getConfig().set("serversSpawn.z", Double.valueOf(loc.getZ()));
		plugin.getConfig().set("serversSpawn.yaw", Float.valueOf(loc.getYaw()));
		plugin.getConfig().set("serversSpawn.pitch", Float.valueOf(loc.getPitch()));
		plugin.saveConfig();
	}

	public Chat getChat() {
		return chat;
	}

	public Permission getPermissions() {
		return permission;
	}

}