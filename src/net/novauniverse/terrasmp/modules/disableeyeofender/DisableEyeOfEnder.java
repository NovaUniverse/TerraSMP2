package net.novauniverse.terrasmp.modules.disableeyeofender;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class DisableEyeOfEnder extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "DisableEyeOfEnder";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (NovaCore.getInstance().getVersionIndependentUtils().getItemInMainHand(e.getPlayer()).getType() == Material.EYE_OF_ENDER) {
				e.setCancelled(true);
			}
		}
	}
}