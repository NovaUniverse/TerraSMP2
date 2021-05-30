package net.novauniverse.terrasmp.modules.systemmessage;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TerraSMPSystemMessage extends NovaModule implements Listener {
	private static TerraSMPSystemMessage instance;

	private File systemMessageFile;

	private String systemMessage;
	private BossBar systemMessageBar;

	@Override
	public String getName() {
		return "TerraSMPSystemMessage";
	}

	@Override
	public void onLoad() {
		TerraSMPSystemMessage.instance = this;
		systemMessageFile = new File(TerraSMP.getInstance().getDataFolder().getPath() + File.separator + "system_message.json");

		systemMessage = null;
		systemMessageBar = null;

		if (!systemMessageFile.exists()) {
			try {
				saveSystemMessageToFile("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEnable() throws Exception {
		try {
			JSONObject systemMessageJson = JSONFileUtils.readJSONObjectFromFile(systemMessageFile);
			String savedSystemMessage = systemMessageJson.getString("message");

			if (savedSystemMessage.length() > 0) {
				setSystemMessage(savedSystemMessage, false);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() throws Exception {
		if (systemMessageBar != null) {
			systemMessageBar.removeAll();
			systemMessageBar = null;
		}
		this.systemMessage = null;
	}

	public static TerraSMPSystemMessage getInstance() {
		return instance;
	}

	public void setSystemMessage(String systemMessage) {
		this.setSystemMessage(systemMessage, true);
	}

	public void removeSystemMessage() {
		setSystemMessage(null);
	}

	public void setSystemMessage(String systemMessage, boolean save) {
		this.systemMessage = systemMessage;

		if (systemMessage == null) {
			if (systemMessageBar != null) {
				systemMessageBar.removeAll();
				systemMessageBar = null;
			}
		} else {
			if (systemMessageBar == null) {
				systemMessageBar = Bukkit.getServer().createBossBar("System Message", BarColor.BLUE, BarStyle.SOLID);
			}

			systemMessageBar.setVisible(true);
			systemMessageBar.setTitle(ChatColor.RED + "" + ChatColor.BOLD + "System Message: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', systemMessage));

			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (!systemMessageBar.getPlayers().contains(player)) {
					systemMessageBar.addPlayer(player);
				}
			}
		}

		if (save) {
			try {
				saveSystemMessageToFile(systemMessage == null ? "" : systemMessage);
			} catch (IOException e) {
				e.printStackTrace();
				Log.error("TerraSMP", "Failed to save system message to file. " + e.getClass().getName() + " " + e.getMessage());
			}
		}
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	private void saveSystemMessageToFile(String message) throws IOException {
		JSONObject massageJson = new JSONObject();

		massageJson.put("message", message);

		JSONFileUtils.saveJson(systemMessageFile, massageJson);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (systemMessageBar != null) {
			if (!systemMessageBar.getPlayers().contains(player)) {
				systemMessageBar.addPlayer(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		if (systemMessageBar != null) {
			if (systemMessageBar.getPlayers().contains(player)) {
				systemMessageBar.removePlayer(player);
			}
		}
	}
}