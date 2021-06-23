package net.novauniverse.terrasmp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import com.massivecraft.factions.entity.MPlayer;

import me.missionary.board.BoardManager;
import me.missionary.board.settings.BoardSettings;
import me.missionary.board.settings.ScoreDirection;
import net.novauniverse.terrasmp.api.WebServer;
import net.novauniverse.terrasmp.commands.flag.FlagCommand;
import net.novauniverse.terrasmp.commands.givemeadebugstick.GiveMeADebugStick;
import net.novauniverse.terrasmp.commands.hat.HatCommand;
import net.novauniverse.terrasmp.commands.invitetofaction.InviteToFactionCommand;
import net.novauniverse.terrasmp.commands.map.MapCommand;
import net.novauniverse.terrasmp.commands.processterrasmppurchase.ProcessTerraSMPPurchaseCommand;
import net.novauniverse.terrasmp.commands.removebed.RemoveBedCommand;
import net.novauniverse.terrasmp.commands.shop.ShopCommand;
import net.novauniverse.terrasmp.commands.systemmessage.SystemMessageCommand;
import net.novauniverse.terrasmp.commands.terrasmp.TerraSMPCommand;
import net.novauniverse.terrasmp.commands.wipeplayerdata.WipePlayerDataCommand;
import net.novauniverse.terrasmp.data.Continent;
import net.novauniverse.terrasmp.data.ContinentIndex;
import net.novauniverse.terrasmp.data.PlayerData;
import net.novauniverse.terrasmp.data.PlayerDataManager;
import net.novauniverse.terrasmp.modules.factionpowernerf.FactionPowerNerf;
import net.novauniverse.terrasmp.modules.hiddenplayers.HiddenPlayers;
import net.novauniverse.terrasmp.modules.labymod.TerraSMPLabymodIntegration;
import net.novauniverse.terrasmp.modules.requirelabymod.RequireLabymod;
import net.novauniverse.terrasmp.pluginmessagelisteners.WDLBlocker;
import net.novauniverse.terrasmp.scoreboard.TerraSMPBoardProvider;
import net.novauniverse.terrasmp.utils.CombatTagPlusUtils;
import net.novauniverse.terrasmp.utils.PlayerMessages;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;

public class TerraSMP extends JavaPlugin implements Listener {
	public final static List<String> COUNTRY_CODES = new ArrayList<String>();

	private static TerraSMP instance;

	private File playerDataFolder;
	private File systemMessageFile;

	private String systemMessage;
	private BossBar systemMessageBar;

	private WebServer webServer = null;

	private Location spawnLocation;

	private BoardManager boardManager;

	public static TerraSMP getInstance() {
		return instance;
	}

	public List<Continent> getContinents() {
		return ContinentIndex.getContinents();
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public File getPlayerDataFolder() {
		return playerDataFolder;
	}

	public void removeSystemMessage() {
		setSystemMessage(null);
	}

	public BoardManager getBoardManager() {
		return boardManager;
	}

	public void setSystemMessage(String systemMessage) {
		this.setSystemMessage(systemMessage, true);
	}

	public void setSystemMessage(String systemMessage, boolean save) {
		this.systemMessage = systemMessage;

		if (systemMessage == null) {
			if (systemMessageBar != null) {
				systemMessageBar.removeAll();
				systemMessageBar = null;
			}
		} else {
			if (systemMessageBar == null) {
				systemMessageBar = Bukkit.getServer().createBossBar("System Message", BarColor.BLUE, BarStyle.SOLID);
			}

			systemMessageBar.setVisible(true);
			systemMessageBar.setTitle(ChatColor.RED + "" + ChatColor.BOLD + "System Message: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', systemMessage));

			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (!systemMessageBar.getPlayers().contains(player)) {
					systemMessageBar.addPlayer(player);
				}
			}
		}

		if (save) {
			try {
				saveSystemMessageToFile(systemMessage == null ? "" : systemMessage);
			} catch (IOException e) {
				e.printStackTrace();
				Log.error("TerraSMP", "Failed to save system message to file. " + e.getClass().getName() + " " + e.getMessage());
			}
		}
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	@Nullable
	public Continent getContinent(String name) {
		if (name != null) {
			for (Continent continent : ContinentIndex.getContinents()) {
				if (continent.getName().equalsIgnoreCase(name)) {
					return continent;
				}
			}
		}

		return null;
	}

	private void saveSystemMessageToFile(String message) throws IOException {
		JSONObject massageJson = new JSONObject();

		massageJson.put("message", message);

		JSONFileUtils.saveJson(systemMessageFile, massageJson);
	}

	@Override
	public void onEnable() {
		TerraSMP.instance = this;
		saveDefaultConfig();

		playerDataFolder = new File(getDataFolder().getPath() + File.separator + "userdata");
		systemMessageFile = new File(getDataFolder().getPath() + File.separator + "system_message.json");

		systemMessage = null;
		systemMessageBar = null;

		String[] locales = Locale.getISOCountries();

		for (String countryCode : locales) {

			Locale obj = new Locale("", countryCode);

			COUNTRY_CODES.add(obj.getCountry().toUpperCase());
		}

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(playerDataFolder);

			if (!systemMessageFile.exists()) {
				saveSystemMessageToFile("");
			}

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

				// continents = ContinentReader.readContinents(continentFile,
				// Bukkit.getServer().getWorlds().get(0));
				ContinentIndex.loadContinents(continentFile, Bukkit.getServer().getWorlds().get(0));

				Log.info("TerraSMP", ContinentIndex.getContinents().size() + " continents loaded");
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

		// ModuleManager.loadModule(TerraSMPExtendedDebugging.class);

		// ModuleManager.loadModule(HiddenPlayers.class, true);
		// ModuleManager.loadModule(DisableEyeOfEnder.class, true);
		// ModuleManager.loadModule(DropPlayerHeadsOnKill.class, true);
		// ModuleManager.loadModule(FactionPowerNerf.class, true);
		// ModuleManager.loadModule(NoCrystalPvP.class, true);
		// ModuleManager.loadModule(TerraSMPLabymodIntegration.class, true);
		// ModuleManager.loadModule(ContinentSelectorSigns.class, true);
		// ModuleManager.loadModule(TerraSMPBoardProvider.class, true);
		// ModuleManager.loadModule(KDRManager.class, true);
		// ModuleManager.loadModule(TerraSMPShop.class, true);
		// ModuleManager.loadModule(TerraSMPTime.class, true);

		ModuleManager.scanForModules(this, "net.novauniverse.terrasmp");

		CommandRegistry.registerCommand(new SystemMessageCommand());
		CommandRegistry.registerCommand(new RemoveBedCommand());
		CommandRegistry.registerCommand(new MapCommand());
		CommandRegistry.registerCommand(new InviteToFactionCommand());
		CommandRegistry.registerCommand(new WipePlayerDataCommand());
		CommandRegistry.registerCommand(new ShopCommand());
		CommandRegistry.registerCommand(new TerraSMPCommand());
		CommandRegistry.registerCommand(new FlagCommand());
		CommandRegistry.registerCommand(new ProcessTerraSMPPurchaseCommand());
		CommandRegistry.registerCommand(new HatCommand());
		CommandRegistry.registerCommand(new GiveMeADebugStick());

		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "labymod3:main", ModuleManager.getModuleCast(RequireLabymod.class));
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "LMC", ModuleManager.getModuleCast(RequireLabymod.class));
		
		WDLBlocker wdlBlocker = new WDLBlocker();

		Bukkit.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "WDL|INIT", wdlBlocker);
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "wdl:init", wdlBlocker);

		PermissionRegistrator.registerPermission("terrasmp.moderator", "Moderator permissions", PermissionDefault.OP);

		FactionPowerNerf.getInstance().setPlayerLimit(getConfig().getInt("faction_nerf_player_limit"));
		Log.info("TerraSMP", "Faction power nerf limit: " + FactionPowerNerf.getInstance().getPlayerLimit());

		getCommand("suicide").setExecutor(new CommandExecutor() {
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

		boardManager = new BoardManager(this, BoardSettings.builder().boardProvider(TerraSMPBoardProvider.getInstance()).scoreDirection(ScoreDirection.UP).build());

		try {
			JSONObject systemMessageJson = JSONFileUtils.readJSONObjectFromFile(systemMessageFile);
			String savedSystemMessage = systemMessageJson.getString("message");

			if (savedSystemMessage.length() > 0) {
				setSystemMessage(savedSystemMessage, false);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}

		int apiPort = getConfig().getInt("web-server-port");
		try {

			Log.info("TerraSMP", ChatColor.GREEN + "Starting web server on port " + apiPort);
			webServer = new WebServer(apiPort);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TerraSMP", "Failed to start api on port " + apiPort);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Log.info("TerraSMP", "Trying to init CombatTagPlus");
				CombatTagPlusUtils.init();

				if (CombatTagPlusUtils.isAvailable()) {
					Log.info("TerraSMP", "CombatTagPlus has been initiated");
				} else {
					Log.error("TerraSMP", "Failed to initiate CombatTagPlus");
				}
			}
		}.runTask(this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);

		PlayerDataManager.unloadAll();

		if (webServer != null) {
			webServer.stop();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Log.trace(getName(), e.getClass().getName());
		Player player = e.getPlayer();

		if (systemMessageBar != null) {
			if (!systemMessageBar.getPlayers().contains(player)) {
				systemMessageBar.addPlayer(player);
			}
		}

		PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

		if (player.hasPermission("terrasmp.customflag")) {
			String flag = playerData.getFlag();
			if (flag != null) {
				TerraSMPLabymodIntegration.getInstance().getFlags().put(player.getUniqueId(), flag);
			}
		}

		/*
		 * if (getContinent(playerData.getStarterContinent()) == null) {
		 * HiddenPlayers.getInstance().hidePlayer(player);
		 * player.sendMessage(ChatColor.GOLD + "Please select your starter continent");
		 * player.sendMessage(ChatColor.GOLD +
		 * "If you are using labymod the continent selector screen should open within 5 seconds"
		 * ); player.teleport(spawnLocation);
		 * 
		 * new BukkitRunnable() {
		 * 
		 * @Override public void run() { if
		 * (!PlayerDataManager.getPlayerData(player.getUniqueId()).hasStarterContinent()
		 * ) { TerraSMPLabymodIntegration.getInstance().openContinentSelectorScreen(e.
		 * getPlayer()); } } }.runTaskLater(TerraSMP.getInstance(), 100L); }
		 */

		e.setJoinMessage(PlayerMessages.getJoinMessage(player));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		if (systemMessageBar != null) {
			if (systemMessageBar.getPlayers().contains(player)) {
				systemMessageBar.removePlayer(player);
			}
		}

		PlayerDataManager.unloadPlayerData(player.getUniqueId());

		e.setQuitMessage(PlayerMessages.getLeaveMessage(player));
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
	}
}