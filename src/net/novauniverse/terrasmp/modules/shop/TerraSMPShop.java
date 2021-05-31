package net.novauniverse.terrasmp.modules.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import masecla.villager.classes.VillagerInventory;
import masecla.villager.classes.VillagerTrade;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = true)
public class TerraSMPShop extends NovaModule {
	private static TerraSMPShop instance;

	public static final int MAX_TRADE_USES = 42069;
	private List<VillagerTrade> trades;

	@Override
	public void onLoad() {
		TerraSMPShop.instance = this;
		trades = new ArrayList<VillagerTrade>();
	}

	@Override
	public void onEnable() throws Exception {
		trades.clear();

		trades.add(new VillagerTrade(emeralds(2), new ItemBuilder(Material.GLOWSTONE).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(1), new ItemBuilder(Material.NETHERRACK).setAmount(16).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(1), new ItemBuilder(Material.SLIME_BALL).setAmount(2).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(1), new ItemBuilder(Material.COAL_BLOCK).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(1), new ItemBuilder(Material.NETHER_WARTS).setAmount(5).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(2), new ItemBuilder(Material.QUARTZ).setAmount(16).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(64), emeralds(64), new ItemBuilder(Material.TOTEM).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(10), new ItemBuilder(Material.SOUL_SAND).setAmount(16).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(10), new ItemBuilder(Material.BLAZE_ROD).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(5), new ItemBuilder(Material.GHAST_TEAR).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(5), new ItemBuilder(Material.MAGMA_CREAM).setAmount(1).build(), MAX_TRADE_USES));
		trades.add(new VillagerTrade(emeralds(1), new ItemBuilder(Material.SULPHUR).setAmount(5).build(), MAX_TRADE_USES));
	}

	@Override
	public void onDisable() throws Exception {
		trades.clear();
	}

	@Override
	public String getName() {
		return "TerraSMPShop";
	}

	public static TerraSMPShop getInstance() {
		return instance;
	}

	private ItemStack emeralds(int amount) {
		return new ItemBuilder(Material.EMERALD).setAmount(amount).build();
	}

	public boolean open(Player player) {
		if (!this.isEnabled()) {
			return false;
		}

		VillagerInventory villagerInventory = new VillagerInventory(trades, player);

		villagerInventory.setName("TerraSMP Shop");

		villagerInventory.open();

		return true;
	}
}