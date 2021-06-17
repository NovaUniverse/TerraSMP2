package net.novauniverse.terrasmp.commands.processterrasmppurchase;

import java.io.IOException;
import java.time.LocalDateTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.utils.PermissionsUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.DateTimeUtils;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ProcessTerraSMPPurchaseCommand extends NovaCommand {
	public ProcessTerraSMPPurchaseCommand() {
		super("processterrasmppurchase", TerraSMP.getInstance());
		setPermission("terrasmp.command.processterrasmppurchase");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.ALL);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Required parameters: <Player> and <PackageName>");
			return false;
		}

		Player player = Bukkit.getServer().getPlayer(args[0]);

		if (player != null) {
			if (player.isOnline()) {
				processPurchase(player, args[1]);
			} else {
				Log.error("ProcessTerraSMPPurchaseCommand", "Player " + args[0] + " is not online");
			}
		} else {
			Log.error("ProcessTerraSMPPurchaseCommand", "Cant find player " + args[0]);
		}

		return true;
	}

	public void processPurchase(Player player, String packageName) {
		player.sendMessage(ChatColor.GOLD + "Processing purchase...");
		switch (packageName.toLowerCase()) {
		case "vip365":
			addVipDays(player, 365);
			break;

		case "vip90":
			addVipDays(player, 90);
			break;

		case "vip60":
			addVipDays(player, 60);
			break;

		case "vip30":
			addVipDays(player, 30);
			break;

		case "vip7":
			addVipDays(player, 7);
			break;

		case "vip3":
			addVipDays(player, 3);
			break;

		case "vip1":
			addVipDays(player, 1);
			break;
			
		case "hat":
			PermissionsUtils.addNode(player, "terrasmp.command.hat");
			player.sendMessage(ChatColor.GOLD + "You now have the " + ChatColor.AQUA + "/hat" + ChatColor.GOLD + " command");
			break;

		default:
			player.sendMessage(ChatColor.RED + "Could not process your purchase. Please contact an admin about this");
			Log.error("ProcessTerraSMPPurchaseCommand", "Invalid package name " + packageName);
			break;
		}
	}

	public void addVipDays(Player player, int days) {
		PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());
		LocalDateTime oldExpires = data.getRankExpires();

		if (oldExpires == null) {
			oldExpires = LocalDateTime.now();
		} else {
			if (oldExpires.isBefore(LocalDateTime.now())) {
				oldExpires = LocalDateTime.now();
			}
		}

		LocalDateTime newExpires = oldExpires.plusDays(days);

		data.setRankExpires(newExpires);

		try {
			data.save();
			player.sendMessage(ChatColor.GOLD + "Added " + days + " days to your VIP rank. Your rank will expire on " + DateTimeUtils.dateToString(newExpires));
		} catch (IOException e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to save rank expire date. Please contact an admin about this");
			Log.error("ProcessTerraSMPPurchaseCommand", "Failed to set vip rank expire date for " + player.getName() + ". Old: " + DateTimeUtils.dateToString(oldExpires) + " New: " + DateTimeUtils.dateToString(newExpires));
		}
	}
}