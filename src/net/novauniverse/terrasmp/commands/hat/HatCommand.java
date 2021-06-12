package net.novauniverse.terrasmp.commands.hat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class HatCommand extends NovaCommand {
	public HatCommand() {
		super("hat", TerraSMP.getInstance());
		setPermission("terrasmp.command.hat");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Hat command pog");
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;
		PlayerInventory inv = player.getInventory();
		ItemStack held = inv.getItemInMainHand();
		ItemStack helm = inv.getHelmet();

		inv.setHelmet(held);
		inv.setItemInMainHand(helm);

		return true;
	}
}