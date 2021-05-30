package net.novauniverse.terrasmp.commands.misc.suicide;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.MPlayer;

import net.novauniverse.terrasmp.TerraSMP;

public class SuicideCommand {
	public SuicideCommand() {
		TerraSMP.getInstance().getCommand("suicide").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (sender instanceof Player) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("confirm")) {
							Player p = (Player) sender;

							MPlayer mp = MPlayer.get(p.getUniqueId());

							mp.setPower(0.0);

							p.setHealth(0);

							return true;
						}
					}

					sender.sendMessage(ChatColor.RED + "Warning: Using /suicide will reset your power to 0");
					sender.sendMessage(ChatColor.RED + "Please use" + ChatColor.AQUA + " /suicide confirm" + ChatColor.RED + " to do this");

					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can use this command");
				}
				return false;
			}
		});
	}
}