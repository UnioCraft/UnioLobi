package me.uniodex.uniolobi.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.egg82.antivpn.APIException;
import me.egg82.antivpn.ExternalAPI;
import me.uniodex.uniolobi.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class VotifierListener implements Listener {

    private Main plugin;
    private VPNChecker vpnChecker;

    public VotifierListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (Bukkit.getPluginManager().isPluginEnabled("AntiVPN")) {
            vpnChecker = new VPNChecker();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
        String address = vote.getAddress();
        String username = vote.getUsername();
        Long time = vote.getLocalTimestamp() / 1000;
        String serviceName = vote.getServiceName();

        if (username.length() > 16) {
            return;
        }

        if (vpnChecker != null && vpnChecker.isVPN(address)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd sendmessagetoplayer " + username + " " + "&cVPN ile oy verdiğiniz tespit edildiği için oy iptal edildi!");
            Bukkit.getLogger().severe("VPN olarak algılandığı için " + username + " isimli kişinin oyu iptal edildi.\n" +
                    "IP: " + address + "\n" +
                    "Time: " + time + "\n" +
                    "Service: " + serviceName);
            return;
        }

        Bukkit.getLogger().log(Level.INFO, "Votifier listener succesfully worked:\n"
                + " Adres: " + address
                + ", Username: " + username
                + ", Time: " + time
                + ", serviceName: " + serviceName);


        plugin.getManager().rewardManager.voteReward(username, address, serviceName, time);

        plugin.getManager().rconManager.sendCommand("uci give fly " + username + " 600", "factions");
        plugin.getManager().rconManager.sendCommand("uci give fly " + username + " 600", "skyblock");
        plugin.getManager().rconManager.sendCommand("puan ver " + username + " 5", "factions");
        plugin.getManager().rconManager.sendCommand("puan ver " + username + " 5", "skyblock");
        plugin.getManager().rconManager.sendCommand("crate give virtual oy 1 " + username, "factions");
        plugin.getManager().rconManager.sendCommand("crate give virtual oy 1 " + username, "skyblock");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bcmd votealert &8&o[VOTE] &7&o" + username + " sunucumuza /oy vererek puan, uçuş hakkı ve oy kasası kazandı!");
        plugin.getManager().rconManager.sendCommand("uci sendmessage " + username + " &aOy verdiğiniz için teşekkürler! Ödül olarak &b5 puan, 10 dakika uçuş hakkı &ave &boy kasası &akazandınız! Puanlarınızı /puan ile görebilir, /puanmarketi ile harcayabilirsiniz. /fly ile de uçabilirsiniz. Kasanızı /kasa ile açabilirsiniz.", "skyblock");
        plugin.getManager().rconManager.sendCommand("uci sendmessage " + username + " &aOy verdiğiniz için teşekkürler! Ödül olarak &b5 puan, 10 dakika uçuş hakkı &ave &boy kasası &akazandınız! Puanlarınızı /puan ile görebilir, /puanmarketi ile harcayabilirsiniz. /fly ile de uçabilirsiniz. Kasanızı /kasa ile açabilirsiniz.", "factions");
    }

    public class VPNChecker {

        private ExternalAPI api = ExternalAPI.getInstance();

        public VPNChecker() {
        }

        public boolean isVPN(String ip) {
            try {
                double consensus = api.consensus(ip);

                Bukkit.getLogger().log(Level.INFO, ip + " adresinin consensus'u: " + consensus);
                return consensus >= 0.65;
            } catch (APIException e) {
                e.printStackTrace();
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
