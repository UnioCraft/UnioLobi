package me.uniodex.uniolobi.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.objects.Cage;
import me.uniodex.uniolobi.objects.Item;
import me.uniodex.uniolobi.objects.Kit;
import me.uniodex.uniolobi.objects.Trail;
import me.uniodex.uniolobi.utils.Base64;
import me.uniodex.uniolobi.utils.ItemStackBuilder;
import me.uniodex.uniolobi.utils.Reflections;
import me.uniodex.uniolobi.utils.packages.itembuilder.ItemBuilder;
import me.uniodex.uniolobi.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import me.uniodex.uniolobi.utils.packages.menubuilder.inventory.ItemListener;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

//TODO Full Recode
@SuppressWarnings("deprecation")
public class SkywarsMarketManager implements Listener{

	private Main plugin;
	
	public HashMap<Player, ArrayList<String>> playerKits;
	public HashMap<Player, ArrayList<String>> playerCages;
	public HashMap<Player, ArrayList<String>> playerTrails;
	
	public HashMap<String, Kit> kits;
	public HashMap<String, Cage> cages;
	public HashMap<String, Trail> trails;

	public SkywarsMarketManager(Main plugin) {
		this.plugin = plugin;
		reloadConfigData("all");
		loadKits();
		loadCages();
		loadTrails();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				loadPlayerItems(p);
			}
		}, 1L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		unloadPlayerItems(p);
	}

	public void openMenu(Player p, InventoryMenuBuilder menu) {
		if (p != null && menu != null && menu.build() != null) {
			menu.show(p);
		}
	}

	public int getPlayerCoins(String p) {
		String QUERY = "SELECT * FROM `skywars`.`usw_coin` WHERE player = '" + p + "';";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getInt("coin");
			}else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void loadPlayerItems(Player p) {
		// Kits
		ArrayList<String> playerKits = new ArrayList<String>();
		playerKits.add("Default");
		String kitQuery = "SELECT kit FROM `skywars`.`usw_kit` WHERE player = '"+p.getName()+"';";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(kitQuery);
			ResultSet res = statement.executeQuery();
			while (res.next())
			{
				ResultSetMetaData resmd = res.getMetaData();
				int columnCount = resmd.getColumnCount();
				if (columnCount != 0) {
					for (int i = 1; i <= columnCount; i++) {
						if (!res.getString(i).isEmpty()) {
							playerKits.add(res.getString(i));  
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (isVIP(p.getName())) {
			playerKits.add("soloZirhci");
			playerKits.add("duoZirhci");
			playerKits.add("soloBalikci");
			playerKits.add("duoBalikci");
			playerKits.add("soloBalcik");
			playerKits.add("duoBalcik");
		}

		this.playerKits.put(p, playerKits);


		// Cages
		ArrayList<String> playerCages = new ArrayList<String>();
		playerCages.add("Default");
		String cageQuery = "SELECT cage FROM `skywars`.`usw_cage` WHERE player = '"+p.getName()+"';";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(cageQuery);
			ResultSet res = statement.executeQuery();
			while (res.next())
			{
				ResultSetMetaData resmd = res.getMetaData();
				int columnCount = resmd.getColumnCount();
				if (columnCount != 0) {
					for (int i = 1; i <= columnCount; i++) {
						if (!res.getString(i).isEmpty()) {
							playerCages.add(res.getString(i));  
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (isVIP(p.getName())) {
			playerCages.add("soloGorunmez");
			playerCages.add("duoGorunmez");
		}

		this.playerCages.put(p, playerCages);

		// Trails
		ArrayList<String> playerTrails  = new ArrayList<String>();
		playerTrails.add("Default");
		String trailQuery = "SELECT trail FROM `skywars`.`usw_trail` WHERE player = '"+p.getName()+"';";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(trailQuery);
			ResultSet res = statement.executeQuery();
			while (res.next())
			{
				ResultSetMetaData resmd = res.getMetaData();
				int columnCount = resmd.getColumnCount();
				if (columnCount != 0) {
					for (int i = 1; i <= columnCount; i++) {
						if (!res.getString(i).isEmpty()) {
							playerTrails.add(res.getString(i));
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (isVIP(p.getName())) {
			playerTrails.add("soloElmas");
			playerTrails.add("duoElmas");
			playerTrails.add("soloTNT");
			playerTrails.add("duoTNT");
		}

		this.playerTrails.put(p, playerTrails);
	}

	public void unloadPlayerItems(Player p) {
		if (this.playerKits.containsKey(p)) {
			this.playerKits.remove(p);
		}
		if (this.playerCages.containsKey(p)) {
			this.playerCages.remove(p);
		}
		if (this.playerTrails.containsKey(p)) {
			this.playerTrails.remove(p);
		}
	}

	public boolean playerExists(String player, String table)
	{
		String QUERY = "SELECT * FROM `skywars`.`"+table+"` WHERE player = '" + player.toString() + "';";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				if (res.getString("player") == null) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean takeCoins(String player, Integer coins)
	{
		if (playerExists(player, "usw_coin")) 
		{
			if (getPlayerCoins(player) >= coins) {
				int ayarlanacakCoin = getPlayerCoins(player) - coins; //Mevcut coin - Azaltılacak coin
				if (plugin.getManager().sqlManager.updateSQL("UPDATE `skywars`.`usw_coin` SET coin='" + ayarlanacakCoin + "' WHERE player='" + player + "';"))
				{
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}else {
			createPlayer(player);
			return takeCoins(player, coins);
		}
	}

	public boolean giveCoins(String player, Integer coins)
	{
		if (playerExists(player, "usw_coin"))
		{
			int ayarlanacakCoin = getPlayerCoins(player) + coins; //Mevcut coin + Verilecek coin
			if (plugin.getManager().sqlManager.updateSQL("UPDATE `skywars`.`usw_coin` SET coin='" + ayarlanacakCoin + "' WHERE player='" + player + "';"))
			{
				return true;
			}else {
				return false;
			}
		}else {
			createPlayer(player);
			return giveCoins(player, coins);
		}
	}

	public boolean setCoins(String player, Integer coins)
	{
		if (playerExists(player, "usw_coin"))
		{
			if (plugin.getManager().sqlManager.updateSQL("UPDATE `skywars`.`usw_coin` SET coin='" + coins + "' WHERE player='" + player + "';"))
			{
				return true;
			}else {
				return false;
			}
		}else {
			createPlayer(player);
			return setCoins(player, coins);
		}
	}

	public void createPlayer(String player) {
		if (!playerExists(player, "usw_coin")) {
			plugin.getManager().sqlManager.updateSQL("INSERT INTO `skywars`.`usw_coin` (`id`, `player`, `coin`) VALUES (NULL, '"+player+"', '0');");
		}
		if (!playerExists(player, "usw_selected_solo")) {
			plugin.getManager().sqlManager.updateSQL("INSERT INTO `skywars`.`usw_selected_solo` (`id`, `player`) VALUES (NULL, '"+player+"');");
		}
		if (!playerExists(player, "usw_selected_duo")) {
			plugin.getManager().sqlManager.updateSQL("INSERT INTO `skywars`.`usw_selected_duo` (`id`, `player`) VALUES (NULL, '"+player+"');");
		}
	}

	public boolean playerHaveItem(String p, String itemid, String itemType) {
		String serverType;
		if (itemType.contains("solo")) {
			serverType = "solo";
		}else {
			serverType = "duo";
		}

		String actualItemType = itemType.replace(serverType, "");

		if (actualItemType.equalsIgnoreCase("kit")) {
			if (playerKits.get(Bukkit.getPlayer(p)).contains(serverType+itemid)) {
				return true;
			}else {
				return false;
			}
		}
		if (actualItemType.equalsIgnoreCase("cage")) {
			if (playerCages.get(Bukkit.getPlayer(p)).contains(serverType+itemid)) {
				return true;
			}else {
				return false;
			}
		}
		if (actualItemType.equalsIgnoreCase("trail")) {
			if (playerTrails.get(Bukkit.getPlayer(p)).contains(serverType+itemid)) {
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	public void buyItem(String p, String itemid, String itemType, int coin) {
		if (this.playerHaveItem(p, itemid, itemType) || itemid.equalsIgnoreCase("Default")) {
			Bukkit.getPlayer(p).sendMessage("§2[§bUnioCraft§2] §cZaten bu eşyaya sahip olduğunuzdan satın alamadınız!");
		}else {
			if (getPlayerCoins(p) >= coin) {
				String serverType;
				if (itemType.contains("solo")) {
					serverType = "solo";
				}else {
					serverType = "duo";
				}

				String actualItemType = itemType.replace(serverType, "");

				String QUERY = "INSERT INTO `skywars`.`usw_"+actualItemType+"` (`id`, `player`, `"+actualItemType+"`) VALUES (NULL, '"+p+"', '"+serverType+itemid+"');";
				try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
					PreparedStatement statement = connection.prepareStatement(QUERY);
					statement.execute();
					takeCoins(p, coin);
					Bukkit.getPlayer(p).sendMessage("§2[§bUnioCraft§2] §aTebrikler! Satın alım başarılı!");
					unloadPlayerItems(Bukkit.getPlayer(p));
					loadPlayerItems(Bukkit.getPlayer(p));
					selectItem(p, itemid, itemType);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else {
				Bukkit.getPlayer(p).sendMessage("§2[§bUnioCraft§2] §cYeterli miktarda uCoin'iniz olmadığı için satın alma işlemi başarısız!");
			}
		}
	}

	public void selectItem(String p, String itemid, String itemType) {
		if (this.playerHaveItem(p, itemid, itemType) || itemid.equalsIgnoreCase("Default")) {
			String serverType;
			if (itemType.contains("solo")) {
				serverType = "solo";
			}else {
				serverType = "duo";
			}

			String actualItemType = itemType.replace(serverType, "");
			String QUERY = "";
			if (!playerExists(p, "usw_selected_"+serverType)) {
				if (actualItemType.equalsIgnoreCase("kit")) {
					QUERY = "INSERT INTO `skywars`.`usw_selected_"+serverType+"` (`id`, `player`, `kit`, `cage`, `trail`) VALUES (NULL, '"+p+"', '"+itemid+"', 'Default', 'Default');";	
				}
				if (actualItemType.equalsIgnoreCase("cage")) {
					QUERY = "INSERT INTO `skywars`.`usw_selected_"+serverType+"` (`id`, `player`, `kit`, `cage`, `trail`) VALUES (NULL, '"+p+"', 'Default', '"+itemid+"', 'Default');";		
				}
				if (actualItemType.equalsIgnoreCase("trail")) {
					QUERY = "INSERT INTO `skywars`.`usw_selected_"+serverType+"` (`id`, `player`, `kit`, `cage`, `trail`) VALUES (NULL, '"+p+"', 'Default', 'Default', '"+itemid+"');";		
				}
				try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
					PreparedStatement statement = connection.prepareStatement(QUERY);
					statement.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else {
				QUERY = "UPDATE `skywars`.`usw_selected_"+serverType+"` SET `"+actualItemType+"` = '"+itemid+"' WHERE `usw_selected_"+serverType+"`.`player` = '"+p+"';";
				try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
					PreparedStatement statement = connection.prepareStatement(QUERY);
					statement.execute();
					Bukkit.getPlayer(p).sendMessage("§2[§bUnioCraft§2] §aEşya başarıyla seçildi.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else {
			Bukkit.getPlayer(p).sendMessage("§2[§bUnioCraft§2] §cHata! Bu eşyaya sahip olmadığınızdan eşyayı seçemediniz!");
		}
	}

	public String getItemName(ItemStack item) {
		return item.getType().toString();
	}

	public ItemStack getItem(Player p, Item item, String itemType) {
		boolean greyedOut = false, buyable = false, chooseable = false, youDontHavethat = false, vipOzel = false;
		if (this.playerHaveItem(p.getName(), item.itemId, itemType) || item.itemId.equalsIgnoreCase("Default")) {
			chooseable = true;
		}else {
			//greyedOut = true;
			//youDontHavethat = true;
			buyable = true;
		}
		
		if (!item.permission.equalsIgnoreCase("NONE")) {
			vipOzel = true;
			//youDontHavethat = false;
			buyable = false;
		}

		List<String> splittedDesc = new ArrayList<String>();
		String itemInfoInstance = item.itemInfo;
		String[] itemInfoSplitted = itemInfoInstance.split("\n");
		for (String itemInfo : itemInfoSplitted) {
			if (itemInfo.length() > 35) {
				String[] splitted = splitString(itemInfo, 4).split("\n");
				for (String itemInfomsg : splitted) {
					splittedDesc.add(itemInfomsg);
				}
			}else {
				splittedDesc.add(itemInfo);
			}
		}

		ItemStack itemIcon = item.itemIcon;
		if (greyedOut) {
			itemIcon = new ItemStack(Material.STAINED_GLASS_PANE);
			itemIcon.setDurability((short) 14);
		}

		List<String> lore = new ArrayList<String>(); 

		for (String lorePart : splittedDesc) {
			if (lorePart.length() > 0 ) {
				lore.add("§7"+ChatColor.translateAlternateColorCodes('&', lorePart));
			}
		}

		lore.add(" ");
		String rarityInstance = item.rarity;
		if (rarityInstance.equalsIgnoreCase("yaygin")) {
			rarityInstance = "§aYaygın";
		} else if (rarityInstance.equalsIgnoreCase("nadir")) {
			rarityInstance = "§5Nadir";
		} else if (rarityInstance.equalsIgnoreCase("efsanevi")) {
			rarityInstance = "§cEfsanevi";
		} else if (rarityInstance.equalsIgnoreCase("destansi")) {
			rarityInstance = "§dDestansı";
		}
		int costInstance = item.cost;
		lore.add("§7Nadirlik: "+ rarityInstance);
		if (!vipOzel) 
		{
			lore.add("§7Fiyat: §2"+ NumberFormat.getNumberInstance(Locale.GERMAN).format(costInstance) + " uCoin");
		}
		lore.add(" ");
		if (buyable) {
			lore.add("§a§lSatın almak için tıkla!");
		}
		if (chooseable) {
			if (itemType.contains("cage")) {
				lore.add("§a§lBu kafesi seçmek için tıkla!");
			}else if (itemType.contains("kit")) {
				lore.add("§a§lBu kiti seçmek için tıkla!");
			}else if (itemType.contains("trail")) {
				lore.add("§a§lBu iz efektini seçmek için tıkla!");
			}
		}

		if (vipOzel) {
			if (!this.playerHaveItem(p.getName(), item.itemId, itemType)) {
			lore.add("§c§lBu eşya VIP oyunculara özeldir!");
			}
		}

		if (youDontHavethat) {
			lore.add("§c§lBu kite sahip değilsiniz!");
		}

		ItemMeta meta = itemIcon.getItemMeta();
		meta.setDisplayName("§6§l"+item.itemName);
		meta.setLore(lore);
		itemIcon.setItemMeta(meta);
		return itemIcon;

	}

	public void loadKits() {
		kits = new HashMap<String, Kit>();
		FileConfiguration kitsFile = getConfigData("kits");
		for(String kitId : kitsFile.getConfigurationSection("Kits").getKeys(false)){
			String path = "Kits." + kitId + ".";

			String kitName = kitsFile.getString(path + "name");
			String kitDesc = kitsFile.getString(path + "description");
			String preKitIcon = kitsFile.getString(path + "icon");
			String rarity = kitsFile.getString(path + "rarity");
			int cost = kitsFile.getInt(path + "cost");
			String permission = kitsFile.getString(path + "permission");
			ArrayList<ItemStack> kitItems = new ArrayList<ItemStack>();
			for(String item : kitsFile.getStringList(path + "items")) {
				ItemStack kitItem = getItemStack(item, true, true);
				kitItems.add(kitItem);
			}

			kitName = ChatColor.translateAlternateColorCodes('&', kitName);
			ItemStack kitIcon = getItemStack(preKitIcon, true, true);

			kits.put(kitId, new Kit(kitId, kitName, kitIcon, kitDesc, rarity, cost, permission, kitItems));
		}
		this.playerKits = new HashMap<Player, ArrayList<String>>();
	}

	public void loadCages() {
		cages = new HashMap<String, Cage>();
		FileConfiguration cagesFile = getConfigData("cages");
		for(String cageId : cagesFile.getConfigurationSection("Cages").getKeys(false)){
			String path = "Cages." + cageId + ".";

			String cageName = cagesFile.getString(path + "name");
			String cageDesc = cagesFile.getString(path + "description");
			String preCageIcon = cagesFile.getString(path + "icon");
			String rarity = cagesFile.getString(path + "rarity");
			int cost = cagesFile.getInt(path + "cost");
			String permission = cagesFile.getString(path + "permission");

			/* 
			 * 0 = ceiling
			 * 1 = ceilingBorder
			 * 2 = higherMiddle
			 * 3 = higherMiddleBorder
			 * 4 = middle
			 * 5 = middleBorder
			 * 6 = lowerMiddle
			 * 7 = lowerMiddleBorder
			 * 8 = floorBorder
			 * 9 = floor
			 */
			String[] cagePartsString = new String[] {"ceiling", "ceilingBorder", "higherMiddle", "higherMiddleBorder", "middle", "middleBorder", "lowerMiddle", "lowerMiddleBorder", "floorBorder", "floor"};
			ItemStack[] cageParts = new ItemStack[10];
			for(int i = 0; i < cageParts.length; i++){
				cageParts[i] = getItemStack(cagesFile.getString(path + cagePartsString[i]), false, false);
			}

			cageName = ChatColor.translateAlternateColorCodes('&', cageName);
			ItemStack cageIcon = getItemStack(preCageIcon, true, true);

			cages.put(cageId, new Cage(cageId, cageName, cageIcon, cageDesc, rarity, cost, permission, cageParts));
		}
		this.playerCages = new HashMap<Player, ArrayList<String>>();
	}

	public void loadTrails() {
		trails = new HashMap<String, Trail>();
		FileConfiguration trailsFile = getConfigData("trails");
		for(String trailId : trailsFile.getConfigurationSection("Trails").getKeys(false)){
			String path = "Trails." + trailId + ".";

			String trailName = trailsFile.getString(path + "name");
			String trailDesc = trailsFile.getString(path + "description");
			String preTrailIcon = trailsFile.getString(path + "icon");
			String rarity = trailsFile.getString(path + "rarity");
			int cost = trailsFile.getInt(path + "cost");
			String permission = trailsFile.getString(path + "permission");

			ItemStack trailItem = getItemStack(trailsFile.getString(path + "icon"), false, false);

			trailName = ChatColor.translateAlternateColorCodes('&', trailName);
			ItemStack trailIcon = getItemStack(preTrailIcon, true, true);

			trails.put(trailId, new Trail(trailId, trailName, trailIcon, trailDesc, rarity, cost, permission, trailItem));
		}
		this.playerTrails = new HashMap<Player, ArrayList<String>>();
	}


	public ItemStack addPotionEffect(ItemStack potion, PotionEffectType effect, int duration, int amplifier, boolean isSplashable) {
		ItemStack ptt = potion;
		Potion pot = Potion.fromItemStack(ptt);
		pot.setSplash(isSplashable);
		ItemStack pt = pot.toItemStack(ptt.getAmount());
		PotionMeta pm = (PotionMeta) pt.getItemMeta();

		pm.addCustomEffect(new PotionEffect(effect, duration * 20, amplifier - 1), true);

		pt.setItemMeta(pm);
		return pt;
	}

	public ItemStack addGlow(ItemStack item){
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null) tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

	public ItemStack getItemStack(String item, boolean amount, boolean extra){
		String[] split = item.split(" : ");	
		if (split[0].equalsIgnoreCase("ozelIksirAtestenKorunma")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.FIRE_RESISTANCE, 5, 1, false));
			builder.setName("§5Ateşten Korunma İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("ozelIksirRegen2")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.REGENERATION, 16, 2, true));
			builder.setName("§aPatlayıcı Yenilenme İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("ozelIksirZehir")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.POISON, 12, 1, true));
			builder.setName("§cPatlayıcı Zehir İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("ozelIksirCeviklik")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.SPEED, 67, 2, true));
			builder.setName("§bPatlayıcı Çeviklik İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("korlukIksiri")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.BLINDNESS, 8, 1, true));
			builder.setName("§0Yarasa Adamın İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("gucIksiri")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.INCREASE_DAMAGE, 5, 1, false));
			builder.setName("§6Güç İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("kurbagaIksiri")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(addPotionEffect(pot, PotionEffectType.JUMP, 20, 2, true), PotionEffectType.SPEED, 20, 2, true));
			builder.setName("§2Kurbağanın İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("hiz2Iksiri")) {
			ItemStack pot = new ItemStack(Material.POTION);
			ItemStackBuilder builder = new ItemStackBuilder(addPotionEffect(pot, PotionEffectType.SPEED, 15, 2, true));
			builder.setName("§bAtılabilir Hız İksiri");
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("rastgeleMuzikDiski")) {
			ItemStack[] disk = new ItemStack[] {new ItemStack(Material.RECORD_3), new ItemStack(Material.RECORD_4), new ItemStack(Material.RECORD_5), new ItemStack(Material.RECORD_6), new ItemStack(Material.RECORD_7), new ItemStack(Material.RECORD_8), new ItemStack(Material.RECORD_9), new ItemStack(Material.RECORD_10), new ItemStack(Material.RECORD_11), new ItemStack(Material.RECORD_12)};
			Random generator = new Random();
			int randomIndex = generator.nextInt(disk.length);
			ItemStackBuilder builder = new ItemStackBuilder(disk[randomIndex]);
			if(amount) builder.setAmount(Integer.valueOf(split[1]));
			return builder.build();
		}
		if (split[0].equalsIgnoreCase("kurbagaKafasi")) {
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			PropertyMap propertyMap = profile.getProperties();
			if (propertyMap == null) {
				throw new IllegalStateException("Profile doesn't contain a property map");
			}
			String encodedData = Base64.encodeBytes(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/d2c3b98ada19957f8d83a7d42faf81a290fae7d08dbf6c1f8992a1ada44b31").getBytes());
			propertyMap.put("textures", new Property("textures", encodedData));
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			ItemMeta headMeta = head.getItemMeta();
			Class<?> headMetaClass = headMeta.getClass();
			Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
			head.setItemMeta(headMeta);

			if(amount) head.setAmount(Integer.valueOf(split[1]));
			return head;
		}

		ItemStackBuilder builder = new ItemStackBuilder(split[0].contains(":") ? Material.getMaterial(split[0].split(":")[0].toUpperCase()) : Material.getMaterial(split[0].toUpperCase()));
		if(amount) builder.setAmount(Integer.valueOf(split[1]));
		if(split[0].contains(":")) builder.setDurability(Integer.valueOf(split[0].split(":")[1]));
		if(extra){
			for(int i = amount ? 2 : 1; i < split.length; i++){
				String type = split[i].split(":")[0].toLowerCase();
				if(type.equals("name")) builder.setName(ChatColor.translateAlternateColorCodes('&', split[i].split(":")[1])); else
					if(type.equals("lore")) builder.addLore(ChatColor.translateAlternateColorCodes('&', split[i].split(":")[1])); else
						if(type.equals("enchant")) {
							int enchantAmount = (split[i].split(":").length) / 2;
							int d = 0;
							while (d < enchantAmount) {
								builder.addEnchantment(Enchantment.getByName(split[i].split(":")[d+1].toUpperCase()), Integer.valueOf(split[i].split(":")[d+2]));
								d += 2;
							}
						}
			}
		}
		return builder.build();
	}
	
	public boolean isVIP(String player)
	{
		String QUERY = "SELECT * FROM `skywars`.`zp_memberships` WHERE `member` = '"+player+"' AND `expiration` >= 'CURDATE()'";
		try ( Connection connection = plugin.getManager().sqlManager.getPool().getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				if (res.getString("member") == null) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public final String WHITESPACE = " ";
	public final String LINEBREAK = System.getProperty("line.separator");

	/**
	 * Insert line-breaks into the text so that each line has maximum number of words.
	 *
	 * @param text         the text to insert line-breaks into
	 * @param wordsPerLine maximum number of words per line
	 * @return a new text with linebreaks
	 */
	public String splitString(String text, int wordsPerLine)
	{
		final StringBuilder newText = new StringBuilder();

		final StringTokenizer wordTokenizer = new StringTokenizer(text);
		long wordCount = 1;
		while (wordTokenizer.hasMoreTokens())
		{
			newText.append(wordTokenizer.nextToken());
			if (wordTokenizer.hasMoreTokens())
			{
				if (wordCount++ % wordsPerLine == 0)
				{
					newText.append(LINEBREAK);
				}
				else
				{
					newText.append(WHITESPACE);
				}
			}
		}
		return newText.toString();
	}

	private FileConfiguration kitData = null;
	private File kitDataFile = null;
	private FileConfiguration cageData = null;
	private File cageDataFile = null;
	private FileConfiguration trailData = null;
	private File trailDataFile = null;

	public void reloadConfigData(String type) {
		if (type.equalsIgnoreCase("kits")) {
			if (kitDataFile == null) {
				kitDataFile = new File(plugin.getDataFolder(), "kits.yml");
				try {
					kitDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			kitData = YamlConfiguration.loadConfiguration(kitDataFile);
		}
		if (type.equalsIgnoreCase("cages")) {
			if (cageDataFile == null) {
				cageDataFile = new File(plugin.getDataFolder(), "cages.yml");
				try {
					cageDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			cageData = YamlConfiguration.loadConfiguration(cageDataFile);
		}
		if (type.equalsIgnoreCase("trails")) {
			if (trailDataFile == null) {
				trailDataFile = new File(plugin.getDataFolder(), "trails.yml");
				try {
					trailDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			trailData = YamlConfiguration.loadConfiguration(trailDataFile);
		}
		if (type.equalsIgnoreCase("all")) {
			if (trailDataFile == null) {
				trailDataFile = new File(plugin.getDataFolder(), "trails.yml");
				try {
					trailDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			trailData = YamlConfiguration.loadConfiguration(trailDataFile);
			if (cageDataFile == null) {
				cageDataFile = new File(plugin.getDataFolder(), "cages.yml");
				try {
					cageDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			cageData = YamlConfiguration.loadConfiguration(cageDataFile);
			if (kitDataFile == null) {
				kitDataFile = new File(plugin.getDataFolder(), "kits.yml");
				try {
					kitDataFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			kitData = YamlConfiguration.loadConfiguration(kitDataFile);
		}
	}

	public FileConfiguration getConfigData(String type) {
		if (type.equalsIgnoreCase("kits")) {
			if (kitData == null) {
				reloadConfigData("kits");
			}
			return kitData;
		}
		if (type.equalsIgnoreCase("trails")) {
			if (trailData == null) {
				reloadConfigData("trails");
			}
			return trailData;
		}
		if (type.equalsIgnoreCase("cages")) {
			if (cageData == null) {
				reloadConfigData("cages");
			}
			return cageData;
		}
		return null;
	}

	public void saveConfigData(String type) {
		if (type.equalsIgnoreCase("kits")) {
			if (kitData == null || kitDataFile == null) {
				return;
			}
			try {
				getConfigData("kits").save(kitDataFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + kitDataFile, ex);
			}
		}
		if (type.equalsIgnoreCase("cages")) {
			if (cageData == null || cageDataFile == null) {
				return;
			}
			try {
				getConfigData("cages").save(cageDataFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + cageDataFile, ex);
			}
		}
		if (type.equalsIgnoreCase("trails")) {
			if (trailData == null || trailDataFile == null) {
				return;
			}
			try {
				getConfigData("trails").save(trailDataFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + trailDataFile, ex);
			}
		}
	}
	

	public InventoryMenuBuilder mainMarketMenu() {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(9, "§1§lMarket");
		ItemStack skywarsItem = new ItemBuilder(Material.FIREBALL, 1, 0).buildMeta().withDisplayName("§6§lSkywars").withLore("§aSkywars marketi için buraya tıkla!").item().build();

		menu.withItem(0, skywarsItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, marketMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder marketMenu() {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(36, "§1§lSkywars Market");
		ItemStack kitItem = new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1, 0).buildMeta().withDisplayName("§6§lKitler").withLore("§aKitler için tıkla!").item().build();
		ItemStack kafesItem = new ItemBuilder(Material.IRON_FENCE, 1, 0).buildMeta().withDisplayName("§6§lKafesler").withLore("§aÖzel kafesler için tıkla!").item().build();
		ItemStack izItem = new ItemBuilder(Material.ARROW, 1, 0).buildMeta().withDisplayName("§6§lİz Efektleri").withLore("§aÖzel iz efektleri için tıkla!").item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(11, kitItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kitMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(13, kafesItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kafesMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(15, izItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, izMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(31, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, mainMarketMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder kitMenu() {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(36, "§1§lSkywars Kit Marketi"); 
		ItemStack soloItem = new ItemBuilder(Material.SKULL_ITEM, 1, 1).buildMeta().withDisplayName("§6§lSolo").withLore("§aSolo Skywars kitleri için buraya tıkla!").item().build();
		ItemStack duoItem = new ItemBuilder(Material.SKULL_ITEM, 2, 1).buildMeta().withDisplayName("§6§lDuo").withLore("§aDuo Skywars kitleri için buraya tıkla!").item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(12, soloItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, soloKitMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(14, duoItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, duoKitMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(31, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, marketMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder kafesMenu() {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(36, "§1§lSkywars Kafes Marketi"); 
		ItemStack soloItem = new ItemBuilder(Material.SKULL_ITEM, 1, 1).buildMeta().withDisplayName("§6§lSolo").withLore("§aSolo Skywars kafesleri için buraya tıkla!").item().build();
		ItemStack duoItem = new ItemBuilder(Material.SKULL_ITEM, 2, 1).buildMeta().withDisplayName("§6§lDuo").withLore("§aDuo Skywars kafesleri için buraya tıkla!").item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(12, soloItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, soloKafesMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(14, duoItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, duoKafesMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(31, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, marketMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder izMenu() {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(36, "§1§lSkywars İz Efekti Marketi"); 
		ItemStack soloItem = new ItemBuilder(Material.SKULL_ITEM, 1, 1).buildMeta().withDisplayName("§6§lSolo").withLore("§aSolo Skywars iz efektleri için buraya tıkla!").item().build();
		ItemStack duoItem = new ItemBuilder(Material.SKULL_ITEM, 2, 1).buildMeta().withDisplayName("§6§lDuo").withLore("§aDuo Skywars iz efektleri için buraya tıkla!").item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(12, soloItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, soloIzMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(14, duoItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, duoIzMenu(player));
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(31, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, marketMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder confirmMenu(String itemName, String itemId, String itemType, Integer coins, InventoryMenuBuilder previousMenu) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(9, "§1§lSatın almak istediğine emin misin?");
		List<String> evetLore = new ArrayList<String>();
		evetLore.add("§6" + itemName+" §bisimli eşyayı");
		evetLore.add("§2"+coins.toString()+" §buCoin karşılığında");
		evetLore.add("§bsatın almak istiyorum.");

		List<String> hayirLore = new ArrayList<String>();
		hayirLore.add("§6" + itemName+" §disimli eşyayı");
		hayirLore.add("§2"+coins.toString()+" §duCoin karşılığında");
		hayirLore.add("§dsatın almak istemiyorum.");

		ItemStack evetItem = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§a§lEvet").withLore(evetLore).item().build();
		ItemStack hayirItem = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§c§lHayır").withLore(hayirLore).item().build();

		menu.withItem(3, evetItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				player.closeInventory();
				Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
					public void run() {
						buyItem(player.getName(), itemId, itemType, coins);
					}
				}, 1L);
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		menu.withItem(5, hayirItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, previousMenu);
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		return menu;
	}

	public InventoryMenuBuilder soloKitMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Solo Kit Marketi"); 
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kitMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "solokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "solokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "solokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && !playerHaveItem(player.getName(), kit.itemId, "solokit")) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "solokit", kit.cost, soloKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "solokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "solokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "solokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && !playerHaveItem(player.getName(), kit.itemId, "solokit")) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "solokit", kit.cost, soloKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "solokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "solokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "solokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && !playerHaveItem(player.getName(), kit.itemId, "solokit")) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "solokit", kit.cost, soloKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}

		return menu;
	}

	public InventoryMenuBuilder duoKitMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Duo Kit Marketi"); 
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kitMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);
		
		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "duokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "duokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "duokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), kit.itemId, "duokit"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "duokit", kit.cost, duoKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "duokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "duokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "duokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), kit.itemId, "duokit"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "duokit", kit.cost, duoKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Kit> kitEntry : kits.entrySet()) {
			Kit kit = kitEntry.getValue();
			if (kit.itemId.equalsIgnoreCase("Default") || !kit.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, kit, "duokit"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), kit.itemId, "duokit") || kit.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), kit.itemId, "duokit");
							}
						}, 1L);
					}else if (!kit.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), kit.itemId, "duokit"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(kit.itemName, kit.itemId, "duokit", kit.cost, duoKitMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}

		return menu;
	}

	public InventoryMenuBuilder soloKafesMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Solo Kafes Marketi"); 
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kafesMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "solocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "solocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "solocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "solocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "solocage", cage.cost, soloKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "solocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "solocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "solocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "solocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "solocage", cage.cost, soloKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("destansi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "solocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "solocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "solocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "solocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "solocage", cage.cost, soloKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "solocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "solocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "solocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "solocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "solocage", cage.cost, soloKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		return menu;
	}

	public InventoryMenuBuilder duoKafesMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Duo Kafes Marketi"); 
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, kafesMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "duocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "duocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "duocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "duocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "duocage", cage.cost, duoKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "duocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "duocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "duocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "duocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "duocage", cage.cost, duoKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("destansi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "duocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "duocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "duocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "duocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "duocage", cage.cost, duoKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Cage> cageEntry : cages.entrySet()) {
			Cage cage = cageEntry.getValue();
			if (cage.itemId.equalsIgnoreCase("Default") || !cage.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, cage, "duocage"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), cage.itemId, "duocage") || cage.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), cage.itemId, "duocage");
							}
						}, 1L);
					}else if (!cage.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), cage.itemId, "duocage"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(cage.itemName, cage.itemId, "duocage", cage.cost, duoKafesMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}

		return menu;
	}

	public InventoryMenuBuilder soloIzMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Solo İz Marketi"); 
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, izMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "solotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "solotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "solotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "solotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "solotrail", trail.cost, soloIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "solotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "solotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "solotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "solotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "solotrail", trail.cost, soloIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "solotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "solotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "solotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "solotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "solotrail", trail.cost, soloIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		return menu;
	}

	public InventoryMenuBuilder duoIzMenu(Player p) {
		InventoryMenuBuilder menu = new InventoryMenuBuilder(54, "§1§lSkywars Duo İz Marketi");
		ItemStack coinItem = new ItemBuilder(Material.EMERALD, 1, 0).buildMeta().withDisplayName("§6§luCoin Miktarı").withLore("§7uCoin miktarınız: §a" + getPlayerCoins(p.getName())).item().build();
		ItemStack oncekiMenuItem = new ItemBuilder(Material.BARRIER, 1, 0).buildMeta().withDisplayName("§c§lÖnceki Menü").withLore("§dÖnceki menüye dönmek için tıkla!").item().build();

		menu.withItem(48, coinItem);
		menu.withItem(50, oncekiMenuItem, new ItemListener() {
			@Override
			public void onInteract(Player player, ClickType action, ItemStack item) {
				openMenu(player, izMenu());
			}
		}, InventoryMenuBuilder.ALL_CLICK_TYPES);

		int menuSlot = 0;
		int i = 0;
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("yaygin")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "duotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "duotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "duotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "duotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "duotrail", trail.cost, duoIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("nadir")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "duotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "duotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "duotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "duotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "duotrail", trail.cost, duoIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}
		
		for (Entry<String, Trail> trailEntry : trails.entrySet()) {
			Trail trail = trailEntry.getValue();
			if (trail.itemId.equalsIgnoreCase("Default") || !trail.rarity.equalsIgnoreCase("efsanevi")) {
				continue;
			}
			
			if (i >= 0 && i <= 6) {
				menuSlot = i+10;
			}else if (i > 6 && i<= 13) {
				menuSlot = i+12;
			}else if (i > 13 && i<= 20) {
				menuSlot = i+14;
			}else if (i > 20 && i<= 27) {
				menuSlot = i+16;
			}else if (i > 27 && i<= 34) {
				menuSlot = i+18;
			}
			
			menu.withItem(menuSlot, getItem(p, trail, "duotrail"), new ItemListener() {
				@Override
				public void onInteract(Player player, ClickType action, ItemStack item) {
					player.closeInventory();
					if (playerHaveItem(p.getName(), trail.itemId, "duotrail") || trail.itemId.equalsIgnoreCase("Default")) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								selectItem(player.getName(), trail.itemId, "duotrail");
							}
						}, 1L);
					}else if (!trail.permission.equalsIgnoreCase("NONE") && (!playerHaveItem(player.getName(), trail.itemId, "duotrail"))) {
						player.sendMessage("§2[§bUnioCraft§2] §cBu eşya VIP oyunculara özeldir.");
					}else {
						openMenu(player, confirmMenu(trail.itemName, trail.itemId, "duotrail", trail.cost, duoIzMenu(p)));	
					}
				}
			}, InventoryMenuBuilder.ALL_CLICK_TYPES);
			i++;
		}

		return menu;
	}
}
