package net.novauniverse.terrasmp.modules.terrasmpstuck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.ContinentIndex;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.utils.XZLocation;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPStuck extends NovaModule implements Listener {
	private static TerraSMPStuck instance;
	private Map<UUID, Integer> timeLeft;
	private Map<UUID, XZLocation> locations;
	private Task task;

	private double loss = 0.4;
	private int time = 10;

	public static TerraSMPStuck getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "TerraSMPStuck";
	}

	public double getLoss() {
		return loss;
	}

	@Override
	public void onLoad() {
		TerraSMPStuck.instance = this;
		timeLeft = new HashMap<UUID, Integer>();
		locations = new HashMap<>();
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				List<UUID> toCancel = new ArrayList<UUID>();
				List<UUID> toTp = new ArrayList<UUID>();
				for (UUID uuid : timeLeft.keySet()) {
					int left = timeLeft.get(uuid);

					Player player = Bukkit.getServer().getPlayer(uuid);

					if (player != null) {
						if (player.isOnline()) {
							boolean moved = false;

							XZLocation pl = new XZLocation(player.getLocation());
							XZLocation opl = locations.get(uuid);

							if (opl != null) {
								if (!opl.equals(pl)) {
									moved = true;
								}
							}

							if (left <= 0 && !moved) {
								toTp.add(uuid);
							} else if (moved) {
								toCancel.add(uuid);
							} else {
								timeLeft.put(uuid, left - 1);
							}
						}
					}
				}

				for (UUID uuid : toCancel) {
					timeLeft.remove(uuid);
					Player player = Bukkit.getServer().getPlayer(uuid);
					if (player != null) {
						if (player.isOnline()) {
							player.sendMessage(ChatColor.RED + "Teleport canceled because you moved");
						}
					}
				}

				for (UUID uuid : toTp) {
					timeLeft.remove(uuid);
					Player player = Bukkit.getServer().getPlayer(uuid);
					if (player != null) {
						if (player.isOnline()) {
							player.sendMessage(ChatColor.GOLD + "Finding a location...");
							Continent continent = ContinentIndex.getContinent(PlayerDataManager.getPlayerData(player.getUniqueId()).getStarterContinent());

							if (continent != null) {
								Location location = continent.getRandomSpawnLocation().add(0, 2, 0);

								MPlayer mPlayer = MPlayer.get(player);
								double power = mPlayer.getPower();

								if (power > 0) {
									power = power - (power * loss);

									mPlayer.setPower(power);
								}

								player.sendMessage(ChatColor.GOLD + "Teleporting...");
								player.teleport(location);
							} else {
								player.sendMessage(ChatColor.RED + "Cant teleport you since you have not selected a continent yet");
							}
						}
					}
				}
			}
		}, 20L);
	}

	public void teleportPlayer(Player player) {
		timeLeft.put(player.getUniqueId(), time);
		locations.put(player.getUniqueId(), new XZLocation(player.getLocation()));
		player.sendMessage(ChatColor.GOLD + "Teleporting to a random location in " + time + " seconds. Do not move or the teleportiation will be canceled");
	}

	@Override
	public void onEnable() throws Exception {
		timeLeft.clear();
		locations.clear();
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStartTask(task);
		timeLeft.clear();
		locations.clear();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		timeLeft.remove(e.getPlayer().getUniqueId());
		locations.remove(e.getPlayer().getUniqueId());
	}
}