package me.uniodex.uniolobi.utils.packages.bossbar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.utils.packages.menubuilder.util.Reflection;
import me.uniodex.uniolobi.utils.packages.reflection.minecraft.Minecraft;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class BossBarAPI implements Listener {

	protected static final Map<UUID, Collection<BossBar>> barMap = new ConcurrentHashMap<>();

	public static boolean is1_9 = Minecraft.VERSION.newerThan(Minecraft.Version.v1_9_R1);

	public enum Color {
		PINK,
		BLUE,
		RED,
		GREEN,
		YELLOW,
		PURPLE,
		WHITE;
	}

	public enum Style {
		PROGRESS,
		NOTCHED_6,
		NOTCHED_10,
		NOTCHED_12,
		NOTCHED_20;
	}

	public enum Property {
		DARKEN_SKY,
		PLAY_MUSIC,
		//This seems to be the only property implemented client-side currently
		CREATE_FOG;
	}

	/**
	 * Not compatible with 1.8!
	 *
	 * @param players    Receivers of the BossBar
	 * @param message    Message of the BossBar (JSON)
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color}
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style}
	 * @param progress   progress (0.0 - 1.0)
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property}
	 * @return the new {@link BossBar} instance
	 */
	public static BossBar addBar(Collection<Player> players, String message, Color color, Style style, float progress, Property... properties) {
		validate1_9();
		BossBar bossBar = new PacketBossBar(message, color, style, progress, properties);
		for (Player player : players) {
			addBarForPlayer(player, bossBar);
		}
		return bossBar;
	}

	/**
	 * Not compatible with 1.8!
	 *
	 * @param players    Receivers of the BossBar
	 * @param component  displayed message
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color}
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style}
	 * @param progress   progress (0.0 - 1.0)
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property}
	 * @return the new {@link BossBar} instance
	 */
	public static BossBar addBar(Collection<Player> players, BaseComponent component, Color color, Style style, float progress, Property... properties) {
		validate1_9();
		BossBar bossBar = new PacketBossBar(component, color, style, progress, properties);
		for (Player player : players) {
			addBarForPlayer(player, bossBar);
		}
		return bossBar;
	}

	/**
	 * Not compatible with 1.8!
	 *
	 * @param players    Receivers of the BossBar
	 * @param component  displayed message
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color}
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style}
	 * @param progress   progress (0.0 - 1.0)
	 * @param timeout    time until the bar disappears
	 * @param interval   interval of the "countdown"
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property}
	 * @return the new {@link BossBar} instance
	 */
	public static BossBar addBar(Collection<Player> players, BaseComponent component, Color color, Style style, float progress, int timeout, long interval, Property... properties) {
		validate1_9();
		final BossBar bossBar = addBar(players, component, color, style, progress, properties);
		new BossBarTimer((PacketBossBar) bossBar, progress, timeout).runTaskTimer(Main.instance, interval, interval);
		return bossBar;
	}

	/**
	 * Compatible with 1.8
	 *
	 * @param player     Receiver of the BossBar
	 * @param component  displayed message
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color} (1.9-only)
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style} (1.9-only)
	 * @param progress   progress (0.0 - 1.0)
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property} (1.9-only)
	 * @return the new {@link BossBar} instance
	 * @see #setMessage(Player, String, float)
	 */
	public static BossBar addBar(Player player, BaseComponent component, Color color, Style style, float progress, Property... properties) {
		if (is1_9) {
			BossBar bossBar = new PacketBossBar(component, color, style, progress, properties);
			addBarForPlayer(player, bossBar);
			return bossBar;
		} else {
			setMessage(player, component.toLegacyText(), progress * 100);
			return getBossBar(player);
		}
	}

	/**
	 * Compatible with 1.8
	 *
	 * @param player     Receiver of the BossBar
	 * @param component  displayed message
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color} (1.9-only)
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style} (1.9-only)
	 * @param progress   progress (0.0-1.0)
	 * @param timeout    time until the bar disappears
	 * @param interval   interval of the "countdown"
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property} (1.9-only)
	 * @return the new {@link BossBar} instance
	 */
	public static BossBar addBar(Player player, BaseComponent component, Color color, Style style, float progress, int timeout, long interval, Property... properties) {
		if (is1_9) {
			final BossBar bossBar = addBar(player, component, color, style, progress, properties);
			new BossBarTimer((PacketBossBar) bossBar, progress, timeout).runTaskTimer(Main.instance, interval, interval);
			return bossBar;
		} else {
			setMessage(player, component.toLegacyText(), progress * 100, timeout);
			return getBossBar(player);
		}
	}

	/**
	 * Not compatible with 1.8! Adds a BossBar without displaying it to a player
	 *
	 * @param component  displayed message
	 * @param color      {@link org.inventivetalent.bossbar.BossBarAPI.Color}
	 * @param style      {@link org.inventivetalent.bossbar.BossBarAPI.Style}
	 * @param progress   progress (0.0 - 1.0)
	 * @param properties {@link org.inventivetalent.bossbar.BossBarAPI.Property}
	 * @return the new {@link BossBar} instance
	 */
	public static BossBar addBar(BaseComponent component, Color color, Style style, float progress, Property... properties) {
		validate1_9();
		return new PacketBossBar(component, color, style, progress, properties);
	}

	public static Collection<BossBar> getBossBars(Player player) {
		if (!barMap.containsKey(player.getUniqueId())) { return new ArrayList<>(); }
		return new ArrayList<>(barMap.get(player.getUniqueId()));
	}

	protected static void addBarForPlayer(Player player, BossBar bossBar) {
		bossBar.addPlayer(player);

		Collection<BossBar> collection = barMap.get(player.getUniqueId());
		if (collection == null) { collection = new ArrayList<>(); }
		collection.add(bossBar);
		barMap.put(player.getUniqueId(), collection);
	}

	protected static void removeBarForPlayer(Player player, BossBar bossBar) {
		bossBar.removePlayer(player);

		Collection<BossBar> collection = barMap.get(player.getUniqueId());
		if (collection != null) {
			collection.remove(bossBar);
			if (!collection.isEmpty()) {
				barMap.put(player.getUniqueId(), collection);
			} else {
				barMap.remove(player.getUniqueId());
			}
		}
	}

	public static void removeAllBars(Player player) {
		for (BossBar bossBar : getBossBars(player)) {
			removeBarForPlayer(player, bossBar);
		}
	}

	//// Deprecated (< 1.9) methods

	/**
	 * Sets the boss-bar message for the specified player
	 *
	 * @param player  Receiver of the message
	 * @param message Message content
	 */
	@Deprecated
	public static void setMessage(Player player, String message) {
		setMessage(player, message, 100);
	}

	/**
	 * Sets the boss-bar message for the specified player
	 *
	 * @param player     Receiver of the message
	 * @param message    Message content
	 * @param percentage Health percentage
	 */
	@Deprecated
	public static void setMessage(Player player, String message, float percentage) {
		setMessage(player, message, percentage, 0);
	}

	/**
	 * Sets the boss-bar message for the specified player
	 *
	 * @param player     Receiver of the message
	 * @param message    Message content
	 * @param percentage Health percentage
	 * @param timeout    Amount of seconds until the bar is removed
	 */
	@Deprecated
	public static void setMessage(Player player, String message, float percentage, int timeout) {
		setMessage(player, message, percentage, timeout, 100);
	}

	/**
	 * Sets the boss-bar message for the specified player
	 *
	 * @param player     Receiver of the message
	 * @param message    Message content
	 * @param percentage Health percentage
	 * @param timeout    Amount of seconds until the bar is removed
	 * @param minHealth  minimum health (100 by default)
	 */
	@Deprecated
	public static void setMessage(Player player, String message, float percentage, int timeout, float minHealth) {
		if (is1_9) {
			removeAllBars(player);
			addBar(player, new TextComponent(message), Color.PURPLE, Style.PROGRESS, percentage / 100);
		} else {
			if (!barMap.containsKey(player.getUniqueId())) {
				ArrayList<BossBar> list = new ArrayList<>();
				list.add(new EntityBossBar(player, message, percentage, timeout, minHealth));
				barMap.put(player.getUniqueId(), list);
			}
			BossBar bar = ((List<BossBar>) barMap.get(player.getUniqueId())).get(0);
			if (!bar.getMessage().equals(message)) {
				bar.setMessage(message);
			}
			float newHealth = percentage / 100F * bar.getMaxHealth();
			if (bar.getHealth() != newHealth) {
				bar.setHealth(percentage);
			}
			if (!bar.isVisible()) {
				bar.setVisible(true);
			}
		}
	}

	/**
	 * @param player {@link Player}
	 * @return The current message of the player's bar
	 */
	@Deprecated
	public static String getMessage(Player player) {
		BossBar bar = getBossBar(player);
		if (bar == null) { return null; }
		return bar.getMessage();
	}

	/**
	 * @param player {@link Player} to check
	 * @return <code>true</code> if the player has a bar
	 */
	@Deprecated
	public static boolean hasBar(@Nonnull Player player) {
		return barMap.containsKey(player.getUniqueId());
	}

	/**
	 * Removes the bar of a player
	 *
	 * @param player Player to remove
	 */
	@Deprecated
	public static void removeBar(@Nonnull Player player) {
		BossBar bar = getBossBar(player);
		if (bar != null) { bar.setVisible(false); }
		removeAllBars(player);
	}

	/**
	 * Changes the displayed health of the bar
	 *
	 * @param player     {@link Player}
	 * @param percentage Health percentage
	 */
	@Deprecated
	public static void setHealth(Player player, float percentage) {
		BossBar bar = getBossBar(player);
		if (bar == null) { return; }
		bar.setHealth(percentage);
	}

	/**
	 * @param player {@link Player}
	 * @return The health of the player's bar
	 */
	@Deprecated
	public static float getHealth(@Nonnull Player player) {
		BossBar bar = getBossBar(player);
		if (bar == null) { return -1; }
		return bar.getHealth();
	}

	/**
	 * Get the bar for the specified player
	 *
	 * @param player {@link Player}
	 * @return a {@link EntityBossBar} instance if the player has a bar, <code>null</code> otherwise
	 */
	@Nullable
	@Deprecated
	public static BossBar getBossBar(@Nonnull Player player) {
		if (player == null) { return null; }
		List<BossBar> list = ((List<BossBar>) barMap.get(player.getUniqueId()));

		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * @return A {@link Collection} of all registered bars
	 */
	@Deprecated
	public static Collection<BossBar> getBossBars() {
		List<BossBar> list = new ArrayList<>();
		for (Collection<BossBar> collection : barMap.values()) {
			list.add(((List<BossBar>) collection).get(0));
		}
		return list;
	}

	protected static void sendPacket(Player p, Object packet) {
		if (p == null || packet == null) { throw new IllegalArgumentException("player and packet cannot be null"); }
		try {
			Object handle = Reflection.getHandle(p);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Reflection.getMethod(connection.getClass(), "sendPacket", Reflection.getNMSClass("Packet")).invoke(connection, new Object[] { packet });
		} catch (Exception e) {
		}
	}

	static void validate1_9() {
		if (!is1_9) {
			throw new RuntimeException(new UnsupportedOperationException("This method is not compatible with versions < 1.9"));
		}
	}

	Logger logger = Logger.getLogger("BossBarAPI");
	
	private void init(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Main.instance = plugin;
		for (Player player : Bukkit.getOnlinePlayers()) {
			BossBarAPI.removeAllBars(player);
		}
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
		if ("BarAPI".equals(e.getPlugin().getName()) && Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
			try {
				Class<?> barAPI = Class.forName("me.confuser.barapi.BarAPI");

				Method method = barAPI.getDeclaredMethod("enabled");
				method.setAccessible(true);
				if ((boolean) method.invoke(null)) {
					logger.info("Successfully replaced BarAPI.");
					return;
				}
			} catch (Exception ex) {
			}
			logger.warning("Failed to replace BarAPI.");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		BossBarAPI.removeBar(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent e) {
		BossBarAPI.removeBar(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent e) {
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
			this.handlePlayerTeleport(e.getPlayer(), e.getFrom(), e.getTo());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(PlayerRespawnEvent e) {
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
			this.handlePlayerTeleport(e.getPlayer(), e.getPlayer().getLocation(), e.getRespawnLocation());
		}
	}

	protected void handlePlayerTeleport(Player player, Location from, Location to) {
		if (!BossBarAPI.hasBar(player)) { return; }
		final BossBar bar = BossBarAPI.getBossBar(player);
		bar.setVisible(false);
		new BukkitRunnable() {

			@Override
			public void run() {
				bar.setVisible(true);
			}
		}.runTaskLater(Main.instance, 2);
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
			final BossBar bar = BossBarAPI.getBossBar(e.getPlayer());
			if (bar != null) {
				new BukkitRunnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						if (!e.getPlayer().isOnline()) { return; }
						bar.updateMovement();
					}
				}.runTaskLater(Main.instance, 0);
			}
		}
	}

	public BossBarAPI(Main plugin) {
		init(plugin);
	}

}
