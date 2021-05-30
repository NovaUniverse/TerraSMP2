package net.novauniverse.terrasmp.modules.nocrystalpvp;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class NoCrystalPvP extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "NoCrystalPvP";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.END_CRYSTAL) {
				if (e.getPlayer().getWorld().getEnvironment() == Environment.NORMAL) {
					Log.trace("NoCrystalPvP", "Preventing player " + e.getPlayer() + " from placing an end crystal");
					e.setCancelled(true);
				}
			}
		}
	}
}