package net.novauniverse.terrasmp.modules.requirelabymod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.JSONObject;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.DateTimeParser;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class RequireLabymod extends NovaModule implements Listener, PluginMessageListener {
	private static RequireLabymod instance;

	private DateTimeParser dateParser = new DateTimeParser(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
	private HashMap<UUID, Integer> timeLeft;
	private Task task;

	public static RequireLabymod getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "RequireLabymod";
	}

	@Override
	public void onLoad() {
		timeLeft = new HashMap<>();
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				List<UUID> toRemove = new ArrayList<>();

				for (UUID uuid : timeLeft.keySet()) {
					int ptl = timeLeft.get(uuid);

					if (ptl < 0) {
						toRemove.add(uuid);
					}

					timeLeft.put(uuid, ptl - 1);
				}

				for (UUID uuid : toRemove) {
					timeLeft.remove(uuid);

					Player player = Bukkit.getServer().getPlayer(uuid);
					if (player != null) {
						if (player.isOnline()) {
							player.kickPlayer("You need LabyMod to be able to play on this server");
						}
					}
				}
			}
		}, 20L);
	}

	@Override
	public void onEnable() throws Exception {
		timeLeft.clear();
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		timeLeft.clear();
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		timeLeft.put(e.getPlayer().getUniqueId(), 5);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (timeLeft.containsKey(e.getPlayer().getUniqueId())) {
			timeLeft.remove(e.getPlayer().getUniqueId());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("LMC") || channel.equalsIgnoreCase("labymod3:main")) {
			try {
				String msg = new String(message, "UTF-8");

				int start = -1;
				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i) == '{') {
						start = i;
						break;
					}
				}

				if (start >= 0) {
					String jsonString = msg.substring(start);

					JSONObject json = new JSONObject(jsonString);

					if (json.has("mods")) {

						File dataFolder = new File(TerraSMP.getInstance().getDataFolder().getPath() + File.separator + "player_data");
						
						if(!dataFolder.exists()) {
							dataFolder.mkdir();
						}
						
						File playerDataFolder = new File(dataFolder.getPath() + File.separator + player.getUniqueId().toString());
						
						if(!playerDataFolder.exists()) {
							playerDataFolder.mkdir();
						}
						
						File dataFile = new File(playerDataFolder.getPath() + File.separator + dateParser.dateToString(LocalDateTime.now()) + ".log");
						
						String data = "";
						
						data += "Player: " + player.getName() + "\n";
						data += "UUID: " + player.getUniqueId() + "\n";
						data += "JSON: " + json.toString(4) + "\n";
						
						FileUtils.write(dataFile, data, StandardCharsets.UTF_8);
						
						if (timeLeft.containsKey(player.getUniqueId())) {
							timeLeft.remove(player.getUniqueId());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}