package net.novauniverse.terrasmp.commands.invitetofaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class InviteToFactionCommand extends NovaCommand {
	public InviteToFactionCommand() {
		super("invitetofaction", TerraSMP.getInstance());

		setDescription("Invite a player to your faction");
		setPermission("terrasmp.command.invitetofaction");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length > 0) {
			Bukkit.getServer().dispatchCommand(sender, "f add " + args[0]);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Please provide a player");
		}
		return false;
	}
}
