package com.github.rumoteam.minecraft.plugins.bordercontrol.header;

import java.io.File;

import com.github.rumoteam.minecraft.plugins.bordercontrol.init.BCConfig;

import lombok.Getter;
import lombok.Setter;

public final class BCHeader {
	private BCHeader() {
	}

	private static @Getter File rootDir = new File(new File("files"), "bordercontrol");
	private static @Getter File configFile = new File(getRootDir(), "config.yml");
	private static @Getter @Setter BCConfig config = new BCConfig();
}
