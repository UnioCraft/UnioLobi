package me.uniodex.uniolobi.utils.packages.itembuilder.util;

import java.util.Base64;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;

import me.uniodex.uniolobi.utils.packages.itembuilder.Reflection;

public class HeadTextureChanger {

	public static String encodeBase64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static String buildResourceLocation(String url) {
		String format = "{textures:{SKIN:{url:\"%s\"}}}";
		return String.format(format, url);
	}

	public static Object createProfile(String data) {
		try {
			Object profile = NMUClass.com_mojang_authlib_GameProfile.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), "CustomBlock");
			Object propertyMap = AccessUtil.setAccessible(NMUClass.com_mojang_authlib_GameProfile.getDeclaredField("properties")).get(profile);
			Object property = NMUClass.com_mojang_authlib_properties_Property.getConstructor(String.class, String.class).newInstance("textures", data);
			NMUClass.com_google_common_collect_ForwardingMultimap.getDeclaredMethod("put", Object.class, Object.class).invoke(propertyMap, "textures", property);

			return profile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object createProfile(String value, String signature) {
		if (signature == null) { return createProfile(value); }
		try {
			Object profile = NMUClass.com_mojang_authlib_GameProfile.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), "CustomBlock");
			Object propertyMap = AccessUtil.setAccessible(NMUClass.com_mojang_authlib_GameProfile.getDeclaredField("properties")).get(profile);
			Object property = NMUClass.com_mojang_authlib_properties_Property.getConstructor(String.class, String.class, String.class).newInstance("textures", value, signature);
			NMUClass.com_google_common_collect_ForwardingMultimap.getDeclaredMethod("put", Object.class, Object.class).invoke(propertyMap, "textures", property);

			return profile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void applyTextureToSkull(Skull skull, Object profile) throws Exception {
		Location loc = skull.getLocation();
		Object world = Reflection.getHandle(loc.getWorld());
		Object tileEntity = NMSClass.WorldServer.getDeclaredMethod("getTileEntity", int.class, int.class, int.class).invoke(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		AccessUtil.setAccessible(NMSClass.TileEntitySkull.getDeclaredField("j")).set(tileEntity, profile);
		NMSClass.World.getDeclaredMethod("notify", int.class, int.class, int.class).invoke(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public static SkullMeta applyTextureToMeta(SkullMeta meta, Object profile) throws Exception {
		if (meta == null) { throw new IllegalArgumentException("meta cannot be null"); }
		if (profile == null) { throw new IllegalArgumentException("profile cannot be null"); }
		Object baseNBTTag = NMSClass.NBTTagCompound.newInstance();
		Object ownerNBTTag = NMSClass.NBTTagCompound.newInstance();

		NMSClass.GameProfileSerializer.getDeclaredMethod("serialize", NMSClass.NBTTagCompound, NMUClass.com_mojang_authlib_GameProfile).invoke(null, ownerNBTTag, profile);

		NMSClass.NBTTagCompound.getDeclaredMethod("set", String.class, NMSClass.NBTBase).invoke(baseNBTTag, "SkullOwner", ownerNBTTag);

		SkullMeta newMeta = (SkullMeta) AccessUtil.setAccessible(OBCClass.inventory_CraftMetaSkull.getDeclaredConstructor(NMSClass.NBTTagCompound)).newInstance(baseNBTTag);

		AccessUtil.setAccessible(OBCClass.inventory_CraftMetaSkull.getDeclaredField("profile")).set(meta, profile);
		AccessUtil.setAccessible(OBCClass.inventory_CraftMetaSkull.getDeclaredField("profile")).set(newMeta, profile);

		return newMeta;
	}

}