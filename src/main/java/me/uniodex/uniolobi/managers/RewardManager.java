package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.objects.Vote;
import me.uniodex.uniolobi.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class RewardManager {

	private Main plugin;

	public HashMap<String, Vote> votes = new HashMap<String, Vote>();
	public HashMap<String, Long> gokselkircaRewards = new HashMap<String, Long>();
	public HashMap<String, Long> erdogduRewards = new HashMap<String, Long>();
	public HashMap<String, Long> dailyRewards = new HashMap<String, Long>();
	public HashMap<String, Long> dailyRewardsVIP = new HashMap<String, Long>();
	public HashMap<String, Long> monthlyRewardsVIP = new HashMap<String, Long>();

	public RewardManager(Main plugin) {
		this.plugin = plugin;
	}

	public void loadRewards(Player p) {
		String player = p.getName();

		dailyRewards.put(player, plugin.getManager().sqlManager.getDailyReward(player));
		dailyRewardsVIP.put(player, plugin.getManager().sqlManager.getDailyRewardVIP(player));
		monthlyRewardsVIP.put(player, plugin.getManager().sqlManager.getMonthlyRewardVIP(player));
		erdogduRewards.put(player, plugin.getManager().sqlManager.getErdogduReward(player));
		gokselkircaRewards.put(player, plugin.getManager().sqlManager.getGokselKircaReward(player));

		HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(player);
		this.votes.put(player, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
	}

	public void reloadRewards(Player p) {
		String player = p.getName();

		dailyRewards.remove(player);
		dailyRewardsVIP.remove(player);
		monthlyRewardsVIP.remove(player);
		erdogduRewards.remove(player);
		gokselkircaRewards.remove(player);
		votes.remove(player);

		dailyRewards.put(player, plugin.getManager().sqlManager.getDailyReward(player));
		dailyRewardsVIP.put(player, plugin.getManager().sqlManager.getDailyRewardVIP(player));
		monthlyRewardsVIP.put(player, plugin.getManager().sqlManager.getMonthlyRewardVIP(player));
		erdogduRewards.put(player, plugin.getManager().sqlManager.getErdogduReward(player));
		gokselkircaRewards.put(player, plugin.getManager().sqlManager.getGokselKircaReward(player));

		HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(player);
		this.votes.put(player, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
	}

	public String getVoteLink(String username) {
		Vote vote = plugin.getManager().rewardManager.votes.get(username);

		Long site1 = vote.lastVotedTime_site1;
		Long site2 = vote.lastVotedTime_site2;
		Long site3 = vote.lastVotedTime_site3;
		Long site4 = vote.lastVotedTime_site4;

		if ((site1 + 43200) > (System.currentTimeMillis() / 1000)) {
			if ((site2 + 43200) > (System.currentTimeMillis() / 1000)) {
				if ((site3 + 43200) > (System.currentTimeMillis() / 1000)) {
					if ((site4 + 43200) > (System.currentTimeMillis() / 1000)) {
						return "";
					}else {
						return "www.uniocraft.com/vote4";
					}
				}else {
					return "www.uniocraft.com/vote3";
				}
			}else {
				return "www.uniocraft.com/vote2";
			}
		}else {
			return "www.uniocraft.com/vote1";
		}
	}

	public void voteReward(String username, String address, String service, Long time) {
		Vote vote = votes.get(username);
		if (vote == null) {
			HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(username);
			vote = new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4));
		}

		Long site1 = vote.lastVotedTime_site1;
		Long site2 = vote.lastVotedTime_site2;
		Long site3 = vote.lastVotedTime_site3;
		Long site4 = vote.lastVotedTime_site4;

		int serviceID = Utils.getServiceID(service);

		if (serviceID == 1) {
			if ((site1 + 43200) < (System.currentTimeMillis() / 1000)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 200");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givemysteryboxtoplayer "+ username +" vote");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd sendmessagetoplayer " + username + " " + "&aOy verdiğiniz için teşekkürler! Ödül olarak &b200 Cevher &ave &b1 gizemli kutu &akazandınız! Gizemli kutunuz gelmediyse sunucu seçim menüsünden lobi değiştiriniz.");
				plugin.getManager().sqlManager.setVote(username, serviceID, time, address);
				HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(username);
				this.votes.put(username, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
			}
		}

		if (serviceID == 2) {
			if ((site2 + 43200) < (System.currentTimeMillis() / 1000)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 200");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givemysteryboxtoplayer "+ username +" vote");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd sendmessagetoplayer " + username + " " + "&aOy verdiğiniz için teşekkürler! Ödül olarak &b200 Cevher &ave &b1 gizemli kutu &akazandınız! Gizemli kutunuz gelmediyse sunucu seçim menüsünden lobi değiştiriniz.");
				plugin.getManager().sqlManager.setVote(username, serviceID, time, address);
				HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(username);
				this.votes.put(username, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
			}
		}

		if (serviceID == 3) {
			if ((site3 + 43200) < (System.currentTimeMillis() / 1000)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 300");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd sendmessagetoplayer " + username + " " + "&aOy verdiğiniz için teşekkürler! Ödül olarak &b300 Cevher &akazandınız!");
				plugin.getManager().sqlManager.setVote(username, serviceID, time, address);
				HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(username);
				this.votes.put(username, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
			}
		}

		if (serviceID == 4) {
			if ((site4 + 43200) < (System.currentTimeMillis() / 1000)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 200");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd sendmessagetoplayer " + username + " " + "&aOy verdiğiniz için teşekkürler! Ödül olarak &b200 Cevher &akazandınız!");
				plugin.getManager().sqlManager.setVote(username, serviceID, time, address);
				HashMap<Integer, Long> votes = plugin.getManager().sqlManager.getVotes(username);
				this.votes.put(username, new Vote(votes.get(1), votes.get(2), votes.get(3), votes.get(4)));
			}
		}
	}

	public void dailyReward(String username) {
		Player player = Bukkit.getPlayer(username);
		Long dr = dailyRewards.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getDailyReward(username);
		}

		if ((dr + 86400) < (System.currentTimeMillis() / 1000)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 200");

			plugin.getManager().sqlManager.setDailyReward(username);
			dailyRewards.remove(username);
			dailyRewards.put(username, plugin.getManager().sqlManager.getDailyReward(username));

			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aSunucumuzu ziyaret ettiğiniz için teşekkürler! Ödül olarak &b200 Cevher &akazandınız!"));
			}
		}else {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cGünlük ödülünüzü zaten almışsınız! Bir daha ödül almak için " + Utils.secondsToString((dr + 86400) - (System.currentTimeMillis()/1000)) + " daha beklemelisiniz."));
			}
		}
	}

	public void dailyRewardVIP(String username) {
		Player player = Bukkit.getPlayer(username);
		if (!plugin.getManager().getPermissions().has("world", username, "unio.rank.vip")) {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.RED + "VIP ödülü alabilmek için VIP olmalısınız.");
			}
			return;
		}
		Long dr = dailyRewardsVIP.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getDailyRewardVIP(username);
		}

		if ((dr + 86400) < (System.currentTimeMillis() / 1000)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 400");

			plugin.getManager().sqlManager.setDailyRewardVIP(username);
			dailyRewardsVIP.remove(username);
			dailyRewardsVIP.put(username, plugin.getManager().sqlManager.getDailyRewardVIP(username));

			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aSunucumuzda oynadığınız için teşekkürler! Ödül olarak &b400 Cevher &akazandınız!"));
			}
		}else {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cGünlük VIP ödülünüzü zaten almışsınız! Bir daha ödül almak için " + Utils.secondsToString((dr + 86400) - (System.currentTimeMillis()/1000)) + " daha beklemelisiniz."));
			}
		}
	}

	public void monthlyRewardVIP(String username) {
		Player player = Bukkit.getPlayer(username);
		if (!plugin.getManager().getPermissions().has("world", username, "unio.rank.vip")) {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.RED + "VIP ödülü alabilmek için VIP olmalısınız.");
			}
			return;
		}
		Long dr = monthlyRewardsVIP.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getMonthlyRewardVIP(username);
		}

		if ((dr + 2592000) < (System.currentTimeMillis() / 1000)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 1000");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givemysteryboxtoplayer " + username +" monthlyvip");
			plugin.getManager().sqlManager.setMonthlyRewardVIP(username);
			monthlyRewardsVIP.remove(username);
			monthlyRewardsVIP.put(username, plugin.getManager().sqlManager.getMonthlyRewardVIP(username));

			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aSunucumuzu desteklediğiniz için teşekkürler! Ödül olarak &b1000 Cevher &ave &b5 gizemli kutu &akazandınız!"));
			}
		}else {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cAylık VIP ödülünüzü zaten almışsınız! Bir daha ödül almak için " + Utils.secondsToString((dr + 2592000) - (System.currentTimeMillis()/1000)) + " daha beklemelisiniz."));
			}
		}
	}

	public boolean isDailyRewardTaken(String username) {
		Long dr = dailyRewards.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getDailyReward(username);
		}

		if ((dr + 86400) < (System.currentTimeMillis() / 1000)) {
			return false;
		}else {
			return true;
		}
	}

	public boolean isDailyRewardVIPTaken(String username) {
		Long dr = dailyRewardsVIP.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getDailyRewardVIP(username);
		}

		if ((dr + 86400) < (System.currentTimeMillis() / 1000)) {
			return false;
		}else {
			return true;
		}
	}

	public boolean isMonthlyRewardVIPTaken(String username) {
		Long dr = monthlyRewardsVIP.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getMonthlyRewardVIP(username);
		}

		if ((dr + 2592000) < (System.currentTimeMillis() / 1000)) {
			return false;
		}else {
			return true;
		}
	}

	public void gokselkircaReward(String username) {
		Player player = Bukkit.getPlayer(username);
		Long dr = gokselkircaRewards.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getGokselKircaReward(username);
		}

		if (dr == 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 3000");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givemysteryboxtoplayer " + username +" youtuber");
			plugin.getManager().sqlManager.setGokselKircaReward(username);
			gokselkircaRewards.remove(username);
			gokselkircaRewards.put(username, plugin.getManager().sqlManager.getGokselKircaReward(username));

			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aSunucumuzu ziyaret ettiğiniz için teşekkürler! Ödül olarak &b3000 Cevher &ave &b3 Gizemli Kutu kazandınız!"));
			}
		}else {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cZaten Göksel Kırca ödülünüzü almışsınız."));
			}
		}
	}

	public void erdogduReward(String username) {
		Player player = Bukkit.getPlayer(username);
		Long dr = erdogduRewards.get(username);

		if (dr == null) {
			dr = plugin.getManager().sqlManager.getErdogduReward(username);
		}

		if (dr == 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 3000");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givemysteryboxtoplayer " + username +" youtuber");
			plugin.getManager().sqlManager.setErdogduReward(username);
			erdogduRewards.remove(username);
			erdogduRewards.put(username, plugin.getManager().sqlManager.getErdogduReward(username));

			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aSunucumuzu ziyaret ettiğiniz için teşekkürler! Ödül olarak &b3000 Cevher &ave &b3 Gizemli Kutu kazandınız!"));
			}
		}else {
			if (player != null) {
				player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cZaten Erdem Erdoğdu ödülünüzü almışsınız."));
			}
		}
	}

	/*
	public void adReward(String username) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cevher ver " + username + " 250");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gmysteryboxes give " + username + " 1 2 ex=false");

		Player player = Bukkit.getPlayer(username); 
		if (player != null) {
			player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&aReklam izleyerek bizi desteklediğiniz için teşekkürler! Ödül olarak &b250 Cevher &ave &b1 gizemli kutu &akazandınız!"));
		}
	}
	 */
}
