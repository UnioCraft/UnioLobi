package me.uniodex.uniolobi.config;

import me.uniodex.uniolobi.Main;

public class MainConfig {
	
	public MainConfig(Main plugin) {
		plugin.saveDefaultConfig();
		plugin.getConfig().options().copyDefaults(true);
	}	
}
