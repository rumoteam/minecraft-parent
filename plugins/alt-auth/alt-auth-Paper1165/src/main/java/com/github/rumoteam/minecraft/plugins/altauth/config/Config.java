package com.github.rumoteam.minecraft.plugins.altauth.config;

import lombok.Getter;
import lombok.Setter;

public class Config {
	private @Getter @Setter int port = 50400;
	private @Getter String pattern = "authme forcelogin Alt-Auth-User";
}
