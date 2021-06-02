package net.novauniverse.terrasmp.modules.extendeddebugging;

import org.bukkit.event.Listener;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = false)
public class TerraSMPExtendedDebugging extends NovaModule implements Listener {
	private static TerraSMPExtendedDebugging instance;
	
	public static TerraSMPExtendedDebugging getInstance() {
		return instance;
	}
	
	@Override
	public String getName() {
		return "TerraSMPExtendedDebugging";
	}
	
	@Override
	public void onLoad() {
		TerraSMPExtendedDebugging.instance = this;
	}
	
	@Override
	public void onEnable() throws Exception {
	}
	
	@Override
	public void onDisable() throws Exception {
	}
}