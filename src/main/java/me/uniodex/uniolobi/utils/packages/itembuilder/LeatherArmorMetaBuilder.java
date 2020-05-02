package me.uniodex.uniolobi.utils.packages.itembuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorMetaBuilder extends MetaBuilder {

	final Pattern RGB_PATTERN = Pattern.compile("R:([0-9]+) G:([0-9]+) B:([0-9]+)");

	public LeatherArmorMetaBuilder() {
	}

	public LeatherArmorMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public LeatherArmorMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected LeatherArmorMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private LeatherArmorMeta getMeta() {
		return (LeatherArmorMeta) this.meta;
	}

	@Override
	public LeatherArmorMetaBuilder fromConfig(ConfigurationSection section, boolean translateColors) {
		super.fromConfig(section, translateColors);

		if (section.contains("color")) {
			String colorString = section.getString("color");

			DyeColor dyeColor = null;
			try {
				dyeColor = DyeColor.valueOf(colorString.toUpperCase());
			} catch (Exception e) {
			}

			Color color = null;

			if (dyeColor != null) {
				color = dyeColor.getColor();
			} else {
				Matcher matcher = RGB_PATTERN.matcher(colorString);
				if (matcher.groupCount() == 3) {
					int r = Integer.parseInt(matcher.group(0));
					int g = Integer.parseInt(matcher.group(1));
					int b = Integer.parseInt(matcher.group(2));

					color = Color.fromRGB(r, g, b);
				}
			}

			if (color != null) {
				withColor(color);
			}
		}

		return this;
	}

	@Override
	public ConfigurationSection toConfig(ConfigurationSection section) {
		section = super.toConfig(section);

		Color color = getMeta().getColor();
		section.set("color", "R:" + color.getRed() + " G:" + color.getGreen() + " B:" + color.getBlue());

		return section;
	}

	/**
	 * Change the color of the Armor
	 *
	 * @param color {@link Color}
	 * @return the LeatherArmorMetaBuilder
	 */
	public LeatherArmorMetaBuilder withColor(Color color) {
		validateInit();
		getMeta().setColor(color);
		return this;
	}

	/**
	 * @return the built {@link LeatherArmorMeta}
	 */
	@Override
	public LeatherArmorMeta build() {
		return (LeatherArmorMeta) super.build();
	}
}
