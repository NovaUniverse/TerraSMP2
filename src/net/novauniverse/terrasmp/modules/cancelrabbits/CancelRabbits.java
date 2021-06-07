package net.novauniverse.terrasmp.modules.cancelrabbits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class CancelRabbits extends NovaModule implements Listener {
	private Task yeetThemOutTheWorld;

	@Override
	public String getName() {
		return "CancelRabbits";
	}

	@Override
	public void onLoad() {
		yeetThemOutTheWorld = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (World world : Bukkit.getServer().getWorlds()) {
					for (Entity entity : world.getEntitiesByClass(Rabbit.class)) {
						Location location = entity.getLocation().clone();

						location.setY(-1000);

						entity.teleport(location);
					}
				}
			}
		}, 100L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(yeetThemOutTheWorld);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(yeetThemOutTheWorld);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntity() instanceof Rabbit) {
			e.setCancelled(true);
		}
	}
}