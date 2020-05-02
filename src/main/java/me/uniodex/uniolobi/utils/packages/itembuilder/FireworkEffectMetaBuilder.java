package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class FireworkEffectMetaBuilder extends MetaBuilder {

	public FireworkEffectMetaBuilder() {
	}

	public FireworkEffectMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public FireworkEffectMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected FireworkEffectMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private FireworkEffectMeta getMeta() {
		return (FireworkEffectMeta) this.meta;
	}

	/**
	 * Change the effect
	 *
	 * @param effect {@link FireworkEffect}
	 * @return the FireworkEffectMetaBuilder
	 */
	public FireworkEffectMetaBuilder withEffect(FireworkEffect effect) {
		validateInit();
		getMeta().setEffect(effect);
		return this;
	}

	/**
	 * @return the built {@link FireworkEffectMeta}
	 */
	@Override
	public FireworkEffectMeta build() {
		return (FireworkEffectMeta) super.build();
	}
}
