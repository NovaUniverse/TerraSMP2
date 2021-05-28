package net.novauniverse.terrasmp.commands.terrasmp.fake.join;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.utils.PlayerMessages;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class FakeJoinCommand extends NovaSubCommand {
	public FakeJoinCommand() {
		super("fakejoin");

		setPermission("terrasmp.command.terrasmp.fakejoin");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Fake a join message");
		setAllowedSenders(AllowedSenders.PLAYERS);
		addHelpSubCommand();
		setEmptyTabMode(false);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		Bukkit.getServer().broadcastMessage(PlayerMessages.getJoinMessage(player));
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The join message above for " + player.getName() + " is fake");

		return true;
	}
}