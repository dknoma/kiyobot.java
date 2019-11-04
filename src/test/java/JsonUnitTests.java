import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kiyobot.util.JsonPacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;

public class JsonUnitTests {

	private static final Logger LOGGER = LogManager.getLogger();

	@Test
	public void testJsonPacketPutsToString() {
		String jsonHardcode = "{\"b\":true,\"s\":\"string stuff\",\"data\":{\"f\":false}, \"i\":10}";
		System.out.printf("original:        %s\n", new JsonPacket(jsonHardcode).toString());
		LOGGER.info("jsonHardcode: {}", jsonHardcode);

		final JsonPacket data = JsonPacket.newBuilder()
				.put("f", false)
				.build();
		final JsonPacket jsonPacket = JsonPacket.newBuilder()
				.put("b", true)
				.put("s", "string stuff")
				.put("i", 10)
				.put("data", data)
				.build();

		String packetString = jsonPacket.toString();
		LOGGER.info("packet:          {}", packetString);
		System.out.printf("packet:          %s\n", packetString);

		final JsonParser parser = new JsonParser();
		final JsonElement hardcoded = parser.parse(jsonHardcode);
		final JsonElement built = parser.parse(packetString);

		assertEquals(String.format("hardcoded=%s vs. built=%s", hardcoded.toString(), built.toString()), hardcoded, built);
	}
}

