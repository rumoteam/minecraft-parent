package com.github.rumoteam.minecraft.plugins.bordercontrol.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import com.github.rumoteam.minecraft.plugins.bordercontrol.header.BCHeader;
import com.github.rumoteam.minecraft.plugins.bordercontrol.init.Plugin;

public class MoveListener implements Listener {

	private Plugin plugin;

	public MoveListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	private static void fly(PlayerMoveEvent playerMoveEvent) {
		double y = playerMoveEvent.getTo().getY();

		double yConfCheck = BCHeader.getConfig().getYcheck();

		if (y >= yConfCheck) {
			double yConfTp = BCHeader.getConfig().getYteleport();
			@NotNull
			Location loc = playerMoveEvent.getTo().clone();
			loc.setY(yConfTp);
			playerMoveEvent.getPlayer().teleport(loc);
			playerMoveEvent.getPlayer().sendMessage("Выше нельзя");
		}
	}
}
