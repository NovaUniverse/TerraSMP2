package net.novauniverse.terrasmp.modules.kdr;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class KDRManager extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPKDRManager";
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		PlayerDataManager.getPlayerData(player.getUniqueId()).incrementDeaths();
		PlayerDataManager.savePlayerData(player.getUniqueId());

		if (player.getKiller() != null) {
			Player killer = player.getKiller();

			PlayerDataManager.getPlayerData(killer.getUniqueId()).incrementKills();
			PlayerDataManager.savePlayerData(killer.getUniqueId());
		}
	}
}