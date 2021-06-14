package com.github.rumoteam.minecraft.plugins.bordercontrol.init;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.plugins.bordercontrol.header.BCHeader;
import com.github.rumoteam.minecraft.plugins.bordercontrol.listeners.MoveListener;

public class Plugin extends JavaPlugin {
	@Override
	public void onEnable() {
		try {
			initConfig();
			Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		if (!BCHeader.getConfigFile().exists()) {
			BCHeader.getConfigFile().getParentFile().mkdirs();
			BCHeader.getConfigFile().createNewFile();
			mapper.writeValue(BCHeader.getConfigFile(), BCHeader.getConfig());
		} else {
			BCConfig config = mapper.readValue(BCHeader.getConfigFile(), BCConfig.class);
			BCHeader.setConfig(config);
		}
	}
}
