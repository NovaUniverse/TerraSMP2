package net.novauniverse.terrasmp.api.status;

import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.terrasmp.modules.hiddenplayers.HiddenPlayers;
import net.novauniverse.terrasmp.modules.terrasmptime.TerraSMPTime;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.utils.DateTimeUtils;
import net.zeeraa.novacore.spigot.NovaCore;

@SuppressWarnings("restriction")
public class StatusHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) {
		JSONObject json = new JSONObject();

		/* Server */
		JSONObject server = new JSONObject();

		server.put("recent-tps", NovaCore.getInstance().getVersionIndependentUtils().getRecentTps());
		server.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
		server.put("terrasmp-time", DateTimeUtils.dateToString(TerraSMPTime.getInstance().getTerraSMPCurrentDate()));

		json.put("server", server);

		/* Players */
		JSONObject players = new JSONObject();
		JSONArray playerList = new JSONArray();

		int online = 0;

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(HiddenPlayers.getInstance().isHidden(player)) {
				continue;
			}
			
			online ++;
			
			JSONObject jp = new JSONObject();

			jp.put("uuid", player.getUniqueId().toString());
			jp.put("name", player.getName());
			jp.put("display-name", player.getDisplayName());

			playerList.put(jp);
		}
		
		players.put("max", Bukkit.getServer().getMaxPlayers());
		players.put("online", online);

		players.put("list", playerList);
		json.put("players", players);

		/* Factions */

		JSONArray factions = new JSONArray();

		for (Faction faction : FactionColl.get().getAll()) {
			JSONObject fj = new JSONObject();

			/* Misc */
			fj.put("name", ChatColor.stripColor(faction.getName()));
			fj.put("age", faction.getAge());
			fj.put("land-count", faction.getLandCount());

			/* Power */
			JSONObject power = new JSONObject();

			power.put("current", faction.getPower());
			power.put("max", faction.getPowerMax());
			power.put("boost", faction.getPowerBoost());

			fj.put("power", power);

			/* Flags */
			JSONObject flags = new JSONObject();

			for (MFlag flag : faction.getFlags().keySet()) {
				flags.put(flag.getName(), faction.getFlags().get(flag));
			}

			fj.put("flags", flags);

			/* Players */
			JSONArray fPlayers = new JSONArray();

			for (MPlayer player : faction.getMPlayers()) {
				JSONObject jp = new JSONObject();

				jp.put("uuid", player.getUuid());
				jp.put("name", player.getName());
				jp.put("role", player.getRole().name());

				/* Power */
				JSONObject ppower = new JSONObject();

				ppower.put("current", player.getPower());
				ppower.put("max", player.getPowerMax());
				ppower.put("boost", player.getPowerBoost());

				jp.put("power", power);

				fPlayers.put(jp);
			}

			fj.put("players", fPlayers);

			factions.put(fj);
		}

		json.put("factions", factions);

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					String response = json.toString(4);

					exchange.getResponseHeaders().set("Content-Type", "Content-type: application/json; charset=utf-8");
					exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes(StandardCharsets.UTF_8));
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}