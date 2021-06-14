package com.github.rumoteam.minecraft.server.web.respack;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

public class Files {
	public static void main(String[] args) {
		if (args.length == 3) {
			String input = args[1];
			String key = args[2];

			if (args[0].equals("D")) {
				String decData = decryptXBin(input, key);
				System.err.println(decData);
			}
			if (args[0].equals("E")) {
				String encData = encryptXBin(input, key);
				System.err.println(encData);
			}
		}

	}

	public static String xorString(String string, String keyI) {
		char[] stringAr = string.toCharArray();
		char[] keyAr = keyI.toCharArray();
		for (int i = 0; i < string.length(); i++) {
			stringAr[i] = (char) (stringAr[i] ^ keyAr[i % keyI.length()]);
		}
		return new String(stringAr);
	}

	public static String decryptXBin(String encData, String realKey) {
		return xorString(hex2bin(encData), realKey);
	}

	public static String encryptXBin(String cmd, String realKey) {
		return bin2hex(xorString(cmd, realKey));
	}

	private static String bin2hex(String enc2) {
		return DatatypeConverter.printHexBinary(enc2.getBytes(StandardCharsets.UTF_8));
	}

	private static String hex2bin(String enc2) {
		return new String(DatatypeConverter.parseHexBinary(enc2), StandardCharsets.UTF_8);
	}
}
