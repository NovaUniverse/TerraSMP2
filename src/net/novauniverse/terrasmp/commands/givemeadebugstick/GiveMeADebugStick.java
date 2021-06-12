package net.novauniverse.terrasmp.commands.givemeadebugstick;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.terrasmp.TerraSMP;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class GiveMeADebugStick extends NovaCommand {
	public GiveMeADebugStick() {
		super("givemeadebugstick", TerraSMP.getInstance());
		setPermission("terrasmp.command.ruinthewholeserver");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("AAAAAAAAAAAAAAAAAAAA");
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		ItemBuilder builder = new ItemBuilder(Material.STICK);

		builder.setName(ChatColor.BOLD + "" + ChatColor.GOLD + "Debug stick");
		builder.addLore("Debug stuff by beating it to death");
		builder.setUnbreakable(true);
		for (Enchantment enchantment : Enchantment.values()) {
			builder.addEnchant(enchantment, 1000, true);
		}

		Player player = (Player) sender;
		player.getInventory().addItem(builder.build());

		return true;
	}
}