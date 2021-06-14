package com.github.rumoteam.minecraft.plugins.altauth.init;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacket;
import com.github.rumoteam.minecraft.plugins.altauth.config.Config;
import com.github.rumoteam.minecraft.plugins.altauth.utils.ClientUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

public class Plugin extends JavaPlugin implements @NotNull Listener {

	public static Plugin plugin;

	@Override
	public void onEnable() {
		plugin = this;
		try {
			initConfig();
			initNetwork();
			Bukkit.getPluginManager().registerEvents(this, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File dataDir = new File("files", "alt-auth");

	public static Config config = new Config();

	private static File configFile = new File(dataDir, "config.yml");
	@Getter
	private static File playersDir = new File(dataDir, "players");
	@Getter
	private static File playersAllowDir = new File(dataDir, "playersAllow");

	private static void initConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!configFile.exists()) {
			if (configFile.getParentFile().mkdirs()) {
			}
			mapper.writeValue(configFile, config);
		} else {
			config = mapper.readValue(configFile, Config.class);
		}

	}

	ServerBootstrap serverBootstrap = new ServerBootstrap();

	EventLoopGroup serverWorkgroup = new NioEventLoopGroup();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {

		if (sender instanceof Player) {
			if (args.length != 1) {
				return false;
			}
			Player player = (Player) sender;
			player.sendMessage(Arrays.toString(args));
			@NotNull
			String realName = player.getName();
			File allowsDir = Plugin.playersAllowDir;
			File confirmFile = new File(allowsDir, realName);

			boolean cmd = Boolean.parseBoolean(args[0]);
			try {
				if (cmd) {
					allowsDir.mkdirs();
					if (!confirmFile.exists()) {
						confirmFile.createNewFile();
					}
					int time = (int) (System.currentTimeMillis() / 1000);
					String data = Integer.toString(time);
					Files.write(confirmFile.toPath(), data.getBytes(StandardCharsets.UTF_8),
							StandardOpenOption.TRUNCATE_EXISTING);
					player.sendMessage("ok:allow");
				} else {
					confirmFile.delete();
					ClientUtils.playerDataFile(realName).delete();
					player.sendMessage("ok:deleted");
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void initNetwork() {
		serverBootstrap.group(serverWorkgroup).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(config.getPort()));
		serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ObjectDecoder(
						ClassResolvers.softCachingConcurrentResolver(CheckPacket.class.getClassLoader())));
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(new ClientHandler());
			}
		});
		serverBootstrap.bind();
	}

	@Override
	public void onDisable() {
		// TODO STOP NETWORK
	}
}
