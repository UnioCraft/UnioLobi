package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

	private Main plugin;
	
	public HashMap<String, Integer> bookOpens = new HashMap<String, Integer>(); 
	
	public PlayerManager(Main plugin) {
		this.plugin = plugin;
	}

	public void toggleHidePlayer(Player p) {
		plugin.getManager().toggleVanish(p);
		if (plugin.getManager().sqlManager.toggleSettingStatus(p.getName(), "hidePlayers")) {
			p.sendMessage(Main.prefix + ChatColor.GREEN + "Ayar başarıyla değiştirildi!");
		}else {
			p.sendMessage(Main.prefix + ChatColor.RED + "Bir sorun oluştuğu için ayar değiştirilemedi!");
		}
	}
	
	
	public void toggleDoubleJump(Player p) {
		plugin.getManager().toggleDoubleJump(p);
		if (plugin.getManager().sqlManager.toggleSettingStatus(p.getName(), "doubleJump")) {
			p.sendMessage(Main.prefix + ChatColor.GREEN + "Ayar başarıyla değiştirildi!");
		}else {
			p.sendMessage(Main.prefix + ChatColor.RED + "Bir sorun oluştuğu için ayar değiştirilemedi!");
		}
	}

	public void toggleChat(Player p) {
		plugin.getManager().toggleChat(p);
		if (plugin.getManager().sqlManager.toggleSettingStatus(p.getName(), "closeChat")) {
			p.sendMessage(Main.prefix + ChatColor.GREEN + "Ayar başarıyla değiştirildi!");
		}else {
			p.sendMessage(Main.prefix + ChatColor.RED + "Bir sorun oluştuğu için ayar değiştirilemedi!");
		}
	}
	
	public void toggleSitting(Player p) {
		plugin.getManager().toggleSitting(p);
		if (plugin.getManager().sqlManager.toggleSettingStatus(p.getName(), "sittingEnabled")) {
			p.sendMessage(Main.prefix + ChatColor.GREEN + "Ayar başarıyla değiştirildi!");
		}else {
			p.sendMessage(Main.prefix + ChatColor.RED + "Bir sorun oluştuğu için ayar değiştirilemedi!");
		}
	}

	public void toggleStacker(Player p) {
		plugin.getManager().toggleStacking(p);
		if (plugin.getManager().sqlManager.toggleSettingStatus(p.getName(), "closeStacking")) {
			p.sendMessage(Main.prefix + ChatColor.GREEN + "Ayar başarıyla değiştirildi!");
		}else {
			p.sendMessage(Main.prefix + ChatColor.RED + "Bir sorun oluştuğu için ayar değiştirilemedi!");
		}
	}

	public String getDisplayName(Player p) {
		String prefix = plugin.getManager().getChat().getPlayerPrefix(p);
		return ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.translateAlternateColorCodes('&', p.getName());
	}
}
