package me.uniodex.uniolobi.utils.packages.itembuilder;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkMetaBuilder extends MetaBuilder {

	public FireworkMetaBuilder() {
	}

	public FireworkMetaBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public FireworkMetaBuilder(ItemBuilder itemBuilder) {
		super(itemBuilder);
	}

	protected FireworkMetaBuilder(MetaBuilder builder) {
		super();
		this.meta = builder.meta;
		this.itemBuilder = builder.itemBuilder;
	}

	private FireworkMeta getMeta() {
		return (FireworkMeta) this.meta;
	}

	/**
	 * Add effects to the firework
	 *
	 * @param effects Array of {@link FireworkEffect}s to add
	 * @return the FireworkMetaBuilder
	 */
	public FireworkMetaBuilder withEffect(FireworkEffect... effects) {
		validateInit();
		getMeta().addEffects(effects);
		return this;
	}

	/**
	 * Add effects to the firework
	 *
	 * @param effects List of {@link FireworkEffect}s to add
	 * @return the FireworkMetaBuilder
	 */
	public FireworkMetaBuilder withEffects(Iterable<FireworkEffect> effects) {
		validateInit();
		getMeta().addEffects(effects);
		return this;
	}

	/**
	 * Change the power of the Firework
	 *
	 * @param power power
	 * @return the FireworkMetaBuilder
	 */
	public FireworkMetaBuilder withPower(int power) {
		validateInit();
		getMeta().setPower(power);
		return this;
	}

	/**
	 * @return the built {@link FireworkMeta}
	 */
	@Override
	public FireworkMeta build() {
		return (FireworkMeta) super.build();
	}
}
