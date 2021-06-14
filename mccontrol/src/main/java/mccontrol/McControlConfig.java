package mccontrol;

import lombok.Getter;
import lombok.Setter;

public class McControlConfig {
	private @Getter @Setter String url = "http://127.0.0.1:8080/respack.zip";
	private @Getter @Setter int delay = 4000;

}
