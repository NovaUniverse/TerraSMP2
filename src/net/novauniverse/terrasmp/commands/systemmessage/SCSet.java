package net.novauniverse.terrasmp.commands.systemmessage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.modules.systemmessage.TerraSMPSystemMessage;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class SCSet extends NovaSubCommand {
	public SCSet() {
		super("set");

		setPermission("terrasmp.command.systemmessage");
		setPermissionDefaultValue(PermissionDefault.OP);

		setDescription("Set the system message");
		setAllowedSenders(AllowedSenders.ALL);

		setAliases(generateAliasList("sysmessage", "sysmsg"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a message. If you want to remove the message use " + ChatColor.AQUA + "/systemmessage reset " + ChatColor.RED + "instead");
		} else {
			String text = "";

			for (String s : args) {
				text += s + " ";
			}

			TerraSMPSystemMessage.getInstance().setSystemMessage(text);

			sender.sendMessage(ChatColor.GREEN + "System message set to: " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
		}
		return true;
	}
}
