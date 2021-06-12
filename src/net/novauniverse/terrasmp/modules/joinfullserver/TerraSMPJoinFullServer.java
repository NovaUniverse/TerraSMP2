package net.novauniverse.terrasmp.modules.joinfullserver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPJoinFullServer extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPJoinFullServer";
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (e.getResult().equals(PlayerLoginEvent.Result.KICK_FULL) && e.getPlayer().hasPermission("terrasmp.joinfullserver")) {
			e.allow();
		}
	}
}