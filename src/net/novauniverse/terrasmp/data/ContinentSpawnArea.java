package net.novauniverse.terrasmp.data;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.zeeraa.novacore.commons.utils.RandomGenerator;

public class ContinentSpawnArea {
	private String name;

	private int x1;
	private int z1;

	private int x2;
	private int z2;

	public ContinentSpawnArea(String name, int x1, int z1, int x2, int z2) {
		this.name = name;

		this.x1 = (x1 < x2 ? x1 : x2);
		this.z1 = (z1 < z2 ? z1 : z2);

		this.x2 = (x1 > x2 ? x1 : x2);
		this.z2 = (z1 > z2 ? z1 : z2);
	}

	public String getName() {
		return name;
	}

	public int getX1() {
		return x1;
	}

	public int getZ1() {
		return z1;
	}

	public int getX2() {
		return x2;
	}

	public int getZ2() {
		return z2;
	}
	
	@Nullable
	public Location tryGetSpawnLocation(World world, boolean ignoreWater) {
		int x = RandomGenerator.generate(x1, x2);
		int z = RandomGenerator.generate(z1, z2);
		
		Block block = world.getHighestBlockAt(x, z);
		
		if(block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
			return null;
		}
		
		if(ignoreWater) {
			return block.getLocation();
		}
		
		if(block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
			return null;
		}
		
		return block.getLocation();
	}
}