package net.novauniverse.terrasmp.scoreboard;

import java.util.List;

import org.bukkit.entity.Player;

public interface ScoreboardModifier {
	public void process(List<String> lines, Player player);
}