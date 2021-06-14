package com.github.rumoteam.minecraft.plugins.altauth.utils;

import java.io.File;

import org.bukkit.entity.Player;

import com.github.rumoteam.minecraft.plugins.altauth.init.Plugin;

public final class ClientUtils {
	private ClientUtils() {
	}

	public static Player getPlayer(String user) throws IllegalArgumentException {
		for (Player player : Plugin.plugin.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(user)) {
				return player;
			}
		}
		throw new IllegalArgumentException("player " + user + " not found");
	}

	public static File playerDataFile(String userName) {
		Player player = getPlayer(userName);
		String name = player.getName();
		return new File(Plugin.getPlayersDir(), name);
	}

	public static boolean playerRegistered(String userName) {
		boolean ret;
		try {
			ret = playerDataFile(userName).exists();
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	public static boolean playerRegisteredByFile(String userName) {
		if (!Plugin.getPlayersDir().exists()) {
			Plugin.getPlayersDir().mkdirs();
		}
		for (File file : Plugin.getPlayersDir().listFiles()) {
			if (file.isFile()) {
				String fileName = file.getName();
				if (fileName.equalsIgnoreCase(userName)) {
					return true;
				}
			}
		}
		return false;
	}
}
