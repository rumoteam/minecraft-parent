package com.github.rumoteam.minecraft.server.web.respack.header;

import java.io.File;

import com.github.rumoteam.minecraft.server.web.respack.Plugin;
import com.github.rumoteam.minecraft.server.web.respack.WebServer;
import com.github.rumoteam.minecraft.server.web.respack.config.WebConfig;

import lombok.Getter;
import lombok.Setter;

public final class WRPHeader {
	@Getter
	@Setter
	private static WebServer webServer;

	@Getter
	@Setter
	private static File pluginDir = new File("files");
	@Getter
	@Setter
	private static File configFile = new File(pluginDir, "web-respack-config.yaml");

	@Getter
	@Setter
	private static File respackFile = new File(pluginDir, "respack.zip");
	@Getter
	@Setter
	private static WebConfig config = new WebConfig();

	public static String key;

	public static Plugin plugin;

	private WRPHeader() {
	}
}
