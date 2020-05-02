package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"serial"})
public class BannerMetaBuilder extends MetaBuilder {

	public BannerMetaBuilder() {
		super();
	}

	public BannerMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public BannerMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected BannerMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private BannerMeta getMeta() {
		return (BannerMeta) this.meta;
	}

	@Override
	public BannerMetaBuilder fromConfig(ConfigurationSection section, boolean translateColors) {
		super.fromConfig(section, translateColors);

		if (section.contains("patterns")) {
			List<String> patternStrings = section.getStringList("patterns");

			for (String s : patternStrings) {
				String[] split = s.split("-");
				if (split.length != 2) { continue; }
				String patternName = split[0];
				String colorName = split[1];

				PatternType patternType = null;
				try {
					patternType = PatternType.valueOf(patternName.toUpperCase());
				} catch (Exception e) {
				}

				DyeColor color = null;
				try {
					color = DyeColor.valueOf(colorName);
				} catch (Exception e) {
				}

				if (patternType != null && color != null) {
					Pattern pattern = new Pattern(color, patternType);

					withPattern(pattern);
				}
			}
		}

		return this;
	}

	@Override
	public ConfigurationSection toConfig(ConfigurationSection section) {
		section = super.toConfig(section);

		section.set("patterns", new ArrayList<String>() {
			{
				for (Pattern pattern : getMeta().getPatterns()) {
					add(pattern.getPattern().name() + "-" + pattern.getColor().name());
				}
			}
		});

		return section;
	}

	/**
	 * Change the base color of the banner
	 *
	 * @param baseColor base {@link DyeColor}
	 * @return the BannerMetaBuilder
	 */
	public BannerMetaBuilder withBaseColor(DyeColor baseColor) {
		validateInit();
		getMeta().setBaseColor(baseColor);
		return this;
	}

	/**
	 * Add a pattern to the banner
	 *
	 * @param patterns Array of {@link Pattern}s to add
	 * @return the BannerMetaBuilder
	 */
	public BannerMetaBuilder withPattern(Pattern... patterns) {
		validateInit();
		for (Pattern pattern : patterns) {
			getMeta().addPattern(pattern);
		}
		return this;
	}

	/**
	 * @return the built {@link BannerMeta}
	 */
	@Override
	public BannerMeta build() {
		return (BannerMeta) super.build();
	}
}
