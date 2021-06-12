package net.novauniverse.terrasmp.modules.rankmanager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.utils.PermissionsUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class RankManager extends NovaModule implements Listener {
	public static final String RANK_NAME = "vip";

	private Task checkTask;

	private List<UUID> skipCheck;

	@Override
	public String getName() {
		return "TerraSMPRankManager";
	}

	@Override
	public void onLoad() {
		skipCheck = new ArrayList<>();
		checkTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (skipCheck.contains(player.getUniqueId())) {
						continue;
					}

					checkPlayer(player);
				}

				skipCheck.clear();
			}
		}, 200L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(checkTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(checkTask);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		skipCheck.add(e.getPlayer().getUniqueId());
		checkPlayer(e.getPlayer());
	}

	private void checkPlayer(Player player) {
		boolean hasRank = PermissionsUtils.isPlayerInGroup(player, RANK_NAME);
		PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

		LocalDateTime expires = playerData.getRankExpires();

		boolean hasExpired = true;

		if (expires != null) {
			if (expires.isAfter(LocalDateTime.now())) {
				hasExpired = false;
			}
		}

		// Log.trace("hasRank: " + hasRank);

		if (hasExpired && hasRank) {
			PermissionsUtils.removeNode(player, "group." + RANK_NAME);
			Log.info(getName(), "Removing group " + RANK_NAME + " from " + player.getName());
			player.sendMessage(ChatColor.GOLD + "Your VIP rank has expired. Visit the shop to extend it");
		} else if (!hasExpired && !hasRank) {
			PermissionsUtils.addNode(player, "group." + RANK_NAME);
			Log.info(getName(), "Adding group " + RANK_NAME + " to " + player.getName());
			player.sendMessage(ChatColor.GOLD + "You now have the VIP rank");
		}
	}
}