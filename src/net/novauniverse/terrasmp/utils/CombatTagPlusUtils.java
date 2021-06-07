package net.novauniverse.terrasmp.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.minelink.ctplus.CombatTagPlus;

public class CombatTagPlusUtils {
	private static CombatTagPlus combatTagInstance = null;

	public static void init() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");

		if (plugin != null) {
			if (plugin instanceof CombatTagPlus) {
				combatTagInstance = (CombatTagPlus) plugin;
			}
		}
	}

	public static boolean isTagged(Player player) {
		return CombatTagPlusUtils.isTagged(player.getUniqueId());
	}
	
	public static boolean isTagged(UUID uuid) {
		return combatTagInstance.getTagManager().isTagged(uuid);
	}

	public static boolean isAvailable() {
		return combatTagInstance != null;
	}
}