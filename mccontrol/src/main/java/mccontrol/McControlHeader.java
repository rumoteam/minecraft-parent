package mccontrol;

import java.io.File;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public final class McControlHeader {
	private static @Getter File configFile = new File("McControlConfig.yaml");
	private static @Getter @Setter McControlConfig config = new McControlConfig();

	private static @Getter String initKey = UUID.randomUUID().toString();
	private static @Getter @Setter String realKey;

	private static @Getter McControlGui gui = new McControlGui();
}
