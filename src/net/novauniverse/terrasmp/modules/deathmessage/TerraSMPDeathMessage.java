package net.novauniverse.terrasmp.modules.deathmessage;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;

public class TerraSMPDeathMessage extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPDeathMessage";
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		String newMessage = ChatColor.DARK_GRAY + "[" + ChatColor.RED + ChatColor.BOLD + "*" + ChatColor.RESET + ChatColor.DARK_GRAY + "] " + ChatColor.RED + e.getDeathMessage();
		e.setDeathMessage(newMessage);
	}
}