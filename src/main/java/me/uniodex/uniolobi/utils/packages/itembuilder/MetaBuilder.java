package me.uniodex.uniolobi.utils.packages.itembuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.uniodex.uniolobi.utils.packages.itembuilder.util.EnchantmentGlow;

@SuppressWarnings({"unchecked", "serial"})
public class MetaBuilder extends Formattable {

	 protected ItemBuilder itemBuilder;
	protected           ItemMeta    meta;

	/**
	 * Constructs a new MetaBuilder without content
	 */
	public MetaBuilder() {
	}

	/**
	 * Constructs a new MetaBuilder for an {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 */
	public MetaBuilder(ItemStack itemStack) {
		this.forItem(itemStack);
	}

	/**
	 * Constructs a new MetaBuilder for an {@link ItemBuilder}
	 *
	 * @param itemBuilder {@link ItemBuilder}
	 */
	public MetaBuilder(ItemBuilder itemBuilder) {
		this.forItem(itemBuilder);
	}

	protected void validateInit() {
		if (meta == null) { throw new IllegalStateException("Meta not yet initiated"); }
	}

	/**
	 * Loads the meta from a configuration section
	 * See <link>https://paste.inventivetalent.org/wivacelunu.yml</link> for an example configuration
	 *
	 * @param section Section of the configuration containing the meta
	 * @return the MetaBuilder
	 */
	public MetaBuilder fromConfig(ConfigurationSection section) {
		return fromConfig(section, true);
	}

	/**
	 * Loads the meta from a configuration section
	 * See <link>https://paste.inventivetalent.org/wivacelunu.yml</link> for an example configuration
	 *
	 * @param section         Section of the configuration containing the meta
	 * @param translateColors If alternate color codes (&) should get translated
	 * @return the MetaBuilder
	 */
	public MetaBuilder fromConfig(ConfigurationSection section, boolean translateColors) {
		if (section == null) { throw new IllegalArgumentException("section cannot be null"); }
		if (section.contains("display")) {
			withDisplayName(format(translateColors(section.getString("display"), translateColors)));
		}

		if (section.contains("lore")) {
			if (translateColors || !formatMap.isEmpty()) {
				List<String> translated = new ArrayList<>();
				for (String s : section.getStringList("lore")) {
					translated.add(format(translateColors(s, translateColors)));
				}
				withLore(translated);
			} else { withLore(section.getStringList("lore")); }
		}

		if (section.contains("flags")) {
			List<String> flagStrings = section.getStringList("flags");
			for (String s : flagStrings) {
				ItemFlag flag = null;
				try {
					flag = ItemFlag.valueOf(s.toUpperCase());
				} catch (Exception e) {
				}
				if (flag != null) { withItemFlags(flag); }
			}
		}

		if (section.contains("enchants")) {
			List<String> enchantStrings = section.getStringList("enchants");
			for (String s : enchantStrings) {
				String enchantName = "";
				String enchantOptions = "";

				int level = 1;
				boolean force = false;

				if (s.contains("x")) {
					String[] split = s.split("x");
					enchantName = split[0];
					enchantOptions = split[1];
				}
				if (force = enchantName.contains("-f")) {
					enchantName = enchantName.split("-f")[0];
				}
				if (enchantOptions.contains("-f")) {
					force = true;
					try {
						level = Integer.parseInt(enchantOptions.split("-f")[0]);
					} catch (Exception e) {}
				}

				Enchantment enchantment = null;
				try {
					enchantment = Enchantment.getByName(enchantName.toUpperCase());
				} catch (Exception e) {
				}
				if (enchantment != null) {
					withEnchant(enchantment, level, force);
				}
			}
		}

		return this;
	}

	/**
	 * Writes the meta to a {@link ConfigurationSection}
	 *
	 * @param section {@link ConfigurationSection}
	 * @return return the written {@link ConfigurationSection}
	 */
	public ConfigurationSection toConfig(ConfigurationSection section) {
		if (section == null) { throw new IllegalArgumentException("section cannot be null"); }
		section.set("display", meta.getDisplayName());
		section.set("lore", this.meta.getLore() != null ? new ArrayList<String>(this.meta.getLore()) : new ArrayList<String>());
		section.set("flags", new ArrayList<String>() {
			{
				for (ItemFlag flag : meta.getItemFlags()) {
					add(flag.name());
				}
			}
		});
		section.set("enchants", new ArrayList<String>() {
			{
				for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
					String enchantString = entry.getKey().getName() + "x" + entry.getValue();
					if (entry.getValue() > entry.getKey().getMaxLevel()) {
						enchantString += "-f";
					}

					add(enchantString);
				}
			}
		});

		return section;
	}

	/**
	 * Adds a string replacement for text being loaded by {@link #fromConfig(ConfigurationSection)}
	 *
	 * @param key   string to be replaced
	 * @param value replacement
	 * @return the MetaBuilder
	 */
	@Override
	public MetaBuilder withFormat(String key, String value) {
		super.withFormat(key, value);
		return this;
	}

	//Item constructors

	/**
	 * Change for which {@link ItemStack} the meta is build
	 *
	 * @param itemStack {@link ItemStack}
	 * @return the MetaBuilder
	 */
	public MetaBuilder forItem(ItemStack itemStack) {
		meta = itemStack.getItemMeta();
		return this;
	}

	/**
	 * Change for which {@link ItemBuilder} the meta is build
	 *
	 * @param itemBuilder {@link ItemBuilder}
	 * @return the MetaBuilder
	 */
	public MetaBuilder forItem(ItemBuilder itemBuilder) {
		itemBuilder.validateInit();
		meta = itemBuilder.itemStack.getItemMeta();
		this.itemBuilder = itemBuilder;
		return this;
	}

	//Display name

	/**
	 * Change the display name of the item
	 *
	 * @param displayName new display name
	 * @return the MetaBuilder
	 */
	public MetaBuilder withDisplayName(String displayName) {
		validateInit();
		meta.setDisplayName(displayName);
		return this;
	}

	//Lore

	/**
	 * Change the lore of the item
	 *
	 * @param lore new lore
	 * @return the MetaBuilder
	 */
	public MetaBuilder withLore(List<String> lore) {
		validateInit();
		meta.setLore(lore);
		return this;
	}

	/**
	 * Add lines to the lore
	 *
	 * @param lore Lines to add
	 * @return the MetaBuilder
	 */
	public MetaBuilder withLore(String... lore) {
		validateInit();
		List<String> currentLore = meta.getLore();
		if (currentLore == null) { currentLore = new ArrayList<>(); }
		currentLore.addAll(Arrays.asList(lore));
		return withLore(currentLore);
	}

	//Item flags

	/**
	 * Add {@link ItemFlag}s to the item
	 *
	 * @param flags
	 * @return the MetaBuilder
	 */
	public MetaBuilder withItemFlags(ItemFlag... flags) {
		validateInit();
		meta.addItemFlags(flags);
		return this;
	}

	//Enchantments

	/**
	 * Add an {@link Enchantment} to the item
	 *
	 * @param enchant {@link Enchantment} to add
	 * @param level   level of the enchantment
	 * @param force   force the enchantment
	 * @return the MetaBuilder
	 */
	public MetaBuilder withEnchant(Enchantment enchant, int level, boolean force) {
		validateInit();
		meta.addEnchant(enchant, level, force);
		return this;
	}

	//Unbreakable

	/**
	 * Changes if the item is unbreakable
	 *
	 * @param unbreakable if the item is unbreakable
	 * @return the MetaBuilder
	 */
	public MetaBuilder unbreakable(boolean unbreakable) {
		validateInit();
		meta.spigot().setUnbreakable(unbreakable);
		return this;
	}

	/**
	 * Set the item to be unbreakable
	 *
	 * @return the MetaBuilder
	 */
	public MetaBuilder unbreakable() {
		return unbreakable(true);
	}

	//Enchantment Glow

	/**
	 * Adds a enchantment glow-effect to the item
	 * <p>
	 * (You can change the ID of the enchantment with {@link EnchantmentGlow#ID})
	 *
	 * @return the MetaBuilder
	 */
	public MetaBuilder glow() {
		withEnchant(EnchantmentGlow.inject(), 1, true);
		return this;
	}

	//Conversion

	/**
	 * Convert the MetaBuilder to another {@link MetaBuilder} type
	 *
	 * @param metaClazz Class of the {@link ItemMeta}
	 * @param <T>       {@link MetaBuilder} type
	 * @return converted MetaBuilder
	 * @throws IllegalAccessException if no MetaBuilder for the specified class was found
	 */
	public <T extends MetaBuilder> T convert(Class<? extends ItemMeta> metaClazz) throws IllegalArgumentException {
		if (ItemMeta.class.equals(metaClazz) || CraftMetaItem.equals(metaClazz)) { return (T) this; }
		if (SkullMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new SkullMetaBuilder(this);
		}
		if (BannerMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new BannerMetaBuilder(this);
		}
		if (PotionMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new PotionMetaBuilder(this);
		}
		if (MapMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new MapMetaBuilder(this);
		}
		if (BookMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new BookMetaBuilder(this);
		}
		if (LeatherArmorMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new LeatherArmorMetaBuilder(this);
		}
		if (FireworkMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new FireworkMetaBuilder(this);
		}
		if (FireworkEffectMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new FireworkEffectMetaBuilder(this);
		}
		if (BlockStateMeta.class.isAssignableFrom(metaClazz)) {
			return (T) new BlockStateMetaBuilder(this);
		}
		throw new IllegalArgumentException("No meta builder found for class " + metaClazz.getName());
	}

	/**
	 * Convert the MetaBuilder to another {@link MetaBuilder} type
	 *
	 * @param metaClazz Class of the {@link MetaBuilder}
	 * @param <T>       {@link MetaBuilder} type
	 * @return converted MetaBuilder
	 * @throws IllegalAccessException if no MetaBuilder for the specified class was found
	 */
	public <T extends MetaBuilder> T convertBuilder(Class<T> metaClazz) throws IllegalArgumentException {
		if (MetaBuilder.class.equals(metaClazz)) { return (T) this; }
		try {
			return metaClazz.getDeclaredConstructor(MetaBuilder.class).newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("No meta builder found for class " + metaClazz.getName());
	}

	/**
	 * @return The {@link ItemBuilder} assigned to this MetaBuilder, or null if constructed without one
	 */
	public ItemBuilder item() {
		if (this.itemBuilder != null) {
			this.itemBuilder.withMeta(this);
		}
		return this.itemBuilder;
	}

	//Build

	/**
	 * Build this Item meta
	 *
	 * @return the built {@link ItemMeta}
	 */
	public ItemMeta build() {
		validateInit();
		return this.meta;
	}

	protected String translateColors(String string, boolean translate) {
		return translate ? org.bukkit.ChatColor.translateAlternateColorCodes('&', string) : string;
	}

	private final Class<?> CraftMetaItem = Reflection.getOBCClass("inventory.CraftMetaItem");

}
