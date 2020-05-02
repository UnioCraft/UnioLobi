package me.uniodex.uniolobi.managers;

import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.utils.packages.pool.CredentialPackageFactory;
import me.uniodex.uniolobi.utils.packages.pool.Pool;
import me.uniodex.uniolobi.utils.packages.pool.PoolDriver;
import me.uniodex.uniolobi.utils.packages.pool.properties.PropertyFactory;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLManager {

	private Main plugin;
	private Pool pool;

	public SQLManager(Main plugin, String table, String host, String port, String database, String username, String password){
		this.plugin = plugin;
		pool = new Pool(CredentialPackageFactory.get(username, password), PoolDriver.MYSQL);
		pool.withMin(10).withMax(10).withMysqlUrl(host, database);
		pool.withProperty(PropertyFactory.leakDetectionThreshold(10000));
		pool.withProperty(PropertyFactory.connectionTimeout(15000));
		pool.build();
	}

	public Pool getPool() {
		return pool;
	}

	public void createPlayer(String player, Boolean sync) {
		if (!playerExists(player, "lobi", "uniolobi_playerOptions", "player")) {
			if (sync) {
				updateSQL("INSERT INTO `lobi`.`uniolobi_playerOptions` (`player`) VALUES ('"+player+"');");
			}else {
				updateSQLAsync("INSERT INTO `lobi`.`uniolobi_playerOptions` (`player`) VALUES ('"+player+"');", 1L);
			}
		}

		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			if (sync) {
				updateSQL("INSERT INTO `lobi`.`uniolobi_rewards` (`player`) VALUES ('"+player+"');");
			}else {
				updateSQLAsync("INSERT INTO `lobi`.`uniolobi_rewards` (`player`) VALUES ('"+player+"');", 1L);
			}
		}

		if (!playerExists(player, "lobi", "uniolobi_voteRewards", "player")) {
			if (sync) {
				updateSQL("INSERT INTO `lobi`.`uniolobi_voteRewards` (`player`) VALUES ('"+player+"');");
			}else {
				updateSQLAsync("INSERT INTO `lobi`.`uniolobi_voteRewards` (`player`) VALUES ('"+player+"');", 1L);
			}
		}
	}

	public boolean updateSQL(String QUERY)
	{
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			int count = statement.executeUpdate();
			if(count > 0) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private void updateSQLAsync(String QUERY, Long delay)
	{
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try ( Connection connection = pool.getConnection() ) {
					PreparedStatement statement = connection.prepareStatement(QUERY);
					statement.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};

		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, task, delay);
		/*try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}

	private boolean playerExists(String player, String database, String table, String playerNameTable)
	{
		String QUERY = "SELECT * FROM `"+database+"`.`"+table+"` WHERE `"+playerNameTable+"` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				if (res.getString(playerNameTable) == null) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getPlayerStatSkywars(String player, String statName, String gameMode) {
		if (!playerExists(player, "skywars", "usw_stats_" + gameMode, "player")) {
			return 0;
		}

		String QUERY = "SELECT * FROM `skywars`.`usw_stats_" + gameMode + "` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getInt(statName);
			}else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getPlayerStatSG(String player, String statName, String gameMode) {
		if (!playerExists(player, "sg", gameMode + "sg3", "username")) {
			return 0;
		}

		String QUERY = "SELECT * FROM `sg`.`"+gameMode+"sg3` WHERE `username` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getInt(statName);
			}else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean getSettingStatus(String player, String setting) {
		if (!playerExists(player, "lobi", "uniolobi_playerOptions", "player")) {
			createPlayer(player, true);
			return false;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_playerOptions` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				if (res.getInt(setting) == 1) {
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean toggleSettingStatus(String player, String setting) {
		if (!playerExists(player, "lobi", "uniolobi_playerOptions", "player")) {
			createPlayer(player, true);
			return toggleSettingStatus(player, setting);
		}

		int newSetting;
		if (getSettingStatus(player, setting)) {
			newSetting = 0;
		}else {
			newSetting = 1;
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_playerOptions` SET `"+setting+"` = '"+newSetting+"' WHERE `uniolobi_playerOptions`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}

	public HashMap<Integer, Long> getVotes(String player) {
		HashMap<Integer, Long> votes = new HashMap<Integer, Long>();
		if (!playerExists(player, "lobi", "uniolobi_voteRewards", "player")) {
			createPlayer(player, true);
			return getVotes(player);
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_voteRewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				votes.put(1, res.getLong("lastVotedTime_site1"));
				votes.put(2, res.getLong("lastVotedTime_site2"));
				votes.put(3, res.getLong("lastVotedTime_site3"));
				votes.put(4, res.getLong("lastVotedTime_site4"));
				return votes;
			}else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Long getDailyReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			return 0L;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_rewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getLong("dailyreward");
			}else {
				return 0L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public Long getDailyRewardVIP(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			return 0L;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_rewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getLong("dailyrewardvip");
			}else {
				return 0L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public Long getMonthlyRewardVIP(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			return 0L;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_rewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getLong("monthlyrewardvip");
			}else {
				return 0L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public boolean setVote(String player, Integer voteSiteID, Long votedTime, String IP) {
		if (!playerExists(player, "lobi", "uniolobi_voteRewards", "player")) {
			createPlayer(player, true);
			return setVote(player, voteSiteID, votedTime, IP);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_voteRewards` SET `lastVotedTime_site"+voteSiteID+"` = '"+votedTime+"', `lastVotedIP_site"+voteSiteID+"` = '"+IP+"', `voteCount` = '"+(getPlayerVoteCount(player) + 1)+"' WHERE `uniolobi_voteRewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}

	public boolean setDailyReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			createPlayer(player, true);
			return setDailyReward(player);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_rewards` SET `dailyreward` = '"+System.currentTimeMillis()/1000+"' WHERE `uniolobi_rewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}

	public boolean setDailyRewardVIP(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			createPlayer(player, true);
			return setDailyReward(player);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_rewards` SET `dailyrewardvip` = '"+System.currentTimeMillis()/1000+"' WHERE `uniolobi_rewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}

	public boolean setMonthlyRewardVIP(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			createPlayer(player, true);
			return setDailyReward(player);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_rewards` SET `monthlyrewardvip` = '"+System.currentTimeMillis()/1000+"' WHERE `uniolobi_rewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}
	
	public int getPlayerVoteCount(String player) {
		if (!playerExists(player, "lobi", "uniolobi_voteRewards", "player")) {
			return 0;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_voteRewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getInt("voteCount");
			}else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public Long getGokselKircaReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			return 0L;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_rewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getLong("gokselkirca");
			}else {
				return 0L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public Long getErdogduReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			return 0L;
		}

		String QUERY = "SELECT * FROM `lobi`.`uniolobi_rewards` WHERE `player` = '" + player + "';";
		try ( Connection connection = pool.getConnection() ) {
			PreparedStatement statement = connection.prepareStatement(QUERY);
			ResultSet res = statement.executeQuery();
			if (res.next())
			{
				return res.getLong("erdogdu");
			}else {
				return 0L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public boolean setGokselKircaReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			createPlayer(player, true);
			return setDailyReward(player);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_rewards` SET `gokselkirca` = '"+System.currentTimeMillis()/1000+"' WHERE `uniolobi_rewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}
	
	public boolean setErdogduReward(String player) {
		if (!playerExists(player, "lobi", "uniolobi_rewards", "player")) {
			createPlayer(player, true);
			return setDailyReward(player);
		}

		String QUERY = "UPDATE `lobi`.`uniolobi_rewards` SET `erdogdu` = '"+System.currentTimeMillis()/1000+"' WHERE `uniolobi_rewards`.`player` = '"+player+"';";
		return updateSQL(QUERY);
	}
}
