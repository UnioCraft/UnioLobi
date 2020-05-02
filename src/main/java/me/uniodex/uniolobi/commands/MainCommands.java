package me.uniodex.uniolobi.commands;

import com.yapzhenyie.GadgetsMenu.mysteryboxes.MysteryBoxesManager;
import com.yapzhenyie.GadgetsMenu.utils.mysteryboxes.MysteryBoxType;
import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.managers.Cooldown;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MainCommands implements CommandExecutor {

	private Main plugin;

	public MainCommands(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("uniolobi").setExecutor(this);
		plugin.getCommand("spawn").setExecutor(this);
		plugin.getCommand("serversspawn").setExecutor(this);
		plugin.getCommand("help").setExecutor(this);
		plugin.getCommand("profil").setExecutor(this);
		plugin.getCommand("oduller").setExecutor(this);
		plugin.getCommand("fly").setExecutor(this);
		plugin.getCommand("sendmessagetoplayer").setExecutor(this);
		plugin.getCommand("sudo").setExecutor(this);
		plugin.getCommand("vipbilgi").setExecutor(this);
		plugin.getCommand("bilgi").setExecutor(this);
		plugin.getCommand("givemysteryboxtoplayer").setExecutor(this);
		plugin.getCommand("vote").setExecutor(this);
		plugin.getCommand("reloadrewards").setExecutor(this);
		plugin.getCommand("market").setExecutor(this);
		//plugin.getCommand("erdogdu").setExecutor(this);
		//plugin.getCommand("gokselkirca").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("uniolobi")) {
				String command = args[0];
				if (command == null) {
					sender.sendMessage(Main.prefix + ChatColor.RED + "Hatalı bir komut girdiniz!");
					return false;
				}

				if (command.equalsIgnoreCase("setspawn")) {
					if (p.hasPermission("uniolobi.setspawn")) {
						if (args.length == 1) {
							plugin.getManager().setSpawn(p.getLocation());
							p.sendMessage(Main.prefix + "§6Spawn ayarlandı.");
							return true;
						}else {
							p.sendMessage(Main.prefix + "§cHata. /uniolobi setspawn komutunu kullanın.");
						}
					}
					return false;
				}

				if (command.equalsIgnoreCase("setserversspawn")) {
					if (p.hasPermission("uniolobi.setspawn")) {
						if (args.length == 1) {
							plugin.getManager().setServersSpawn(p.getLocation());
							p.sendMessage(Main.prefix + "§6Sunucular bölümü spawnı ayarlandı.");
							return true;
						}else {
							p.sendMessage(Main.prefix + "§cHata. /uniolobi setserversspawn komutunu kullanın.");
						}
					}
					return false;
				}

				if (command.equalsIgnoreCase("reload")) {
					if (p.hasPermission("uniolobi.reload")) {
						if (args.length == 1) {
							plugin.getManager().npcCommands.clear();
							plugin.reloadConfig();
							plugin.getManager().loadNPCCommands();
							p.sendMessage(Main.prefix + "§6NPC'ler reloadlandı.");
							return true;
						}else {
							p.sendMessage(Main.prefix + "§cHata. /uniolobi reload komutunu kullanın.");
						}
					}
					return false;
				}
			}

			if (cmd.getName().equalsIgnoreCase("fly")) {
				if (args.length == 0) {
					if (plugin.getManager().cooldowns.containsKey(p) && plugin.getManager().cooldowns.get(p).contains("fly")) {
						p.sendMessage(Main.prefix + "§cUçma modunu 2 saniyede bir değiştirebilirsiniz.");
						return true;
					}
					if (p.hasPermission("uniolobi.fly")) {
						if (plugin.getManager().flying.contains(p)) {
							plugin.getManager().flying.remove(p);
							p.setAllowFlight(false);
							p.getInventory().setItem(7, plugin.getManager().clickableItemManager.getFlyToggler(p));
							p.updateInventory();
							p.sendMessage(Main.prefix + "§bUçma modu kapatıldı.");
						} else {
							plugin.getManager().flying.add(p);
							p.setAllowFlight(true);
							p.getInventory().setItem(7, plugin.getManager().clickableItemManager.getFlyToggler(p));
							p.updateInventory();
							p.sendMessage(Main.prefix + "§bUçma modu açıldı.");
						}
						plugin.getManager().cooldowns.put(p, new Cooldown("fly", System.currentTimeMillis()));
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								plugin.getManager().cooldowns.get(p).removeCooldown("fly");
							}
						}, 40L);
						return true;
					}else {
						p.sendMessage(Main.prefix + "§cBunun için izniniz yok.");
						return true;
					}
				}else if (args.length == 1) {
					if (p.hasPermission("uniolobi.fly.others")) {
						Player otherPlayer = Bukkit.getPlayer(args[0]);
						if (otherPlayer == null) {
							p.sendMessage(Main.prefix + "§cBöyle bir oyuncu bulunamadı!");
							return true;
						}

						if (plugin.getManager().flying.contains(otherPlayer)) {
							plugin.getManager().flying.remove(otherPlayer);
							otherPlayer.setAllowFlight(false);
							otherPlayer.sendMessage(Main.prefix + otherPlayer.getName() + " §b için uçma modu kapatıldı.");
							return true;
						} else {
							plugin.getManager().flying.add(p);
							p.setAllowFlight(true);
							otherPlayer.sendMessage(Main.prefix + otherPlayer.getName() + " §b için uçma modu açıldı.");
							return true;
						}
					}else {
						p.sendMessage(Main.prefix + "§cBunun için izniniz yok.");
						return true;
					}
				}
			}

			if (cmd.getName().equalsIgnoreCase("profil")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						p.openInventory(plugin.getManager().menuManager.getProfile(p));
					}
				}, 1L);
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("market")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						p.openInventory(plugin.getManager().menuManager.getMarket());
					}
				}, 1L);
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("vote")) {
				plugin.getManager().rewardManager.reloadRewards(p);
				String voteLink = plugin.getManager().rewardManager.getVoteLink(p.getName());
				if (voteLink != "") {
					p.sendMessage(Main.prefix + "§f§lOy vermek ve ödülleri toplamak için tıkla: §b§l§n" + voteLink);
				}else {
					p.sendMessage(Main.prefix + "§cBugünkü oy verme haklarının tamamını kullanmışsın. Yarın daha fazla oy verebilir ve ödül kazanabilirsin!");
				}
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("vipbilgi")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ib open vip " + sender.getName());
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("bilgi")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ib open bilgi " + sender.getName());
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("oduller")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						p.openInventory(plugin.getManager().menuManager.getRewards(p));
					}
				}, 1L);
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("spawn")) {
				if (plugin.getManager().getSpawn() != null) {
					p.teleport(plugin.getManager().getSpawn());
				}else {
					sender.sendMessage(Main.prefix + ChatColor.RED + "Bir hata oluştu.");
				}
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("serversspawn")) {
				if (plugin.getManager().getServersSpawn() != null) {
					p.teleport(plugin.getManager().getServersSpawn());
				}else {
					sender.sendMessage(Main.prefix + ChatColor.RED + "Bir hata oluştu.");
				}
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("erdogdu")) {
				if (p != null) {
					plugin.getManager().rewardManager.erdogduReward(p.getName());
				}
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("gokselkirca")) {
				if (p != null) {
					plugin.getManager().rewardManager.gokselkircaReward(p.getName());
				}
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("help") || cmd.getName().equalsIgnoreCase("yardim") || cmd.getName().equalsIgnoreCase("yardım")) {
				if (args.length == 0) {
					for (String message : plugin.getConfig().getStringList("Messages.Help.1")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', message).replace("%player%", plugin.getManager().playerManager.getDisplayName(p)));
					}
				}else if (args.length == 1) {
					ArrayList<Integer> pages = new ArrayList<Integer>();
					for (String key : plugin.getConfig().getConfigurationSection("Messages.Help").getKeys(false)) {
						pages.add(Integer.valueOf(key));
					}
					if (pages.contains(Integer.valueOf(args[0]))) {
						for (String message : plugin.getConfig().getStringList("Messages.Help." + args[0])) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', message).replace("%player%", plugin.getManager().playerManager.getDisplayName(p)));
						}
					}else {
						for (String message : plugin.getConfig().getStringList("Messages.Help.1")) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', message).replace("%player%", plugin.getManager().playerManager.getDisplayName(p)));
						}
					}
				}else {
					for (String message : plugin.getConfig().getStringList("Messages.Help.1")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', message).replace("%player%", plugin.getManager().playerManager.getDisplayName(p)));
					}
				}
				return true;
			}
		}else if (sender instanceof ConsoleCommandSender) {
			if (cmd.getName().equalsIgnoreCase("sendmessagetoplayer")) {
				if (args.length >= 2) {
					Player p = Bukkit.getPlayer(args[0]);
					if (p == null) return false;
					String mesaj = args.length > 0 ? StringUtils.join(args, ' ', 1, args.length) : null;
					p.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', mesaj));
				}
			}
			if (cmd.getName().equalsIgnoreCase("sudo")) {
				if (args.length >= 2) {
					Player p = Bukkit.getPlayer(args[0]);
					if (p == null) return false;
					String mesaj = args.length > 0 ? StringUtils.join(args, ' ', 1, args.length) : null;
					Bukkit.dispatchCommand(sender, mesaj);
				}
			}
			if (cmd.getName().equalsIgnoreCase("reloadrewards")) {
				if (args.length == 1) {
					Player p = Bukkit.getPlayer(args[0]);
					if (p == null) return false;
					plugin.getManager().rewardManager.reloadRewards(p);
				}
			}
			if (plugin.getManager().gadgetsMenu != null) {
				if (cmd.getName().equalsIgnoreCase("givemysteryboxtoplayer")) {
					if (args.length >= 2) {
						Player p = Bukkit.getPlayer(args[0]);
						if (args[1].equalsIgnoreCase("vote")) {
							if (p != null) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gmysteryboxes give " + args[0] + " 1 1 ex=1d");
							}else {
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_1, false, null, System.currentTimeMillis() + 86400000);
							}
						}
						if (args[1].equalsIgnoreCase("vip")) {
							if (p != null) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gmysteryboxes give " + args[0] + " 5 5");
							}else {
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_5, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_5, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_5, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_5, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_5, false, null, null);
							}
						}
						if (args[1].equalsIgnoreCase("monthlyvip")) {
							if (p != null) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gmysteryboxes give " + args[0] + " 5 3");
							}else {
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
							}
						}
						if (args[1].equalsIgnoreCase("youtuber")) {
							if (p != null) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gmysteryboxes give " + args[0] + " 3 3");
							}else {
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
								MysteryBoxesManager.giveMysteryBoxes(Bukkit.getOfflinePlayer(args[0]), MysteryBoxType.NORMAL_MYSTERY_BOX_3, false, null, null);
							}
						}
					}
				}
			}
		}
		return false;
	}
}
