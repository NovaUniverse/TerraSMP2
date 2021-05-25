package net.novauniverse.terrasmp.commands.wipeplayerdata;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class WipePlayerDataCommand extends NovaCommand {
	public WipePlayerDataCommand() {
		super("wipeplayerdata", TerraSMP.getInstance());

		setEmptyTabMode(false);
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("terrasmp.command.wipeplayerdata");
		setPermissionDefaultValue(PermissionDefault.OP);
		setUseage("/wipeplayerdata <Player>");
		setDescription("Wipe the player data of a player");
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a player to wipe");
		} else {
			Player player = Bukkit.getServer().getPlayer(args[0]);

			if (player != null) {
				if (player.isOnline()) {
					MPlayer mp = MPlayer.get(player.getUniqueId());

					mp.setPower(0.0);

					player.kickPlayer(ChatColor.GOLD + "Wiping player data...");

					PlayerDataManager.wipe(player.getUniqueId());

					sender.sendMessage(ChatColor.GREEN + "The player data of " + ChatColor.AQUA + player.getName() + ChatColor.GOLD + " has been wiped");
				} else {
					sender.sendMessage(ChatColor.RED + "That player is not online");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find a player with that name");
			}
		}

		return true;
	}
}