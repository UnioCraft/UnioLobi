package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ClickableItemManager {

	private ItemStack serverSelectorItem;
	private ItemStack gameMarketItem;

	private Main plugin;
	
	public ClickableItemManager(Main plugin) {
		this.plugin = plugin;
		initItems();
	}

	private void initItems() {
		initServerSelector();
		initGameMarket();
	}

	private void initServerSelector() {
		serverSelectorItem = new ItemStack(Material.COMPASS, 1);
		ItemMeta meta = serverSelectorItem.getItemMeta(); 
		meta.setDisplayName(ChatColor.GREEN + "Oyun Menüsü " + ChatColor.GRAY + "(Sağ Tıkla)");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Oyunlar arasında geçiş");
		lore.add(ChatColor.GRAY + "yapmak için sağ tıkla!");
		meta.setLore(lore);
		serverSelectorItem.setItemMeta(meta);
	}

	private void initGameMarket() {
		gameMarketItem = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = gameMarketItem.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Market " + ChatColor.GRAY + "(Sağ Tıkla)");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Kredi, cevher ve uCoin'lerini");
		lore.add(ChatColor.GRAY + "harcayabileceğin marketi açmak");
		lore.add(ChatColor.GRAY + "için sağ tıkla!");
		meta.setLore(lore);
		gameMarketItem.setItemMeta(meta);
	}

	public ItemStack getServerSelectorItem() {
		return serverSelectorItem;
	}

	public ItemStack getProfileItem(String playerName) {
		ItemStack profileItem = new ItemStack(Utils.getSkull(playerName, ChatColor.GREEN + "Profil ve Ayarlar " + ChatColor.GRAY + "(Sağ Tıkla)"));
		ItemMeta meta = profileItem.getItemMeta(); 
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Bu menüden istatistiklerinizi, kişisel");
		lore.add(ChatColor.GRAY + "tercihlerinizi görebilirsin. Ayrıca kişisel");
		lore.add(ChatColor.GRAY + "ayarlarını da düzenleyebilirsin!");
		lore.add(ChatColor.GRAY + "Menüyü açmak için sağ tıkla!");
		meta.setLore(lore);
		profileItem.setItemMeta(meta);
		return profileItem;
	}
	
	public ItemStack getFlyToggler(Player p) {
		ItemStack flyTogglerItem = new ItemStack(Material.FEATHER, 1);
		ItemMeta meta = flyTogglerItem.getItemMeta();
		if (plugin.getManager().flying.contains(p)) {
			meta.setDisplayName(ChatColor.RED + "Uçuş Modunu Kapat " + ChatColor.GRAY + "(Sağ Tıkla)");
		}else {
			meta.setDisplayName(ChatColor.GREEN + "Uçuş Modunu Aç " + ChatColor.GRAY + "(Sağ Tıkla)");	
		}
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Uçuş modunu açmak ya da");
		lore.add(ChatColor.GRAY + "kapatmak için sağ tıkla!");
		meta.setLore(lore);
		flyTogglerItem.setItemMeta(meta);
		return flyTogglerItem;
	}

	public ItemStack getGameMarketItem() {
		return gameMarketItem;
	}
}
