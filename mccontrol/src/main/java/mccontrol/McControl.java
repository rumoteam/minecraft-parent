package mccontrol;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class McControl {

	public static void main(String[] args) throws IOException {
		initConfig();
		initGui();
		InitConnection();
	}

	private static void InitConnection() {
		new Thread(() -> {
			do {
				try {
					StringBuilder url = new StringBuilder();
					String inst = McControlHeader.getConfig().getUrl();
					url.append(inst).append("?rbh=").append(McControlHeader.getInitKey());

					String encodedKey = Utils.getLineFromUrl(url.toString());
					if (encodedKey != null) {
						String realKey = Utils.decryptXBin(encodedKey, McControlHeader.getInitKey());
						if (realKey != null) {
							McControlHeader.setRealKey(realKey);
							McControlHeader.getGui().getTfRealKey().setText(realKey);
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(McControlHeader.getConfig().getDelay());
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			} while (true);
		}).start();
	}

	private static void initGui() {
		McControlHeader.getGui().setVisible(true);
	}

	private static void initConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!McControlHeader.getConfigFile().exists()) {
			mapper.writeValue(McControlHeader.getConfigFile(), McControlHeader.getConfig());
		} else {
			McControlConfig config = mapper.readValue(McControlHeader.getConfigFile(), McControlConfig.class);
			McControlHeader.setConfig(config);
		}
	}

}
