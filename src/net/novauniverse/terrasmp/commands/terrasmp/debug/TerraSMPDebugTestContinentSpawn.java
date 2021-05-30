package net.novauniverse.terrasmp.commands.terrasmp.debug;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.ContinentIndex;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class TerraSMPDebugTestContinentSpawn extends NovaSubCommand {

	public TerraSMPDebugTestContinentSpawn() {
		super("testcontinentspawn");

		setPermission("terrasmp.command.terrasmp.debug.testcontinentspawn");
		setPermissionDefaultValue(PermissionDefault.OP);
		setFilterAutocomplete(true);
		setAllowedSenders(AllowedSenders.ENTITY);
		setDescription("Test the spawn location of a continent");
		setUseage("/terrasmp debug testcontinentspawn <Continent>");
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Missing argument: Continent");
			return false;
		}

		String continentName = args[0];

		for (Continent continent : ContinentIndex.getContinents()) {
			if (continent.getName().equalsIgnoreCase(continentName)) {
				Location location = continent.getRandomSpawnLocation();

				location.getChunk().load();
				((Entity) sender).teleport(location.add(0, 1, 0));

				sender.sendMessage(ChatColor.GREEN + "Success");

				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "Could not fine a continent with that name. Use tab to autocomplete continent names");

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> continents = new ArrayList<String>();

		for (Continent continent : ContinentIndex.getContinents()) {
			continents.add(continent.getName());
		}

		return continents;
	}
}