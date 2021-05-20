package net.novauniverse.terrasmp.commands.systemmessage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class SystemMessageCommand extends NovaCommand {

	public SystemMessageCommand() {
		super("systemmessage", TerraSMP.getInstance());

		setPermission("terrasmp.command.systemmessage");
		setPermissionDefaultValue(PermissionDefault.OP);

		setDescription("Command to manage system messages");
		setAllowedSenders(AllowedSenders.ALL);

		setEmptyTabMode(true);
		addSubCommand(new SCSet());
		addSubCommand(new SCReset());
		addHelpSubCommand();
		setAliases(generateAliasList("sysmessage", "sysmsg"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use" + ChatColor.AQUA + " /systemmessage help " + ChatColor.GOLD + "for help");
		return true;
	}
}