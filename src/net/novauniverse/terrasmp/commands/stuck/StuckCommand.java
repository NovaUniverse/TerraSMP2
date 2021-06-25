package net.novauniverse.terrasmp.commands.stuck;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.modules.terrasmpstuck.TerraSMPStuck;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class StuckCommand extends NovaCommand {
	public StuckCommand() {
		super("stuck", TerraSMP.getInstance());
		setPermission("terrasmp.command.stuck");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setDescription("Teleport to a radnom location on the map");
		setAllowedSenders(AllowedSenders.PLAYERS);
		setEmptyTabMode(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("confirm")) {
				Player p = (Player) sender;

				MPlayer mp = MPlayer.get(p.getUniqueId());

				if (mp.getPower() < 50) {
					p.sendMessage(ChatColor.RED + "You need atleast 50 power to use this command");
				} else {
					TerraSMPStuck.getInstance().teleportPlayer(p);
				}
				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "Warning: Using /stuck will remove " + (TerraSMPStuck.getInstance().getLoss() * 100) + "% of your power");
		sender.sendMessage(ChatColor.RED + "Please use" + ChatColor.AQUA + " /stuck confirm" + ChatColor.RED + " to do this");

		return true;
	}
}