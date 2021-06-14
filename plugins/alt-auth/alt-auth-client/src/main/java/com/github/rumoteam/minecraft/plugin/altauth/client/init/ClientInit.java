package com.github.rumoteam.minecraft.plugin.altauth.client.init;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.plugin.altauth.client.config.Config;
import com.github.rumoteam.minecraft.plugin.altauth.client.gui.Gui;
import com.github.rumoteam.minecraft.plugin.altauth.client.handler.network.ClientHandler;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientInit {
	private static File configFile = new File("alt-auth", "config.yml");
	public static Config config = new Config();
	public static Gui gui = new Gui();

	private static Random random = new Random();

	static EventLoopGroup group = new NioEventLoopGroup();
	public static Bootstrap clientBootstrap = new Bootstrap();
	public static ChannelFuture channelFuture;

	public static void main(String[] args) {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					if (channelFuture != null) {
						channelFuture.awaitUninterruptibly().sync();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
			initConfig();
			initGui();

			initNetwork();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private static void initNetwork() {
		clientBootstrap.group(group);
		clientBootstrap.channel(NioSocketChannel.class);
		clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(new ObjectDecoder(
						ClassResolvers.softCachingConcurrentResolver(CheckPacket.class.getClassLoader())));

				ch.pipeline().addLast(new ClientHandler());
			}
		});
	}

	private static void initConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			initConfigData();
			mapper.writeValue(configFile, config);
		} else {
			config = mapper.readValue(configFile, Config.class);
		}
	}

	public static void saveConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		configFile.getParentFile().mkdirs();
		mapper.writeValue(configFile, config);
	}

	private static void initConfigData() {
		random.nextBytes(config.privateKey);
	}

	private static void initGui() {
		gui.setVisible(true);
		gui.updateFields();
	}

}
