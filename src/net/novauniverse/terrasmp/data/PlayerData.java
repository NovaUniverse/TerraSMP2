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

	public boolean hasStarterContinent() {
		return this.getStarterContinent() != null;
	}
	
	public String getStarterContinent() {
		return dataFile.getString("starter-continent");
	}

	public void setStarterContinent(String continent) {
		dataFile.set("starter-continent", continent);
	}

	public void save() throws IOException {
		dataFile.save(PlayerData.getPlayerDataFile(uuid));
	}

	public int getKills() {
		return dataFile.getInt("kills");
	}

	public void setKills(int kills) {
		dataFile.set("kills", kills);
	}

	public int getDeaths() {
		return dataFile.getInt("deaths");
	}

	public void setDeaths(int deaths) {
		dataFile.set("deaths", deaths);
	}
	
	public void incrementKills() {
		this.setKills(this.getKills() + 1);
	}
	
	public void incrementDeaths() {
		this.setDeaths(this.getDeaths() + 1);
	}

	/* ----- Static ----- */
	public static PlayerData loadPlayerData(UUID uuid) {
		File file = getPlayerDataFile(uuid);

		YamlConfiguration data;
		if (file.exists()) {
			data = YamlConfiguration.loadConfiguration(file);
		} else {
			data = new YamlConfiguration();
		}

		if (!data.contains("starter-continent")) {
			data.set("starter-continent", null);
		}

		if (!data.contains("kills")) {
			data.set("kills", 0);
		}

		if (!data.contains("deaths")) {
			data.set("deaths", null);
		}

		return new PlayerData(uuid, data);
	}

	public static File getPlayerDataFile(UUID uuid) {
		return new File(TerraSMP.getInstance().getPlayerDataFolder().getPath() + File.separator + uuid.toString() + ".yml");
	}
}