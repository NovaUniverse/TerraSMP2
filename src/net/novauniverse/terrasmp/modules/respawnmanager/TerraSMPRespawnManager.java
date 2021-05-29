package net.novauniverse.terrasmp.modules.respawnmanager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TerraSMPRespawnManager extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPRespawnManager";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (!e.isBedSpawn()) {
			MPlayer mPlayer = MPlayer.get(player);

			Faction faction = mPlayer.getFaction();

			boolean randomRespawnLocation = false;

			if (faction.getId().equalsIgnoreCase(FactionColl.get().getNone().getId()) || faction.getId().equalsIgnoreCase(FactionColl.get().getSafezone().getId()) || faction.getId().equalsIgnoreCase(FactionColl.get().getWarzone().getId())) {
				randomRespawnLocation = true;
			} else {
				//System.out.println("faction.getHome() : " + faction.getHome());
				if (faction.getHome() == null) {
					randomRespawnLocation = true;
				}
			}

			if (randomRespawnLocation) {
				Continent continent = TerraSMP.getInstance().getContinent(PlayerDataManager.getPlayerData(player.getUniqueId()).getStarterContinent());

				if (continent != null) {
					Location location = continent.getRandomSpawnLocation();
					e.setRespawnLocation(location.add(0, 2, 0));
				}
			}
		}
	}
}