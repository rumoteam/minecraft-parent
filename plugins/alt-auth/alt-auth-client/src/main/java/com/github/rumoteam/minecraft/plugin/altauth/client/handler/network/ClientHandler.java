package com.github.rumoteam.minecraft.plugin.altauth.client.handler.network;

import com.github.rumoteam.minecraft.plugin.altauth.client.gui.Gui;
import com.github.rumoteam.minecraft.plugin.altauth.client.gui.Gui.statusProfile;
import com.github.rumoteam.minecraft.plugin.altauth.client.init.ClientInit;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacket;
import com.github.rumoteam.minecraft.plugin.altauth.libs.CheckPacketAnswer;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.RegisterAnsw;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//CLIENT SIDE
public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
		cause.printStackTrace();
		channelHandlerContext.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(new CheckPacket(ClientInit.config.getUserName()));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof CheckPacketAnswer || msg instanceof RegisterAnsw) {
			boolean registered = false;
			statusProfile status;

			if (msg instanceof RegisterAnsw) {
				RegisterAnsw registerAnsw = (RegisterAnsw) msg;
				registered = registerAnsw.registered;
			}
			if (msg instanceof CheckPacketAnswer) {
				CheckPacketAnswer checkPacketAnswer = (CheckPacketAnswer) msg;
				registered = checkPacketAnswer.answer;
			}

			if (registered) {
				status = Gui.statusProfile.AUTH;
			} else {
				status = Gui.statusProfile.NOTREGISTERED;
			}

			ClientInit.gui.btnAuth.setEnabled(true);
			ClientInit.gui.btnAuth.setText(status.toString());
			return;
		}
	}
}
