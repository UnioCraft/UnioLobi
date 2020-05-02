package me.uniodex.uniolobi.utils.packages.itembuilder.util;

import java.lang.reflect.Field;

import me.uniodex.uniolobi.utils.packages.itembuilder.Reflection;

public abstract class OBCClass {

	private static boolean	initialized;

	public static Class<?>	CraftWorld;
	public static Class<?>	inventory_CraftMetaSkull;

	static {
		if (!initialized) {
			for (Field f : OBCClass.class.getDeclaredFields()) {
				if (f.getType().equals(Class.class)) {
					String name = f.getName().replace("_", ".");
					try {
						f.set(null, Reflection.getOBCClassWithException(name));
					} catch (Exception e) {
					}
				}
			}
		}
	}

}