package net.novauniverse.terrasmp.modules.terrasmptime;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.DateTimeParser;
import net.zeeraa.novacore.commons.utils.DateTimeUtils;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPTime extends NovaModule {
	private static TerraSMPTime instance;
	private ZoneId zoneId;
	private File startTimeFile;
	private File timeConfigFile;
	private LocalDateTime terraSMPCurrentDate;
	private LocalDateTime terraSMPStartDate;
	private LocalDateTime startTime;
	private Task updateTask;
	private double multiplier;

	private DateTimeParser scoreboardDateTimeParser;
	private String scoreboardDate;

	@Override
	public String getName() {
		return "TerraSMPTime";
	}

	@Override
	public void onLoad() {
		zoneId = ZoneId.systemDefault();
		startTimeFile = new File(TerraSMP.getInstance().getDataFolder().toString() + File.separator + "start_time.json");
		timeConfigFile = new File(TerraSMP.getInstance().getDataFolder().toString() + File.separator + "time_config.json");
		TerraSMPTime.instance = this;
		terraSMPCurrentDate = LocalDateTime.now();
		terraSMPStartDate = LocalDateTime.now();
		startTime = LocalDateTime.now();
		multiplier = 1.0;
		scoreboardDateTimeParser = new DateTimeParser(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		scoreboardDate = "1337-69-69";
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				long start = startTime.atZone(zoneId).toEpochSecond();
				long now = LocalDateTime.now().atZone(zoneId).toEpochSecond();

				long dif = now - start;
				long multipliedDif = (long) (dif * multiplier);

				terraSMPCurrentDate = terraSMPStartDate.plusSeconds(multipliedDif);

				// long terraSmpStart = terraSMPStartDate.atZone(zoneId).toEpochSecond();
				// long terraSmpNow = terraSmpStart + multipliedDif;

				// long millis = terraSmpNow * 1000;

				// Date date = new Date(millis);
				// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// terraSMPCurrentDate =
				// DateTimeUtils.getDefaultDateTimeParser().dateFromString(format.format(date));

				scoreboardDate = scoreboardDateTimeParser.dateToString(terraSMPCurrentDate);

				//Log.trace(getName(), "Now at " + DateTimeUtils.getDefaultDateTimeParser().dateToString(terraSMPCurrentDate));
			}
		}, 20L);
	}

	@Override
	public void onEnable() throws Exception {
		if (startTimeFile.exists()) {
			JSONObject json = JSONFileUtils.readJSONObjectFromFile(startTimeFile);
			String startTimeString = json.getString("start_time");
			startTime = DateTimeUtils.getDefaultDateTimeParser().dateFromString(startTimeString);
			Log.info(getName(), "Loaded date " + DateTimeUtils.getDefaultDateTimeParser().dateToString(startTime) + " from date file");
		} else {
			JSONObject json = new JSONObject();
			json.put("start_time", DateTimeUtils.getDefaultDateTimeParser().dateToString(LocalDateTime.now()));
			JSONFileUtils.saveJson(startTimeFile, json);
			startTime = LocalDateTime.now();
			Log.info(getName(), "Starting at " + DateTimeUtils.getDefaultDateTimeParser().dateToString(startTime) + " since there was no date file");
		}

		if (!timeConfigFile.exists()) {
			JSONObject defaultValue = new JSONObject();
			defaultValue.put("time_multiplier", 365.242199);
			defaultValue.put("terrasmp_start_date", "1800-01-01 00:00:00");
			JSONFileUtils.saveJson(timeConfigFile, defaultValue);
		}

		JSONObject config = JSONFileUtils.readJSONObjectFromFile(timeConfigFile);
		multiplier = config.getDouble("time_multiplier");
		terraSMPStartDate = DateTimeUtils.dateFromString(config.getString("terrasmp_start_date"));

		Log.info(getName(), "Using a multiplier of " + multiplier);
		Log.info(getName(), "Start date " + DateTimeUtils.dateToString(terraSMPStartDate));

		Task.tryStartTask(updateTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}

	public static TerraSMPTime getInstance() {
		return instance;
	}

	public LocalDateTime getTerraSMPCurrentDate() {
		return terraSMPCurrentDate;
	}

	public String getScoreboardDate() {
		return scoreboardDate;
	}
}