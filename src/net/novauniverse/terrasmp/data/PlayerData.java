package net.novauniverse.terrasmp.data;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import net.novauniverse.terrasmp.TerraSMP;
public class PlayerData {
	private UUID uuid;
	private YamlConfiguration dataFile;

	public PlayerData(UUID uuid, YamlConfiguration dataFile) {
		this.uuid = uuid;
		this.dataFile = dataFile;
	}

	public String getStarterContinent() {
		return dataFile.getString("starter-continent");
	}

	public void setStarterContinent(String continent) {
		dataFile.set("starter-continent", continent);
	}

	public void save() throws IOException {
		dataFile.save(getPlayerDataFile(uuid));
	}

	public static PlayerData loadPlayerData(UUID uuid) {
		File file = getPlayerDataFile(uuid);

		if (file.exists()) {
			return new PlayerData(uuid, YamlConfiguration.loadConfiguration(file));
		}

		YamlConfiguration dataFile = new YamlConfiguration();

		dataFile.set("starter-continent", null);

		return new PlayerData(uuid, dataFile);
	}

	public static File getPlayerDataFile(UUID uuid) {
		return new File(TerraSMP.getInstance().getPlayerDataFolder().getPath() + File.separator + uuid.toString() + ".yml");
	}
}