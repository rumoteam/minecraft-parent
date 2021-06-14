package com.github.rumoteam.minecraft.plugin.altauth.libs;

import java.io.Serializable;

public class CheckPacket implements Serializable {
	private static final long serialVersionUID = -7773462329827928734L;

	public String username;

	public CheckPacket(String userName) {
		this.username = userName;
	}

}
