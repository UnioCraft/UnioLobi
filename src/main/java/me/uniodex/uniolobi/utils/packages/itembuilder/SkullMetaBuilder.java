package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.uniodex.uniolobi.utils.packages.itembuilder.util.HeadTextureChanger;

public class SkullMetaBuilder extends MetaBuilder {

	public SkullMetaBuilder() {
	}

	public SkullMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public SkullMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected SkullMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private SkullMeta getMeta() {
		return (SkullMeta) this.meta;
	}

	@Override
	public SkullMetaBuilder fromConfig(ConfigurationSection section, boolean translateColors) {
		super.fromConfig(section, translateColors);

		if (section.contains("owner")) {
			withOwner(format(section.getString("owner")));
		}
		if (section.contains("texture")) {
			withTexture(section.getString("texture"));
		}

		return this;
	}

	@Override
	public ConfigurationSection toConfig(ConfigurationSection section) {
		section = super.toConfig(section);

		section.set("owner", getMeta().getOwner());
		//TODO: Set texture

		return section;
	}

	/**
	 * Change the owner of the skull
	 *
	 * @param owner Name of the skull owner
	 * @return the SkullMetaBuilder
	 */
	public SkullMetaBuilder withOwner(String owner) {
		validateInit();
		getMeta().setOwner(owner);
		return this;
	}

	/**
	 * Change the displayed texture of the skull
	 *
	 * @param texture {@link java.util.Base64}-Encoded skin texture
	 * @return the SkullMetaBuilder
	 */
	public SkullMetaBuilder withTexture(String texture) {
		validateInit();
		getMeta().setOwner("MHF_ItemBuilder");
		try {
			HeadTextureChanger.applyTextureToMeta(getMeta(), HeadTextureChanger.createProfile(texture));
		} catch (Throwable e) {
			throw new RuntimeException("Failed to apply custom texture", e);
		}
		return this;
	}

	/**
	 * @return The built {@link SkullMeta}
	 */
	@Override
	public SkullMeta build() {
		return (SkullMeta) super.build();
	}

}
