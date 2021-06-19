package net.novauniverse.terrasmp.commands.terrasmp.fake.leave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.utils.PlayerMessages;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class FakeLeaveCommand extends NovaSubCommand {
	public FakeLeaveCommand() {
		super("fakeleave");

		setPermission("terrasmp.command.terrasmp.fakeleave");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Fake a quit message");
		setAllowedSenders(AllowedSenders.PLAYERS);
		addHelpSubCommand();
		setEmptyTabMode(false);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		Bukkit.getServer().broadcastMessage(PlayerMessages.getLeaveMessage(player));
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The join message above for " + player.getName() + " is fake");

		return true;
	}
}