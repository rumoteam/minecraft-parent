package com.github.rumoteam.minecraft.plugins.altauth.init;

import java.io.File;
import java.net.SocketAddress;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacket;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacketAnswer;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.AuthRequest;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.RegisterAnsw;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.RegisterRequest;
import com.github.rumoteam.minecraft.plugins.altauth.config.PlayerData;
import com.github.rumoteam.minecraft.plugins.altauth.utils.ClientUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//SERVER SIDE
public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
		cause.printStackTrace();
		channelHandlerContext.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof CheckPacket) {
			CheckPacket checkPacket = (CheckPacket) msg;
			String userName = checkPacket.username;

			boolean registered = ClientUtils.playerRegistered(userName);
			if (!registered) {
				registered = ClientUtils.playerRegisteredByFile(userName);
			}
			CheckPacketAnswer checkPacketAnswer = new CheckPacketAnswer();
			checkPacketAnswer.answer = registered;
			ctx.writeAndFlush(checkPacketAnswer);
			return;
		}

		if (msg instanceof RegisterRequest) {
			SocketAddress ip = ctx.channel().remoteAddress();
			System.err.println("RegisterRequest from " + ip);
			RegisterAnsw registerAnsw = new RegisterAnsw();
			try {
				RegisterRequest request = (RegisterRequest) msg;

				String userName = request.userName;

				boolean playerExist = ClientUtils.playerRegistered(userName);
				@NotNull
				String realName = ClientUtils.getPlayer(userName).getName();
				File configmFile = new File(Plugin.getPlayersAllowDir(), realName);
				if (!configmFile.exists()) {
					return;
				}

				if (playerExist) {
					registerAnsw.registered = false;
					ctx.channel().writeAndFlush(registerAnsw);
					return;
				}
				File playerData = ClientUtils.playerDataFile(userName);
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

				PlayerData data = new PlayerData();
				data.user = ClientUtils.getPlayer(userName).getName();
				data.key = request.key;

				if (!playerData.getParentFile().exists()) {
					playerData.getParentFile().mkdirs();
				}
				mapper.writeValue(playerData, data);
				System.err.println("player " + data.user + " registered from ip:" + ip);

				registerAnsw.registered = true;
			} catch (Exception e) {
				registerAnsw.registered = false;
			}
			ctx.channel().writeAndFlush(registerAnsw);
			return;
		}
		try {

			if (msg instanceof AuthRequest) {
				SocketAddress ip = ctx.channel().remoteAddress();
				System.err.println("AuthRequest from " + ip);

				AuthRequest authRequest = (AuthRequest) msg;

				String requestUserName = authRequest.user;
				byte[] key = authRequest.key;

				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				File playerData = ClientUtils.playerDataFile(requestUserName);
				PlayerData readedData = mapper.readValue(playerData, PlayerData.class);

				if (Arrays.equals(key, readedData.key)) {
					@NotNull
					final String cmdF = Plugin.config.getPattern().replaceAll("Alt-Auth-User", playerData.getName());
					Runnable task = () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmdF);
					Bukkit.getScheduler().runTaskLater(Plugin.plugin, task, 0L);
					return;
				}

			}
		} catch (Exception e) {
		}

		System.out.println(msg.getClass());
	}

}
