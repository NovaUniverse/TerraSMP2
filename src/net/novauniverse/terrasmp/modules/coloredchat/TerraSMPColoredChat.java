package net.novauniverse.terrasmp.modules.coloredchat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPColoredChat extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TerraSMPColoredChat";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().hasPermission("terrasmp.coloredchat")) {
			e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		}
	}
}