package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class MapMetaBuilder extends MetaBuilder {

	public MapMetaBuilder() {
	}

	public MapMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public MapMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected MapMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private MapMeta getMeta() {
		return (MapMeta) this.meta;
	}

	/**
	 * Change if the map is scaled
	 *
	 * @param scaling
	 * @return the MapMetaBuilder
	 */
	public MapMetaBuilder withScaling(boolean scaling) {
		validateInit();
		getMeta().setScaling(scaling);
		return this;
	}

	/**
	 * Enable scaling for the map
	 *
	 * @return the MapMetaBuilder
	 */
	public MapMetaBuilder withScaling() {
		return withScaling(true);
	}

	/**
	 * @return the build {@link MapMeta}
	 */
	@Override
	public MapMeta build() {
		return (MapMeta) super.build();
	}
}
