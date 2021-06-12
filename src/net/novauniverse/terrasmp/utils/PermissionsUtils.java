package net.novauniverse.terrasmp.utils;

import org.bukkit.entity.Player;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;

public class PermissionsUtils {
	public static boolean isPlayerInGroup(Player player, String group) {
		return player.hasPermission("group." + group);
	}

	public static boolean hasNode(Player player, String node) {
		User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

		return user.data().contains(Node.builder(node).value(true).build(), NodeEqualityPredicate.EXACT).asBoolean();
	}

	public static void addNode(Player player, String node) {
		User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

		user.data().add(Node.builder(node).value(true).build());

		LuckPermsProvider.get().getUserManager().saveUser(user);
	}

	public static void removeNode(Player player, String node) {
		User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

		user.data().remove(Node.builder(node).value(true).build());

		LuckPermsProvider.get().getUserManager().saveUser(user);
	}
}