package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;
import org.bukkit.Bukkit;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class RconManager {

	private Main plugin;
	private Rcon skyblockRcon;
	private Rcon factionsRcon;

	public RconManager(Main plugin) {
		this.plugin = plugin;
		init();
	}

	private void init() {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				connectRcon();
			}
		});
	}

	private void connectRcon() {
		try {
			factionsRcon = new Rcon("rcon.uniocraft.net", 41330, "password".getBytes());
		} catch (IOException | AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			skyblockRcon = new Rcon("rcon.uniocraft.net", 41331, "".getBytes());
		} catch (IOException | AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendCommand(String command, String server) {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				connectRcon();
				if (server.equalsIgnoreCase("skyblock")) {
					try {
						if (skyblockRcon != null && !skyblockRcon.getSocket().isClosed()) {
							skyblockRcon.command(command);
						}else {
							connectRcon();
							skyblockRcon.command(command);
						}
					} catch (IOException e) {
						connectRcon();
						try {
							skyblockRcon.command(command);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}else if (server.equalsIgnoreCase("factions")) {
					try {
						if (factionsRcon != null && !factionsRcon.getSocket().isClosed()) {

							factionsRcon.command(command);
						}else {
							connectRcon();
							factionsRcon.command(command);
						}
					} catch (IOException e) {
						connectRcon();
						try {
							factionsRcon.command(command);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}

}
