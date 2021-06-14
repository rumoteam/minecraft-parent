package com.github.rumoteam.minecraft.savevillagers.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UpdateThread extends Thread {

	private String getDataFromUrl(URL url) throws IOException {
		URLConnection yc = url.openConnection();
		StringBuilder outputBuilder = new StringBuilder();
		String inputLine;
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(yc.getInputStream(), StandardCharsets.UTF_8))) {
			while ((inputLine = in.readLine()) != null) {
				outputBuilder.append(inputLine).append('\n');
			}
		}
		return outputBuilder.toString();
	}

	public void sTop() {
		System.err.println(3);
		stop = true;
		System.err.println(31);
	}

	String urlStringVersion = "https://raw.githubusercontent.com/rumoteam/rumo-versions-raw/main/minecraft/plugins/saveVillagers/version.txt";
	String urlUpdatersListString = "https://raw.githubusercontent.com/rumoteam/rumo-versions-raw/main/updaters/services/list.txt";

	int versionPlugin = 0;
	int versionPluginNew = 0;

	boolean stop;
	public boolean updateNeeded;

	private boolean proxyNeeded;

	public static void main(String[] args) {
		UpdateThread updateThread = new UpdateThread();
		updateThread.proxyNeeded = true;
		updateThread.start();
	}

	@Override
	public void run() {
		int sleepDelay = 1 * 60 * 1000;
		while (!stop) {
			try {
				URL url = new URL(urlStringVersion);

				String data = getDataFromUrl(url);
				data = data.replaceAll("\n", "");
				versionPluginNew = Integer.parseInt(data);
				if (versionPlugin < versionPluginNew) {
					updateNeeded = true;
				}

				URL servicesUrl = new URL(urlUpdatersListString);
				String servicesRawString = getDataFromUrl(servicesUrl);

				ArrayList<String> hosts = new ArrayList<String>();
				String[] array = servicesRawString.split("\n");
				for (String string : array) {
					if (proxyNeeded) {
						string = string + ".dog";
					}
					System.err.println("test:" + string);

				}

			} catch (UnknownHostException e1) {
				// IGNORE
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(sleepDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}