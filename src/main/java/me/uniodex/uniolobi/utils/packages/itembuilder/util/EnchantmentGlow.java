package me.uniodex.uniolobi.utils.packages.itembuilder.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class EnchantmentGlow extends EnchantmentWrapper {

	public static  int     ID       = 99;
	private static boolean INJECTED = false;
	private static EnchantmentGlow STATIC;

	EnchantmentGlow() {
		super(ID);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	public static EnchantmentGlow inject() {
		if (INJECTED && STATIC != null) { return STATIC; }
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e) {
			System.err.println("Can't inject EnchantmentGlow, failed to set Enchantment field accessible");
			e.printStackTrace();
			return STATIC;
		}
		EnchantmentGlow enchantmentGlow = new EnchantmentGlow();
		registerEnchantment(enchantmentGlow);

		INJECTED = true;
		return STATIC = enchantmentGlow;
	}
}
