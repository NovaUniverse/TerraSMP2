package net.novauniverse.terrasmp.commands.terrasmp.debug;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class TerraSMPDebugCommand extends NovaSubCommand {
	public TerraSMPDebugCommand() {
		super("debug");
		setPermission("terrasmp.command.terrasmp.debug");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Debug command for TerraSMP");
		setEmptyTabMode(true);

		addSubCommand(new TerraSMPDebugTestContinentSpawn());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "use /terrasmp debug help for help");
		return true;
	}
}