package net.novauniverse.terrasmp.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

import me.missionary.board.provider.BoardProvider;
import net.novauniverse.terrasmp.modules.terrasmptime.TerraSMPTime;
import net.novauniverse.terrasmp.utils.TerraSMPUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPBoardProvider extends NovaModule implements BoardProvider, Listener {
	private static TerraSMPBoardProvider instance;
	private HashMap<UUID, BoardData> boardDataMap;
	private Task task;
	private String tspString;

	private List<ScoreboardModifier> modifiers;

	public static TerraSMPBoardProvider getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "TerraSMPBoardProvider";
	}

	@Override
	public void onLoad() {
		TerraSMPBoardProvider.instance = this;
		modifiers = new ArrayList<>();
		boardDataMap = new HashMap<>();
		tspString = ChatColor.DARK_GRAY + "Unknown";
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				double tps = -1;
				try {
					tps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
					tspString = ChatColor.GOLD + "TPS: " + TextUtils.formatTps(tps);
				} catch (Exception e) {
					Log.trace("TabList", "Failed to fetch server ping " + e.getClass().getName() + " " + e.getMessage());
				}

				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					updatePlayer(player);
				}
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@Override
	public List<String> getLines(Player player) {
		// Log.trace("getLines(" + player + ")");

		List<String> lines = new ArrayList<>();

		BoardData data = boardDataMap.get(player.getUniqueId());

		if (data != null) {
			lines.add(ChatColor.GOLD + "Date: " + ChatColor.AQUA + TerraSMPTime.getInstance().getScoreboardDate());
			lines.add(data.isCombatTagged() ? ChatColor.RED + TextUtils.ICON_WARNING + " Combat tagged " + TextUtils.ICON_WARNING : "");
			lines.add(data.getAtLocation());
			lines.add(ChatColor.GOLD + "Facing: " + ChatColor.AQUA + TerraSMPUtils.getCardinalDirection(player));
			lines.add("");
			lines.add(data.getInFaction());
			lines.add(data.getPower());
			lines.add(data.getFactionPower());
			lines.add("");
			lines.add(tspString);
			lines.add(data.getPing());
		}

		lines.add(ChatColor.YELLOW + "novauniverse.net");

		for (ScoreboardModifier modifier : modifiers) {
			modifier.process(lines, player);
		}

		return lines;
	}

	@Override
	public String getTitle(Player player) {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "TerraSMP";
	}

	public List<ScoreboardModifier> getModifiers() {
		return modifiers;
	}

	public void addModifier(ScoreboardModifier modifier) {
		modifiers.add(modifier);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		updatePlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerQuitEvent e) {
		boardDataMap.remove(e.getPlayer().getUniqueId());
	}

	public void updatePlayer(Player player) {
		BoardData boardData;
		if (boardDataMap.containsKey(player.getUniqueId())) {
			boardData = boardDataMap.get(player.getUniqueId());
		} else {
			boardData = new BoardData();

			boardDataMap.put(player.getUniqueId(), boardData);
		}

		MPlayer mPlayer = MPlayer.get(player);

		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		Faction playerFaction = mPlayer.getFaction();

		if (faction == null) {
			boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.YELLOW + "Wilderness");
		} else {
			if (faction.getId().equalsIgnoreCase(FactionColl.get().getWarzone().getId())) {
				boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.RED + "Warzone");
			} else if (faction.getId().equalsIgnoreCase(FactionColl.get().getSafezone().getId())) {
				boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.GREEN + "Safezone");
			} else if (faction.getId().equalsIgnoreCase(FactionColl.get().getNone().getId())) {
				boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.YELLOW + "Wilderness");
			} else {
				if (playerFaction != null) {
					if (playerFaction.getId() != FactionColl.get().getNone().getId()) {
						if (faction.getId() == playerFaction.getId()) {
							boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.GREEN + faction.getName());
						} else {
							Rel rel = faction.getRelationTo(playerFaction);

							ChatColor color = ChatColor.AQUA;

							if (rel == Rel.ALLY) {
								color = ChatColor.AQUA;
							} else if (rel == Rel.ENEMY) {
								color = ChatColor.RED;
							} else if (rel == Rel.NEUTRAL) {
								color = ChatColor.YELLOW;
							}

							boardData.setAtLocation(ChatColor.GOLD + "At: " + color + faction.getName());
						}
					} else {
						boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.YELLOW + faction.getName());
					}
				} else {
					boardData.setAtLocation(ChatColor.GOLD + "At: " + ChatColor.YELLOW + faction.getName());
				}
			}
		}

		String playerPower = ChatColor.GOLD + "Power: " + ChatColor.AQUA + ((int) mPlayer.getPower()) + "/" + ((int) mPlayer.getPowerMax());
		String factionPower = ChatColor.GOLD + "Faction pwr: " + ChatColor.AQUA;

		boolean hasFaction = false;

		if (playerFaction != null) {
			if (playerFaction.getId() != FactionColl.get().getNone().getId()) {
				hasFaction = true;
			}
		}

		String in = ChatColor.GOLD + "Faction: ";

		if (hasFaction) {
			factionPower += ((int) playerFaction.getPower()) + "/" + ((int) playerFaction.getPowerMax());
			in += ChatColor.AQUA + playerFaction.getName();
		} else {
			factionPower += "---/---";
			in += ChatColor.RED + "No faction";
		}

		boardData.setInFaction(in);
		boardData.setFactionPower(factionPower);
		boardData.setPower(playerPower);

		int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);
		boardData.setPing(ChatColor.GOLD + "Ping: " + TextUtils.formatPing(ping));
	}
}