package net.novauniverse.terrasmp.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;

public class ContinentReader {
	public static List<Continent> readContinents(File file, World world) throws IOException {

		String fileContent = "";
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		for (String line : lines) {
			fileContent += line;
		}

		JSONObject jsonData = new JSONObject(fileContent);

		return ContinentReader.readContinents(jsonData, world);
	}

	public static List<Continent> readContinents(JSONObject json, World world) {
		List<Continent> result = new ArrayList<Continent>();

		JSONArray continents = json.getJSONArray("continents");

		for (int i = 0; i < continents.length(); i++) {
			JSONObject continent = continents.getJSONObject(i);

			String name = continent.getString("name");
			String displayName = continent.getString("display_name");

			Log.trace("ContinentReader", "Reading continent " + name);

			JSONObject fallbackLocation = continent.getJSONObject("fallback_spawn_location");

			int fallbackSpawnX = fallbackLocation.getInt("x");
			int fallbackSpawnZ = fallbackLocation.getInt("z");

			List<ContinentSpawnArea> spawnAreas = new ArrayList<ContinentSpawnArea>();

			JSONArray spawnAreasJson = continent.getJSONArray("spawn_areas");
			for (int j = 0; j < spawnAreasJson.length(); j++) {
				JSONObject spawnAreaJson = spawnAreasJson.getJSONObject(j);

				String spName = spawnAreaJson.getString("name");

				Log.trace("ContinentReader", "Reading spawn area " + spName + " for continent " + name);

				int x1 = spawnAreaJson.getInt("x1");
				int z1 = spawnAreaJson.getInt("z1");

				int x2 = spawnAreaJson.getInt("x2");
				int z2 = spawnAreaJson.getInt("z2");

				spawnAreas.add(new ContinentSpawnArea(spName, x1, z1, x2, z2));
			}
			
			Log.trace("ContinentReader", "Continent " + name + " has " + spawnAreas.size() + " spawn areas");

			result.add(new Continent(name, displayName, spawnAreas, world, fallbackSpawnX, fallbackSpawnZ));
		}
		return result;
	}
}