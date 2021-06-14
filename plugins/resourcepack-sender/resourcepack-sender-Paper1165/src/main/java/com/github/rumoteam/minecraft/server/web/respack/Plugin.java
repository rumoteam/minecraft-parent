package com.github.rumoteam.minecraft.server.web.respack;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.server.web.respack.config.WebConfig;
import com.github.rumoteam.minecraft.server.web.respack.header.WRPHeader;

public class Plugin extends JavaPlugin {

	@Override
	public void onEnable() {
		WRPHeader.getWebServer().start();
	}

	@Override
	public void onLoad() {
		try {
			WRPHeader.plugin = this;
			// read config
			createFS();
			readConfigForWeb();
			reloadKeys();

			// create server
			WRPHeader.setWebServer(new WebServer());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reloadKeys() {
		WRPHeader.key = UUID.randomUUID().toString();
	}

	private void createFS() throws IOException {
		WRPHeader.getPluginDir().mkdirs();
		if (!WRPHeader.getRespackFile().exists()) {
			WRPHeader.getRespackFile().createNewFile();
		}
	}

	private void readConfigForWeb() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		if (!WRPHeader.getConfigFile().exists()) {
			WRPHeader.getConfigFile().getParentFile().mkdirs();
			WRPHeader.getConfigFile().createNewFile();
			mapper.writeValue(WRPHeader.getConfigFile(), WRPHeader.getConfig());
		} else {
			WebConfig config = mapper.readValue(WRPHeader.getConfigFile(), WebConfig.class);
			WRPHeader.setConfig(config);
		}
	}

	@Override
	public void onDisable() {
		WRPHeader.getWebServer().interrupt();
		super.onDisable();
	}

}
