package net.novauniverse.terrasmp.modules.dropplayerheads;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class DropPlayerHeadsOnKill extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "DropPlayerHeadOnKill";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		if(player.getName().toLowerCase().startsWith("pvplogger")) {
			return;
		}
		
		if (player.getKiller() != null) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			meta.setOwningPlayer(player);

			item.setItemMeta(meta);

			player.getWorld().dropItem(player.getLocation(), item);
		}
	}
}