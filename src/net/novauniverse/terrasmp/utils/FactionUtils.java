package net.novauniverse.terrasmp.utils;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;

public class FactionUtils {
	public static boolean hasFaction(Player player) {
		return FactionUtils.hasFaction(player.getUniqueId());
	}

	public static boolean hasFaction(UUID uuid) {
		MPlayer mPlayer = MPlayer.get(uuid);

		return FactionUtils.hasFaction(mPlayer);
	}

	public static boolean hasFaction(MPlayer mPlayer) {
		Faction faction = mPlayer.getFaction();

		if (faction == null) {
			return false;
		}

		return !isSystemFaction(faction);
	}

	public static boolean isSystemFaction(Faction faction) {
		if (faction.getId().equalsIgnoreCase(FactionColl.get().getWarzone().getId())) {
			return true;
		}

		if (faction.getId().equalsIgnoreCase(FactionColl.get().getSafezone().getId())) {
			return true;
		}

		if (faction.getId().equalsIgnoreCase(FactionColl.get().getNone().getId())) {
			return true;
		}

		return false;
	}
}