package com.github.rumoteam.minecraft.plugin.altauth.client.config;

import com.github.rumoteam.minecraft.plugin.altauth.client.object.ServerInfo;

import lombok.Getter;
import lombok.Setter;

public class Config {
	@Getter
	public byte[] privateKey = new byte[512];
	private @Getter ServerInfo serverInfo = new ServerInfo();
	private @Getter @Setter String userName = "userName";

}
