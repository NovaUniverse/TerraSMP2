package net.novauniverse.terrasmp.modules.joinquitmessage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.terrasmp.utils.PlayerMessages;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TerraSMPJoinQuitMessage extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPJoinQuitMessage";
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		e.setJoinMessage(PlayerMessages.getJoinMessage(player));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		e.setQuitMessage(PlayerMessages.getLeaveMessage(player));
	}
}