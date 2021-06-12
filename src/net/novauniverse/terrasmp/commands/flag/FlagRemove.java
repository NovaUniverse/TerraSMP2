package net.novauniverse.terrasmp.commands.flag;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.modules.labymod.TerraSMPLabymodIntegration;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class FlagRemove extends NovaSubCommand{
	public FlagRemove() {
		super("remove");
		
		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("terrasmp.command.flag");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setEmptyTabMode(true);
		setDescription("Remove your flag");
		
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;
		
		TerraSMPLabymodIntegration.getInstance().getFlags().remove(player.getUniqueId());
		PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());
		playerData.setFlag(null);
		try {
			playerData.save();
			sender.sendMessage(ChatColor.GREEN + "Your flag has been removed");
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Failed to save data");
			e.printStackTrace();
		}
		
		return true;
	}
}