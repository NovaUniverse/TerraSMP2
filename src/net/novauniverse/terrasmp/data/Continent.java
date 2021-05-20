package net.novauniverse.terrasmp.data;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.zeeraa.novacore.commons.log.Log;

public class Continent {
	private String name;
	private String displayName;

	private List<ContinentSpawnArea> spawnAreas;

	private World world;

	private int fallbackSpawnX;
	private int fallbackSpawnZ;

	public Continent(String name, String displayName, List<ContinentSpawnArea> spawnAreas, World world, int fallbackSpawnX, int fallbackSpawnZ) {
		this.name = name;
		this.displayName = displayName;

		this.spawnAreas = spawnAreas;

		this.world = world;

		this.fallbackSpawnX = fallbackSpawnX;
		this.fallbackSpawnZ = fallbackSpawnZ;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<ContinentSpawnArea> getSpawnAreas() {
		return spawnAreas;
	}

	public World getWorld() {
		return world;
	}

	public int getFallbackSpawnX() {
		return fallbackSpawnX;
	}

	public int getFallbackSpawnZ() {
		return fallbackSpawnZ;
	}

	@Nullable
	public Location getRandomSpawnLocation() {
		if (spawnAreas.size() > 0) {
			for (int i = 0; i < 3; i++) {
				Location location = spawnAreas.get(new Random().nextInt(spawnAreas.size())).tryGetSpawnLocation(world, (i < 2));

				if (location != null) {
					Log.trace("Continent#getRandomSpawnLocation()", "Found a spawn location at X: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ());
					return location.add(0, 1, 0);
				}
			}
		}

		Log.trace("Continent#getRandomSpawnLocation()", "Failed to get a spawn location from spawn areas. Using default location instead");

		Block block = world.getHighestBlockAt(fallbackSpawnX, fallbackSpawnZ);

		if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
			return null;
		}

		return block.getLocation();
	}
}