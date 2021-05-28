package net.novauniverse.terrasmp.modules.hiddenjoinquitmessages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;

public class HiddenJoinQuitMessages extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "HiddenJoinQuitMessages";
	}
	
	@Override
	public void onLoad() {
		PermissionRegistrator.registerPermission("terrasmp.nojoinmessage", "Hide join message", PermissionDefault.FALSE);
		PermissionRegistrator.registerPermission("terrasmp.noquitmessage", "Hide quit message", PermissionDefault.FALSE);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (player.hasPermission("terrasmp.nojoinmessage")) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "(silent join) " + ChatColor.RESET + e.getJoinMessage());

			e.setJoinMessage(null);

			player.sendMessage(ChatColor.AQUA + "Join message hidden");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		if (player.hasPermission("terrasmp.noquitmessage")) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "(silent quit) " + ChatColor.RESET + e.getQuitMessage());

			e.setQuitMessage(null);
		}
	}
}