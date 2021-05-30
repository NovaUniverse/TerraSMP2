package net.novauniverse.terrasmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.missionary.board.BoardManager;
import me.missionary.board.settings.BoardSettings;
import me.missionary.board.settings.ScoreDirection;
import net.novauniverse.terrasmp.commands.invitetofaction.InviteToFactionCommand;
import net.novauniverse.terrasmp.commands.map.MapCommand;
import net.novauniverse.terrasmp.commands.misc.suicide.SuicideCommand;
import net.novauniverse.terrasmp.commands.removebed.RemoveBedCommand;
import net.novauniverse.terrasmp.commands.shop.ShopCommand;
import net.novauniverse.terrasmp.commands.systemmessage.SystemMessageCommand;
import net.novauniverse.terrasmp.commands.terrasmp.TerraSMPCommand;
import net.novauniverse.terrasmp.commands.wipeplayerdata.WipePlayerDataCommand;
import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.ContinentReader;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.modules.continentselectorsigns.ContinentSelectorSigns;
import net.novauniverse.terrasmp.modules.deathmessage.TerraSMPDeathMessage;
import net.novauniverse.terrasmp.modules.disableeyeofender.DisableEyeOfEnder;
import net.novauniverse.terrasmp.modules.dropplayerheads.DropPlayerHeadsOnKill;
import net.novauniverse.terrasmp.modules.factionpowernerf.FactionPowerNerf;
import net.novauniverse.terrasmp.modules.hiddenplayers.HiddenPlayers;
import net.novauniverse.terrasmp.modules.intialjoinmanager.InitialJoinManager;
import net.novauniverse.terrasmp.modules.joinquitmessage.TerraSMPJoinQuitMessage;
import net.novauniverse.terrasmp.modules.kdr.KDRManager;
import net.novauniverse.terrasmp.modules.labymod.TerraSMPLabymodIntegration;
import net.novauniverse.terrasmp.modules.nocrystalpvp.NoCrystalPvP;
import net.novauniverse.terrasmp.modules.playerdatagarbagecollector.PlayerDataGarbageCollector;
import net.novauniverse.terrasmp.modules.respawnmanager.TerraSMPRespawnManager;
import net.novauniverse.terrasmp.modules.shop.TerraSMPShop;
import net.novauniverse.terrasmp.modules.systemmessage.TerraSMPSystemMessage;
import net.novauniverse.terrasmp.modules.terrasmptime.TerraSMPTime;
import net.novauniverse.terrasmp.pluginmessagelisteners.WDLBlocker;
import net.novauniverse.terrasmp.scoreboard.TerraSMPBoardProvider;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;

public class TerraSMP extends JavaPlugin implements Listener {
	private static TerraSMP instance;

	private List<Continent> continents;
	private File playerDataFolder;

	private Location spawnLocation;

	private BoardManager boardManager;

	public static TerraSMP getInstance() {
		return instance;
	}

	public List<Continent> getContinents() {
		return continents;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public File getPlayerDataFolder() {
		return playerDataFolder;
	}

	public BoardManager getBoardManager() {
		return boardManager;
	}

	@Nullable
	public Continent getContinent(String name) {
		if (name != null) {
			for (Continent continent : continents) {
				if (continent.getName().equalsIgnoreCase(name)) {
					return continent;
				}
			}
		}

		return null;
	}

	@Override
	public void onEnable() {
		TerraSMP.instance = this;
		saveDefaultConfig();

		continents = new ArrayList<Continent>();
		playerDataFolder = new File(getDataFolder().getPath() + File.separator + "userdata");

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(playerDataFolder);

			saveDefaultConfig();
		} catch (Exception e) {
			Log.fatal("TerraSMP", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		spawnLocation = new Location(Bukkit.getServer().getWorlds().get(0), getConfig().getDouble("spawn_x"), getConfig().getDouble("spawn_y"), getConfig().getDouble("spawn_z"), getConfig().getLong("spawn_yaw"), getConfig().getLong("spawn_pitch"));

		File continentFile = new File(getDataFolder().getPath() + File.separator + "continents.json");
		if (continentFile.exists()) {
			try {
				continents = ContinentReader.readContinents(continentFile, Bukkit.getServer().getWorlds().get(0));
				Log.info("TerraSMP", continents.size() + " continents loaded");
			} catch (Exception e) {
				Log.fatal("Failed to read continent file " + continentFile.getPath() + ". Caused by " + e.getClass().getName());
				e.printStackTrace();
			}
		} else {
			Log.error("TerraSMP", "Continent file: " + continentFile.getPath() + " does not exist");
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			PlayerDataManager.getPlayerData(player.getUniqueId());
		}

		ModuleManager.loadModule(HiddenPlayers.class, true);
		ModuleManager.loadModule(DisableEyeOfEnder.class, true);
		ModuleManager.loadModule(DropPlayerHeadsOnKill.class, true);
		ModuleManager.loadModule(FactionPowerNerf.class, true);
		ModuleManager.loadModule(NoCrystalPvP.class, true);
		ModuleManager.loadModule(TerraSMPLabymodIntegration.class, true);
		ModuleManager.loadModule(ContinentSelectorSigns.class, true);
		ModuleManager.loadModule(TerraSMPBoardProvider.class, true);
		ModuleManager.loadModule(KDRManager.class, true);
		ModuleManager.loadModule(TerraSMPShop.class, true);
		ModuleManager.loadModule(TerraSMPTime.class, true);
		ModuleManager.loadModule(TerraSMPDeathMessage.class, true);
		ModuleManager.loadModule(TerraSMPRespawnManager.class, true);
		ModuleManager.loadModule(TerraSMPSystemMessage.class, true);
		ModuleManager.loadModule(InitialJoinManager.class, true);
		ModuleManager.loadModule(PlayerDataGarbageCollector.class, true);
		ModuleManager.loadModule(TerraSMPJoinQuitMessage.class, true);
		
		CommandRegistry.registerCommand(new SystemMessageCommand());
		CommandRegistry.registerCommand(new RemoveBedCommand());
		CommandRegistry.registerCommand(new MapCommand());
		CommandRegistry.registerCommand(new InviteToFactionCommand());
		CommandRegistry.registerCommand(new WipePlayerDataCommand());
		CommandRegistry.registerCommand(new ShopCommand());
		CommandRegistry.registerCommand(new TerraSMPCommand());

		new SuicideCommand();
		
		WDLBlocker wdlBlocker = new WDLBlocker();

		Bukkit.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "WDL|INIT", wdlBlocker);
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "wdl:init", wdlBlocker);

		PermissionRegistrator.registerPermission("terrasmp.moderator", "Moderator permissions", PermissionDefault.OP);

		FactionPowerNerf.getInstance().setPlayerLimit(getConfig().getInt("faction_nerf_player_limit"));
		Log.info("TerraSMP", "Faction power nerf limit: " + FactionPowerNerf.getInstance().getPlayerLimit());

		boardManager = new BoardManager(this, BoardSettings.builder().boardProvider(TerraSMPBoardProvider.getInstance()).scoreDirection(ScoreDirection.UP).build());

	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);

		PlayerDataManager.unloadAll();
	}

	public static void setStarterContinent(Player player, Continent continent) {
		PlayerDataManager.getPlayerData(player.getUniqueId()).setStarterContinent(continent.getName());
		PlayerDataManager.savePlayerData(player.getUniqueId());

		HiddenPlayers.getInstance().showPlayer(player);

		Location location = continent.getRandomSpawnLocation();

		if (location != null) {
			player.teleport(location.add(0, 2, 0));
			return;
		}

		player.sendMessage(ChatColor.RED + "Failed to find a spawn location. Please try again");
		Log.warn("TerraSMP", "Failed to get spawn location for player " + player.getName() + " in continent " + continent.getName());
	}
}