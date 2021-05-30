package net.novauniverse.terrasmp.modules.labymod;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

import net.labymod.serverapi.bukkit.event.BukkitMessageReceiveEvent;
import net.labymod.serverapi.common.widgets.WidgetScreen;
import net.labymod.serverapi.common.widgets.components.widgets.ButtonWidget;
import net.labymod.serverapi.common.widgets.components.widgets.ImageWidget;
import net.labymod.serverapi.common.widgets.components.widgets.LabelWidget;
import net.labymod.serverapi.common.widgets.util.Anchor;
import net.labymod.serverapi.common.widgets.util.EnumScreenAction;
import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.ContinentIndex;
import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.utils.LabyModProtocol;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPLabymodIntegration extends NovaModule implements Listener {
	private Task task;

	private static TerraSMPLabymodIntegration instance;

	public static final String KILLS_ICON = "https://novauniverse.net/cdn/terrasmp/icon_kills.png";
	public static final String DEATHS_ICON = "https://novauniverse.net/cdn/terrasmp/icon_deaths.png";
	public static final String SERVER_BANNER = "https://novauniverse.net/cdn/terrasmp/labymod_ingame_banner.png";

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
					updateKDR(player);
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.sendTabImage(e.getPlayer());
		this.sendPlayerTitles(e.getPlayer());
		this.sendCurrentPlayingGamemode(e.getPlayer(), true, "TerraSMP");
		this.setMiddleClickActions(e.getPlayer());
		this.updateKDR(e.getPlayer());
		// this.sendWatermark(e.getPlayer(), true); // Breaks the tablist
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.sendCurrentPlayingGamemode(e.getPlayer(), false, "TerraSMP");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBukkitMessageReceiveEvent(BukkitMessageReceiveEvent e) {
		Log.trace(this.getName(), e.getClass().getName() + ": " + e.getPlayer().getName() + " | " + e.getMessageKey() + " | " + e.getMessageContent().toString());
		try {
			if (e.getMessageKey().equalsIgnoreCase("screen")) {
				JSONObject json = new JSONObject(e.getMessageContent().toString());

				if (json.has("id") && json.has("type")) {
					if (json.getInt("id") == 1 && json.getInt("type") == 1) {
						processCountrySelection(e.getPlayer(), json);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.warn(this.getName(), "Kicking " + e.getPlayer() + " since their plugin message caused a " + ex.getClass().getName() + " with message " + ex.getMessage());
			e.getPlayer().kickPlayer(ChatColor.RED + "Invalid response from client");
		}
	}

	private void processCountrySelection(Player player, JSONObject json) {
		PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());

		if (json.has("widget_id")) {
			int widgetId = json.getInt("widget_id");

			if (widgetId >= 100) {
				if (data.hasStarterContinent()) {
					player.sendMessage(ChatColor.RED + "You have already selected your starter continent");
					return;
				}

				int realId = widgetId - 100;

				if (realId >= ContinentIndex.getContinents().size()) {
					player.sendMessage(ChatColor.RED + "Failed to read continent id");
					return;
				}

				Continent continent = ContinentIndex.getContinents().get(realId);

				player.sendMessage(ChatColor.GOLD + "Selected " + ChatColor.AQUA + continent.getDisplayName() + ChatColor.GOLD + " as your starter continent");

				TerraSMP.setStarterContinent(player, continent);
			}
		}
	}

	private void sendTabImage(Player player) {
		JsonObject object = new JsonObject();
		object.addProperty("url", SERVER_BANNER);
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

	public void updateKDR(Player player) {
		PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());

		int kills = data.getKills();
		int deaths = data.getDeaths();

		updateBalanceDisplay(player, EnumBalanceType.CASH, true, kills, KILLS_ICON);
		updateBalanceDisplay(player, EnumBalanceType.BANK, true, deaths, DEATHS_ICON);
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

		LabelWidget labelWidget1 = new LabelWidget(1, anchor, 0, -25, "Select your starter continent", 1, 1);
		screen.addWidget(labelWidget1);

		LabelWidget labelWidget2 = new LabelWidget(2, anchor, 0, -15, "this can't be changed later", 1, 0.5);
		screen.addWidget(labelWidget2);

		ImageWidget image = new ImageWidget(3, anchor, -100, -80, 200, 40, SERVER_BANNER);
		screen.addWidget(image);

		boolean alternating = false;
		int lastY = 0;

		for (int i = 0; i < ContinentIndex.getContinents().size(); i++) {
			int x = alternating ? -110 : 10;
			lastY = (int) (25 * Math.floor(i / 2));

			alternating = !alternating;

			String text = ContinentIndex.getContinents().get(i).getDisplayName();

			ButtonWidget button = new ButtonWidget(i + 100, anchor, x, lastY, text, 100, 20);
			button.setCloseScreenOnClick(true);
			// button.setValue(TerraSMP.getInstance().getContinents().get(i).getName());
			screen.addWidget(button);
		}

		ButtonWidget closeButton = new ButtonWidget(4, anchor, -50, lastY + 40, "Close", 100, 20);
		closeButton.setCloseScreenOnClick(true);
		screen.addWidget(closeButton);

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