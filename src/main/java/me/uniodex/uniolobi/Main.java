package me.uniodex.uniolobi;

import me.uniodex.uniolobi.managers.MainManager;
import me.uniodex.uniolobi.utils.packages.bossbar.BossBarAPI;
import me.uniodex.uniolobi.utils.packages.menubuilder.inventory.InventoryListener;
import net.blackscarx.betterchairs.ChairsPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private MainManager mainManager;
    public static String prefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.GREEN + " ";
    public static String nmsver;
    public static Main instance;

    public InventoryListener inventoryListener;
    BossBarAPI apiInstance;

    public void onEnable() {
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        Bukkit.getPluginManager().registerEvents(inventoryListener = new InventoryListener(this), this);
        mainManager = new MainManager(this);
        instance = this;
        apiInstance = new BossBarAPI(this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (getManager().hidPlayers.contains(pl)) {
                    pl.hidePlayer(p);
                }
            }

            getManager().sqlManager.createPlayer(p.getName(), true);

            getManager().rewardManager.loadRewards(p);

            if (getManager().sqlManager.getSettingStatus(p.getName(), "closeChat")) {
                getManager().closeChat.add(p);
            }

            if (!getManager().sqlManager.getSettingStatus(p.getName(), "closeStacking")) {
                getManager().stackers.add(p);
            }

            if (!getManager().sqlManager.getSettingStatus(p.getName(), "sittingEnabled")) {
                if (getManager().betterChairs != null) {
                    ChairsPlugin.disableList.add(p.getUniqueId());
                }
            }

            if (!getManager().sqlManager.getSettingStatus(p.getName(), "doubleJump")) {
                getManager().closeDoubleJump.add(p);
            }

            if (getManager().sqlManager.getSettingStatus(p.getName(), "hidePlayers")) {
                getManager().hidPlayers.add(p);
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (getManager().hidPlayers.contains(pl)) {
                        p.hidePlayer(pl);
                    }
                }
            }
        }
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
    }

    public MainManager getManager() {
        return mainManager;
    }

}