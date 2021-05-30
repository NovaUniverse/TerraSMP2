package net.novauniverse.terrasmp.modules.playerdatagarbagecollector;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class PlayerDataGarbageCollector extends NovaModule implements Listener {
@Override
public String getName() {
	return "PlayerDataGarbageCollector";
}

@EventHandler(priority = EventPriority.MONITOR)
public void onPlayerQuit(PlayerQuitEvent e) {
	Player player = e.getPlayer();

	PlayerDataManager.unloadPlayerData(player.getUniqueId());
}
}