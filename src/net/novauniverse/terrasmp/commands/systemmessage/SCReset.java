package net.novauniverse.terrasmp.commands.systemmessage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.modules.systemmessage.TerraSMPSystemMessage;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class SCReset extends NovaSubCommand {
	public SCReset() {
		super("reset");

		setPermission("terrasmp.command.systemmessage");
		setPermissionDefaultValue(PermissionDefault.OP);

		setDescription("Set the system message");
		setAllowedSenders(AllowedSenders.ALL);

		setEmptyTabMode(true);

		setAliases(generateAliasList("sysmessage", "sysmsg"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		TerraSMPSystemMessage.getInstance().removeSystemMessage();
		sender.sendMessage(ChatColor.GREEN + "System message removed");
		return true;
	}
}
