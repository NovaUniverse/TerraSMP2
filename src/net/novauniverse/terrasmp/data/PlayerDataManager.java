package net.novauniverse.terrasmp.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
	private static HashMap<UUID, PlayerData> loadedPlayerData = new HashMap<UUID, PlayerData>();

	public static PlayerData getPlayerData(UUID uuid) {
		if (loadedPlayerData.containsKey(uuid)) {
			return loadedPlayerData.get(uuid);
		}

		PlayerData playerData = PlayerData.loadPlayerData(uuid);

		loadedPlayerData.put(uuid, playerData);

		savePlayerData(uuid);
		
		return playerData;
	}

	public static boolean savePlayerData(UUID uuid) {
		try {
			getPlayerData(uuid).save();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void unloadPlayerData(UUID uuid) {
		savePlayerData(uuid);
		loadedPlayerData.remove(uuid);
	}

	public static void unloadAll() {
		for (UUID uuid : loadedPlayerData.keySet()) {
			savePlayerData(uuid);
		}

		loadedPlayerData.clear();
	}
}