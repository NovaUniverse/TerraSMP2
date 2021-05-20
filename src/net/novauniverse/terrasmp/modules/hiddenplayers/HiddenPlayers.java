package net.novauniverse.terrasmp.modules.hiddenplayers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class HiddenPlayers extends NovaModule implements Listener {
	private static HiddenPlayers instance;

	private List<Player> hiddenPlayers;

	public static HiddenPlayers getInstance() {
		return instance;
	}

	public void hidePlayer(Player player) {
		if (isHidden(player)) {
			return;
		}

		hiddenPlayers.add(player);
		updateVisibility(player);
	}

	public void showPlayer(Player player) {
		if (!isHidden(player)) {
			return;
		}

		hiddenPlayers.remove(player);
		updateVisibility(player);
	}

	public void updateVisibility(Player hPlayer) {
		boolean hidden = isHidden(hPlayer);

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getUniqueId() == hPlayer.getUniqueId()) {
				continue;
			}

			if (hidden) {
				player.hidePlayer(TerraSMP.getInstance(), hPlayer);
			} else {
				player.showPlayer(TerraSMP.getInstance(), hPlayer);
			}
		}
	}

	public boolean isHidden(Player player) {
		return hiddenPlayers.contains(player);
	}

	@Override
	public String getName() {
		return "HiddenPlayers";
	}

	@Override
	public void onLoad() {
		HiddenPlayers.instance = this;
		
		this.hiddenPlayers = new ArrayList<Player>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		for (Player hPlayer : hiddenPlayers) {
			if (hPlayer.getUniqueId() == player.getUniqueId()) {
				continue;
			}

			player.hidePlayer(TerraSMP.getInstance(), hPlayer);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(isHidden(player)) {
			showPlayer(player);
		}
	}
}