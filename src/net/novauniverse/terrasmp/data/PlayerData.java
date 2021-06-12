package net.novauniverse.terrasmp.data;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.DateTimeUtils;

public class PlayerData {
	private UUID uuid;
	private YamlConfiguration dataFile;

	public PlayerData(UUID uuid, YamlConfiguration dataFile, boolean save) {
		this.uuid = uuid;
		this.dataFile = dataFile;
		if (save) {
			try {
				this.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean hasStarterContinent() {
		return this.getStarterContinent() != null;
	}

	public String getStarterContinent() {
		String data = dataFile.getString("starter-continent");
		return data.length() == 0 ? null : data;
	}

	public void setStarterContinent(String continent) {
		dataFile.set("starter-continent", continent == null ? "" : continent);
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

	public void setFlag(String flag) {
		dataFile.set("flag", flag == null ? "" : flag);
	}

	public void setRankExpires(LocalDateTime time) {
		dataFile.set("rank_expires", DateTimeUtils.dateToString(time));
	}

	public LocalDateTime getRankExpires() {
		String strVal = dataFile.getString("rank_expires");

		if (strVal.length() > 0) {
			return DateTimeUtils.dateFromString(strVal);
		}

		return null;
	}

	public String getFlag() {
		String data = dataFile.getString("flag");
		return data.length() == 0 ? null : data;
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

		boolean save = false;

		if (!data.contains("starter-continent")) {
			data.set("starter-continent", "");
			save = true;
		}

		if (!data.contains("kills")) {
			data.set("kills", 0);
			save = true;
		}

		if (!data.contains("deaths")) {
			data.set("deaths", 0);
			save = true;
		}

		if (!data.contains("flag")) {
			data.set("flag", "");
			save = true;
		}

		if (!data.contains("rank_expires")) {
			data.set("rank_expires", "");
			save = true;
		}

		Log.trace("PlayerData", "Creating player data object for " + uuid.toString() + ". save: " + save);

		return new PlayerData(uuid, data, save);
	}

	public static File getPlayerDataFile(UUID uuid) {
		return new File(TerraSMP.getInstance().getPlayerDataFolder().getPath() + File.separator + uuid.toString() + ".yml");
	}
}