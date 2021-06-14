package mccontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static String getLineFromUrl(String string) throws IOException {
		URL oracle = new URL(string);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			return inputLine;
		}
		in.close();
		return null;
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
