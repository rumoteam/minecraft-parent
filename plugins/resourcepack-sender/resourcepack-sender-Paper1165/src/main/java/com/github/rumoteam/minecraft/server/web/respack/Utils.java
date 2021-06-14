package com.github.rumoteam.minecraft.server.web.respack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.server.web.respack.header.WRPHeader;

public class Utils {
	static Logger logger = LoggerFactory.getLogger("NetUtils");

	public static int getFreePortByFirst(int port) {
		String host = "127.0.0.1";
		if (!checkPort(host, port)) {
			return port;
		}

		while (true) {
			port = getIntFromRange(1025, 65535);
			if (!checkPort(host, port)) {
				return port;
			}
		}
	}

	private static int getIntFromRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	private static boolean checkPort(String host, int port) {
		logger.info("check port {} for host {}", port, host);
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			socket.close();
			return true;
		} catch (Exception e) {
			// IGNORE
		}
		return false;
	}

	public static void saveConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		if (!WRPHeader.getConfigFile().exists()) {
			WRPHeader.getConfigFile().getParentFile().mkdirs();
			WRPHeader.getConfigFile().createNewFile();
		}
		mapper.writeValue(WRPHeader.getConfigFile(), WRPHeader.getConfig());
	}

}
