package net.novauniverse.terrasmp.modules.labymod;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.utils.LabyModProtocol;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TerraSMPLabymodIntegration extends NovaModule implements Listener {
	private Task task;

	@Override
	public String getName() {
		return "TerraSMPLabymodIntegration";
	}

	@Override
	public void onLoad() {
		this.task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					sendPlayerTitles(player);
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
		// this.sendWatermark(e.getPlayer(), true); // Breaks the tablist
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

	public void sendWatermark(Player player, boolean visible) {
		JsonObject object = new JsonObject();

		// Visibility
		object.addProperty("visible", visible);

		// Send to LabyMod using the API
		LabyModProtocol.sendLabyModMessage(player, "watermark", object);
	}

	public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance) {
		JsonObject economyObject = new JsonObject();
		JsonObject cashObject = new JsonObject();

		// Visibility
		cashObject.addProperty("visible", visible);

		// Amount
		cashObject.addProperty("balance", balance);

		/*
		 * // Icon (Optional) cashObject.addProperty( "icon", "<url to image>" );
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
}