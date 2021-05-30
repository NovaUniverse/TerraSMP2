package net.novauniverse.terrasmp.modules.intialjoinmanager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.ContinentIndex;
import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.modules.hiddenplayers.HiddenPlayers;
import net.novauniverse.terrasmp.modules.labymod.TerraSMPLabymodIntegration;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class InitialJoinManager extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "InitialJoinManager";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

		if (ContinentIndex.getContinent(playerData.getStarterContinent()) == null) {
			AsyncManager.runSync(new Runnable() {
				@Override
				public void run() {
					HiddenPlayers.getInstance().hidePlayer(player);
					player.sendMessage(ChatColor.GOLD + "Please select your starter continent");
					player.sendMessage(ChatColor.GOLD + "If you are using labymod the continent selector screen should open within 5 seconds");
					player.teleport(TerraSMP.getInstance().getSpawnLocation());

					new BukkitRunnable() {
						@Override
						public void run() {
							if (!PlayerDataManager.getPlayerData(player.getUniqueId()).hasStarterContinent()) {
								TerraSMPLabymodIntegration.getInstance().openContinentSelectorScreen(e.getPlayer());
							}
						}
					}.runTaskLater(TerraSMP.getInstance(), 100L);
				}
			}, 4L);
		}
	}
}