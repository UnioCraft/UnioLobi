package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class BlockStateMetaBuilder extends MetaBuilder {

	public BlockStateMetaBuilder() {
	}

	public BlockStateMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public BlockStateMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected BlockStateMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}
	/**
	 * @return the built {@link FireworkEffectMeta}
	 */
	@Override
	public BlockStateMeta build() {
		return (BlockStateMeta) super.build();
	}
}
