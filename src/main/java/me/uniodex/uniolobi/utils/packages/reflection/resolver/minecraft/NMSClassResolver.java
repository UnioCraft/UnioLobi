package me.uniodex.uniolobi.utils.packages.reflection.resolver.minecraft;

import me.uniodex.uniolobi.utils.packages.reflection.minecraft.Minecraft;
import me.uniodex.uniolobi.utils.packages.reflection.resolver.ClassResolver;

/**
 * {@link ClassResolver} for <code>net.minecraft.server.*</code> classes
 */
public class NMSClassResolver extends ClassResolver {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class resolve(String... names) throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			if (!names[i].startsWith("net.minecraft.server")) {
				names[i] = "net.minecraft.server." + Minecraft.getVersion() + names[i];
			}
		}
		return super.resolve(names);
	}
}
