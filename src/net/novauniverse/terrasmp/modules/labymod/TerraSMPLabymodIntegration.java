package net.novauniverse.terrasmp.modules.labymod;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;

import net.labymod.serverapi.common.widgets.WidgetScreen;
import net.labymod.serverapi.common.widgets.components.widgets.ButtonWidget;
import net.labymod.serverapi.common.widgets.util.Anchor;
import net.labymod.serverapi.common.widgets.util.EnumScreenAction;
import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.utils.LabyModProtocol;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TerraSMPLabymodIntegration extends NovaModule implements Listener {
	private Task task;

	private static TerraSMPLabymodIntegration instance;

	private static final String PLAYER_POWER_ICON = "https://novauniverse.net/cdn/terrasmp/placeholder_1.jpg";
	private static final String FACTION_POWER_ICON = "https://novauniverse.net/cdn/terrasmp/placeholder_1.jpg";

	public static TerraSMPLabymodIntegration getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "TerraSMPLabymodIntegration";
	}

	@Override
	public void onLoad() {
		TerraSMPLabymodIntegration.instance = this;

		this.task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					sendPlayerTitles(player);
					updatePowerDisplay(player);
				}
			}
		}, 100L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.sendTabImage(e.getPlayer());
		this.sendPlayerTitles(e.getPlayer());
		this.sendCurrentPlayingGamemode(e.getPlayer(), true, "TerraSMP");
		this.setMiddleClickActions(e.getPlayer());
		this.updatePowerDisplay(e.getPlayer());
		// this.sendWatermark(e.getPlayer(), true); // Breaks the tablist
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.sendCurrentPlayingGamemode(e.getPlayer(), false, "TerraSMP");
	}

	private void sendTabImage(Player player) {
		JsonObject object = new JsonObject();
		object.addProperty("url", "https://novauniverse.net/cdn/terrasmp/labymod_ingame_banner.png");
		LabyModProtocol.sendLabyModMessage(player, "server_banner", object);
	}

	private void sendPlayerTitles(Player target) {
		MPlayer mTargetPlayer = MPlayer.get(target);
		Faction targetPlayerFaction = mTargetPlayer.getFaction();

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			MPlayer mPlayer = MPlayer.get(player);
			Faction playerFaction = mPlayer.getFaction();

			String faction = ChatColor.RED + "One lonely boi";

			if (playerFaction == null) {
				faction = ChatColor.YELLOW + "Wilderness";
			} else {
				ChatColor relation = ChatColor.YELLOW;

				if (targetPlayerFaction.getId().equalsIgnoreCase(playerFaction.getId())) {
					relation = ChatColor.GREEN;
				} else {
					if (targetPlayerFaction != null) {
						Rel rel = targetPlayerFaction.getRelationTo(playerFaction);

						if (rel == Rel.ALLY) {
							relation = ChatColor.AQUA;
						} else if (rel == Rel.ENEMY) {
							relation = ChatColor.RED;
						} else if (rel == Rel.NEUTRAL) {
							relation = ChatColor.YELLOW;
						}
					}
				}

				faction = relation + playerFaction.getName();
			}

			setSubtitle(target, player.getUniqueId(), faction);
		}
	}

	public void updatePowerDisplay(Player player) {
		MPlayer mPlayer = MPlayer.get(player);
		Faction faction = mPlayer.getFaction();

		int playerPower = 0;
		int factionPower = 0;

		if (faction.getId() != FactionColl.get().getNone().getId()) {
			playerPower = (int) Math.round(mPlayer.getPower());
			factionPower = (int) Math.round(faction.getPower());
		}

		updateBalanceDisplay(player, EnumBalanceType.CASH, true, playerPower, PLAYER_POWER_ICON);
		updateBalanceDisplay(player, EnumBalanceType.BANK, true, factionPower, FACTION_POWER_ICON);
	}

	public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance, String icon) {
		JsonObject economyObject = new JsonObject();
		JsonObject cashObject = new JsonObject();

		// Visibility
		cashObject.addProperty("visible", visible);

		// Amount
		cashObject.addProperty("balance", balance);

		cashObject.addProperty("icon", icon);
		/*
		 * // Icon (Optional)
		 * 
		 * // Decimal number (Optional) JsonObject decimalObject = new JsonObject();
		 * decimalObject.addProperty("format", "##.##"); // Decimal format
		 * decimalObject.addProperty("divisor", 100); // The value that divides the
		 * balance cashObject.add( "decimal", decimalObject );
		 */

		// The display type can be "cash" or "bank".
		economyObject.add(type.getKey(), cashObject);

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(player, "economy", economyObject);
	}

	public void openContinentSelectorScreen(Player player) {
		WidgetScreen screen = new WidgetScreen(1);

		Anchor anchor = new Anchor(50, 50);

		for (int i = 0; i < TerraSMP.getInstance().getContinents().size(); i++) {
			ButtonWidget button = new ButtonWidget(i + 1, anchor, -50, 20 * (i + 1), TerraSMP.getInstance().getContinents().get(i).getDisplayName(), 100, 20);
			button.setCloseScreenOnClick(true);
			button.setValue(TerraSMP.getInstance().getContinents().get(i).getName());
			button.setCloseScreenOnClick(true);
		}

		JsonObject object = screen.toJsonObject(EnumScreenAction.OPEN);

		LabyModProtocol.sendLabyModMessage(player, "screen", object);
	}

	public void setMiddleClickActions(Player player) {
		// List of all action menu entries
		JsonArray array = new JsonArray();

		// Add entries
		JsonObject entry = new JsonObject();
		entry.addProperty("displayName", "Invite to faction");
		entry.addProperty("type", EnumActionType.RUN_COMMAND.name());
		entry.addProperty("value", "invitetofaction {name}");
		array.add(entry);

		if (player.hasPermission("terrasmp.moderator")) {
			JsonObject entry2 = new JsonObject();
			entry2.addProperty("displayName", "Create anticheat report");
			entry2.addProperty("type", EnumActionType.RUN_COMMAND.name());
			entry2.addProperty("value", "goose report {name}");
			array.add(entry2);
		}

		entry = new JsonObject();
		entry.addProperty("displayName", "Open dynmap");
		entry.addProperty("type", EnumActionType.OPEN_BROWSER.name());
		entry.addProperty("value", "https://terrasmp-map.novauniverse.net/");
		array.add(entry);

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(player, "user_menu_actions", array);
	}

	public void setSubtitle(Player receiver, UUID subtitlePlayer, String value) {
		// List of all subtitles
		JsonArray array = new JsonArray();

		// Add subtitle
		JsonObject subtitle = new JsonObject();
		subtitle.addProperty("uuid", subtitlePlayer.toString());

		// Optional: Size of the subtitle
		subtitle.addProperty("size", 1.6d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)

		// no value = remove the subtitle
		if (value != null) {
			subtitle.addProperty("value", value);
		}

		// If you want to use the new text format in 1.16+
		// subtitle.add("raw_json_text", textObject );

		// You can set multiple subtitles in one packet
		array.add(subtitle);

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(receiver, "account_subtitle", array);
	}

	public void sendCurrentPlayingGamemode(Player player, boolean visible, String gamemodeName) {
		JsonObject object = new JsonObject();
		object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
		object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(player, "server_gamemode", object);
	}

	public void sendWatermark(Player player, boolean visible) {
		JsonObject object = new JsonObject();

		// Visibility
		object.addProperty("visible", visible);

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(player, "watermark", object);
	}
}