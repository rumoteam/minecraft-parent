package com.github.rumoteam.minecraft.plugin.altauth.libs.request;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
	private static final long serialVersionUID = -2522816489136711736L;

	public String userName;

	public byte[] key;
}
