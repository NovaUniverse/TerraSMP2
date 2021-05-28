package net.novauniverse.terrasmp.pluginmessagelisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class WDLBlocker implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		player.kickPlayer(ChatColor.RED + "World downloader is not allowed on this server. Please uninstall world downloader and join again");
	}
}