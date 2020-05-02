package me.uniodex.uniolobi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class Utils {

	public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
		if (useSubCardinalDirections)
			return radial[Math.round(yaw / 45f) & 0x7].getOppositeFace();

		return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
	}

	private static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

	public static Date parseTime(String time)
	{
		try
		{
			String[] frag = time.split("-");
			if (frag.length < 2) {
				return new Date();
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.parse(frag[0] + "-" + frag[1] + "-" + frag[2]);
		}
		catch (Exception e) {}
		return new Date();
	}

	public static ItemStack getSkull(String name, String displayName){
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(name);
		skull.setItemMeta(meta);
		ItemMeta itemMeta = skull.getItemMeta();
		itemMeta.setDisplayName(displayName);
		skull.setItemMeta(itemMeta);
		return skull;
	}

	public static ItemStack getSkullwithTexture(String texture, String displayName) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		String encodedData = Base64.encodeBytes(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/"+texture).getBytes());
		propertyMap.put("textures", new Property("textures", encodedData));
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headMeta = head.getItemMeta();
		headMeta.setDisplayName(displayName);
		Class<?> headMetaClass = headMeta.getClass();
		Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
		head.setItemMeta(headMeta);

		return head;
	}

	public static boolean compareItem(ItemStack item1, ItemStack item2){
		return item1 != null && item2 != null && item1.getType().equals(item2.getType()) && item1.getItemMeta().equals(item2.getItemMeta());
	}

	/*
	 * It compares items but it won't check entire ItemMeta.
	 */
	public static boolean isSimilar(ItemStack item1, ItemStack item2){
		return item1 != null && item2 != null && item1.getType().equals(item2.getType()) && item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName());
	}

	public static final String WHITESPACE = " ";
	public static final String LINEBREAK = System.getProperty("line.separator");

	/**
	 * Insert line-breaks into the text so that each line has maximum number of words.
	 *
	 * @param text         the text to insert line-breaks into
	 * @param wordsPerLine maximum number of words per line
	 * @return a new text with linebreaks
	 */
	public static String splitString(String text, int wordsPerLine)
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

	public static double round(double value) {
		return Math.round(value * 10.0) / 10.0;
	}

	public static int getServiceID(String service) {
		if (service.equalsIgnoreCase("Minecraft-MP.com")) {
			return 1;
		//} else if (service.equalsIgnoreCase("MCSL")) {
		} else if (service.equalsIgnoreCase("Minecraft-Server.net")) {
			return 2;
		} else if (service.equalsIgnoreCase("MinecraftServers.org")) {
			return 3;
		} else if (service.equalsIgnoreCase("TopG.org")) {
			return 4;
		}
		return -1;
	}

	public static String secondsToString(Long seconds) {
		long second = seconds % 60;
		long minute = seconds % 3600 / 60;
		long hours = seconds % 86400 / 3600;
		long day = seconds / 86400;

		if (day == 0 && minute == 0 && hours == 0) {
			return second + " saniye";
		}

		if (day == 0 && hours == 0) {
			return minute + " dakika, " + second + " saniye";
		}

		if (day == 0) {
			return hours + " saat, " + minute + " dakika, " + second + " saniye";
		}

		return day + " gün, " + hours + " saat, " + minute + " dakika, " + second + " saniye";
	}

	private final static int CENTER_PX = 154;
	public static void sendCenteredMessage(Player player, String message) {
		if(message == null || message.equals("")) player.sendMessage("");
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : message.toCharArray()){
			if(c == '§'){
				previousCode = true;
				continue;
			}else if(previousCode == true){
				previousCode = false;
				if(c == 'l' || c == 'L'){
					isBold = true;
					continue;
				}else isBold = false;
			}else{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while(compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb.toString() + message);
	}

	public enum DefaultFontInfo {
		A('A', 5),
		a('a', 5),
		B('B', 5),
		b('b', 5),
		C('C', 5),
		c('c', 5),
		D('D', 5),
		d('d', 5),
		E('E', 5),
		e('e', 5),
		F('F', 5),
		f('f', 4),
		G('G', 5),
		g('g', 5),
		H('H', 5),
		h('h', 5),
		I('I', 3),
		i('i', 1),
		J('J', 5),
		j('j', 5),
		K('K', 5),
		k('k', 4),
		L('L', 5),
		l('l', 1),
		M('M', 5),
		m('m', 5),
		N('N', 5),
		n('n', 5),
		O('O', 5),
		o('o', 5),
		P('P', 5),
		p('p', 5),
		Q('Q', 5),
		q('q', 5),
		R('R', 5),
		r('r', 5),
		S('S', 5),
		s('s', 5),
		T('T', 5),
		t('t', 4),
		U('U', 5),
		u('u', 5),
		V('V', 5),
		v('v', 5),
		W('W', 5),
		w('w', 5),
		X('X', 5),
		x('x', 5),
		Y('Y', 5),
		y('y', 5),
		Z('Z', 5),
		z('z', 5),
		NUM_1('1', 5),
		NUM_2('2', 5),
		NUM_3('3', 5),
		NUM_4('4', 5),
		NUM_5('5', 5),
		NUM_6('6', 5),
		NUM_7('7', 5),
		NUM_8('8', 5),
		NUM_9('9', 5),
		NUM_0('0', 5),
		EXCLAMATION_POINT('!', 1),
		AT_SYMBOL('@', 6),
		NUM_SIGN('#', 5),
		DOLLAR_SIGN('$', 5),
		PERCENT('%', 5),
		UP_ARROW('^', 5),
		AMPERSAND('&', 5),
		ASTERISK('*', 5),
		LEFT_PARENTHESIS('(', 4),
		RIGHT_PERENTHESIS(')', 4),
		MINUS('-', 5),
		UNDERSCORE('_', 5),
		PLUS_SIGN('+', 5),
		EQUALS_SIGN('=', 5),
		LEFT_CURL_BRACE('{', 4),
		RIGHT_CURL_BRACE('}', 4),
		LEFT_BRACKET('[', 3),
		RIGHT_BRACKET(']', 3),
		COLON(':', 1),
		SEMI_COLON(';', 1),
		DOUBLE_QUOTE('"', 3),
		SINGLE_QUOTE('\'', 1),
		LEFT_ARROW('<', 4),
		RIGHT_ARROW('>', 4),
		QUESTION_MARK('?', 5),
		SLASH('/', 5),
		BACK_SLASH('\\', 5),
		LINE('|', 1),
		TILDE('~', 5),
		TICK('`', 2),
		PERIOD('.', 1),
		COMMA(',', 1),
		SPACE(' ', 3),
		DEFAULT('a', 4);

		private char character;
		private int length;

		DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}

		public char getCharacter(){
			return this.character;
		}

		public int getLength(){
			return this.length;
		}

		public int getBoldLength(){
			if(this == DefaultFontInfo.SPACE) return this.getLength();
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c){
			for(DefaultFontInfo dFI : DefaultFontInfo.values()){
				if(dFI.getCharacter() == c) return dFI;
			}
			return DefaultFontInfo.DEFAULT;
		}
	}
}
