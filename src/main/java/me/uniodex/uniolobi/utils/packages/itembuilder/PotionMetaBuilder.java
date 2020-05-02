package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionMetaBuilder extends MetaBuilder {

	public PotionMetaBuilder() {
	}

	public PotionMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public PotionMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected PotionMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private PotionMeta getMeta() {
		return (PotionMeta) this.meta;
	}

	/**
	 * Change the main effect of the potion
	 *
	 * @param mainEffect Main {@link PotionEffectType}
	 * @return the PotionMetaBuilder
	 */
	public PotionMetaBuilder withMainEffect(PotionEffectType mainEffect) {
		validateInit();
		getMeta().setMainEffect(mainEffect);
		return this;
	}

	/**
	 * Add a custom effect
	 *
	 * @param effect    {@link PotionEffect}
	 * @param overwrite overwrite
	 * @return the PotionMetaBuilder
	 */
	public PotionMetaBuilder withCustomEffect(PotionEffect effect, boolean overwrite) {
		validateInit();
		getMeta().addCustomEffect(effect, overwrite);
		return this;
	}

	/**
	 * @return the built {@link PotionMeta}
	 */
	@Override
	public PotionMeta build() {
		return (PotionMeta) super.build();
	}
}
