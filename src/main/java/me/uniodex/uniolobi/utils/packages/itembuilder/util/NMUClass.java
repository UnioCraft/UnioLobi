package me.uniodex.uniolobi.utils.packages.itembuilder.util;

import java.lang.reflect.Field;

import me.uniodex.uniolobi.utils.packages.itembuilder.Reflection;

public abstract class NMUClass {

	private static boolean	initialized;

	public static Class<?>	com_mojang_authlib_GameProfile;
	public static Class<?>	com_mojang_authlib_properties_PropertyMap;
	public static Class<?>	com_mojang_authlib_properties_Property;
	public static Class<?>	com_google_common_collect_ForwardingMultimap;

	static {
		if (!initialized) {
			for (Field f : NMUClass.class.getDeclaredFields()) {
				if (f.getType().equals(Class.class)) {
					try {
						String name = f.getName().replace("_", ".");
						if (Reflection.getVersion().contains("1_8")) {
							f.set(null, Class.forName(name));
						} else {
							f.set(null, Class.forName("net.minecraft.util." + name));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}