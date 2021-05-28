package net.novauniverse.terrasmp.commands.terrasmp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.commands.terrasmp.debug.TerraSMPDebugCommand;
import net.novauniverse.terrasmp.commands.terrasmp.fake.join.FakeJoinCommand;
import net.novauniverse.terrasmp.commands.terrasmp.fake.leave.FakeLeaveCommand;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class TerraSMPCommand  extends NovaCommand {
	public TerraSMPCommand() {
		super("terrasmp", TerraSMP.getInstance());
		
		setDescription("Command to manage TerraSMP");
		setPermission("terrasmp.command.terrasmp");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		
		addSubCommand(new TerraSMPDebugCommand());
		
		addSubCommand(new FakeJoinCommand());
		addSubCommand(new FakeLeaveCommand());
		
		addHelpSubCommand();
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "use /terrasmp help for help");
		return true;
	}
}