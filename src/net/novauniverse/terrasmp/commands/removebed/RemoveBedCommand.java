package net.novauniverse.terrasmp.commands.removebed;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class RemoveBedCommand extends NovaCommand {
	public RemoveBedCommand() {
		super("removebedlocation", TerraSMP.getInstance());

		setAliases(generateAliasList("removebed"));

		setPermission("terramsp.command.removebedlocation");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Removes your bed spawn location");

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("confirm")) {
				Player p = (Player) sender;

				p.setBedSpawnLocation(null);

				sender.sendMessage(ChatColor.GREEN + "Bed respawn location removed");
				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "Please use" + ChatColor.AQUA + " /removebedlocation confirm" + ChatColor.RED + " to remove your bed location. If your faction home location is trapped use " + ChatColor.AQUA + "/f unsethome");

		return true;
	}
}