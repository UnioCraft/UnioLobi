package me.uniodex.uniolobi.managers;

import java.util.HashMap;

public class Cooldown {
	
	private HashMap<String, Long> cooldowns = new HashMap<String, Long>();

	public Cooldown(String key, Long cooldown) {
		cooldowns.put(key, cooldown);
	}
	
	public Long getCooldown(String key) {
		return cooldowns.get(key);
	}
	
	public boolean contains(String key) {
		return cooldowns.containsKey(key);
	}
	
	public void removeCooldown(String key) {
		cooldowns.remove(key);
	}
}