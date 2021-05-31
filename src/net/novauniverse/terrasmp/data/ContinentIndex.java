package net.novauniverse.terrasmp.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.novauniverse.terrasmp.modules.hiddenplayers.HiddenPlayers;
import net.zeeraa.novacore.commons.log.Log;

public class ContinentIndex {
	private static List<Continent> continents = new ArrayList<>();

	public static List<Continent> getContinents() {
		return continents;
	}

	@Nullable
	public static Continent getContinent(String name) {
		if (name != null) {
			for (Continent continent : continents) {
				if (continent.getName().equalsIgnoreCase(name)) {
					return continent;
				}
			}
		}

		return null;
	}

	public static void loadContinents(File continentFile, World world) throws IOException {
		continents = ContinentReader.readContinents(continentFile, Bukkit.getServer().getWorlds().get(0));
		Log.info("TerraSMP", continents.size() + " continents loaded");
	}

	public static void setStarterContinent(Player player, Continent continent) {
		PlayerDataManager.getPlayerData(player.getUniqueId()).setStarterContinent(continent.getName());
		PlayerDataManager.savePlayerData(player.getUniqueId());

		HiddenPlayers.getInstance().showPlayer(player);

		Location location = continent.getRandomSpawnLocation();

		if (location != null) {
			player.teleport(location.add(0, 2, 0));
			return;
		}

		player.sendMessage(ChatColor.RED + "Failed to find a spawn location. Please try again");
		Log.warn("TerraSMP", "Failed to get spawn location for player " + player.getName() + " in continent " + continent.getName());
	}
}