package net.novauniverse.terrasmp.modules.poweronkill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.entity.MPlayer;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class PowerOnKillModule extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "PowerOnKillModule";
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		if (player.getKiller() != null) {
			Player killer = player.getKiller();

			MPlayer mPlayer = MPlayer.get(player);

			if (mPlayer.getPower() >= 20) {
				MPlayer mKiller = MPlayer.get(killer);

				double gain = 5;
				if (mKiller.getPower() + gain > mKiller.getPowerMax()) {
					gain = mKiller.getPowerMax() - mKiller.getPower();
				}

				double newPower = mKiller.getPower() + gain;

				mKiller.setPower(newPower);

				killer.sendMessage(ChatColor.GREEN + "Gained " + ChatColor.AQUA + "+" + gain + ChatColor.GOLD + " power for killing " + ChatColor.AQUA + player.getDisplayName());
			}
		}
	}
}