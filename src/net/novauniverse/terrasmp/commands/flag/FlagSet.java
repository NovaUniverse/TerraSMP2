package net.novauniverse.terrasmp.commands.flag;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.modules.labymod.TerraSMPLabymodIntegration;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class FlagSet extends NovaSubCommand {
	public FlagSet() {
		super("set");

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("terrasmp.customflag");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(false);
		setFilterAutocomplete(true);
		setDescription("Set your flag");
		setUseage("/flag set <CountryCode>");

		addHelpSubCommand();
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		return TerraSMP.COUNTRY_CODES;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide the 2 letter country code for the flag you want. You can use tab to auto complete valid country codes");
		} else {
			String code = args[0].toUpperCase();

			if (TerraSMP.COUNTRY_CODES.contains(code)) {
				TerraSMPLabymodIntegration.getInstance().getFlags().put(player.getUniqueId(), code);
				PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());
				playerData.setFlag(code);
				try {
					playerData.save();
					sender.sendMessage(ChatColor.GREEN + "Your flag has been updated");
				} catch (IOException e) {
					sender.sendMessage(ChatColor.RED + "Failed to save data");
					e.printStackTrace();
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Please provide a valid 2 letter country code. You can use tab to auto complete valid country codes");
			}
		}

		return true;
	}
}