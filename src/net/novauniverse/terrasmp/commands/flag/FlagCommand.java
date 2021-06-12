package net.novauniverse.terrasmp.commands.flag;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class FlagCommand extends NovaCommand {
	public FlagCommand() {
		super("flag", TerraSMP.getInstance());
		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("terrasmp.command.flag");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setEmptyTabMode(true);
		setDescription("Command to manage your flag in the tablist");

		addSubCommand(new FlagRemove());
		addSubCommand(new FlagSet());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Use " + ChatColor.AQUA + "/flag help" + ChatColor.GREEN + " for help");
		return true;
	}
}