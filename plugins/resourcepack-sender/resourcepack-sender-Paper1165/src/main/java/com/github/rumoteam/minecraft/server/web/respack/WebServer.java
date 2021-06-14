package com.github.rumoteam.minecraft.server.web.respack;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.rumoteam.minecraft.server.web.respack.header.WRPHeader;
import com.sun.net.httpserver.HttpServer;

import lombok.Getter;

public class WebServer extends Thread {

	@Getter
	private HttpServer server;

	public WebServer() throws IOException {
		int port = WRPHeader.getConfig().getPort();
		port = Utils.getFreePortByFirst(port);
		if (WRPHeader.getConfig().getPort() != port) {
			WRPHeader.getConfig().setPort(port);
			Utils.saveConfig();
		}
		this.server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/respack.zip", new FileHandler());
	}

	@Override
	public void interrupt() {
		server.stop(1);
		super.interrupt();
	}

	@Override
	public void run() {
		// port=

		server.start();
	}
}
