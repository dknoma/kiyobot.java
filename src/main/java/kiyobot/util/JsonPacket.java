package kiyobot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A convenience class for creating JSON formatted strings for output
 *
 * Format:
 * 	{
 * 	    "key": value,
 * 	    "anotherkey": {
 * 	        "JsonPackets": "can",
 * 	        "also": "be",
 * 	        "values": true
 * 	    }
 * 	}
 */
public class JsonPacket {
	private static final Gson gson = new GsonBuilder().create();
	private static final Logger LOGGER = LogManager.getLogger();

	private final Map<String, Builder.ValueToMap> valueTypes;
	
	private JsonObject object;

	public JsonPacket() {
		this.valueTypes = new LinkedHashMap<>();
		this.object = new JsonObject();
	}

	public JsonPacket(String json) {
		this.valueTypes = new LinkedHashMap<>();
		this.object = new JsonObject();
		mapJson(json);
	}
	
	private JsonPacket(Map<String, Builder.ValueToMap> valueTypes) {
		this.valueTypes = valueTypes;
		this.object = new JsonObject();
	}

	// public JsonPacket(JsonObject json) {
	// 	this.valueTypes = new LinkedHashMap<>();
	// 	this.object = json;
	// }

	/**
	 * Returns a new Builder instance.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public Builder.ValueToMap get(String key) {
		return valueTypes.get(key);
	}
	
	/**
	 * Turns the given string into a JsonObject, and builds the mapping for the key/value pairs
	 * @param string -
	 */
	private void mapJson(String string) {
		 this.object = gson.fromJson(string, JsonObject.class);
	}

	@Override
	public String toString() {
		return this.object.toString();
	}

	public static final class Builder {
		private final Map<String, ValueToMap> keyValuePairs;

		private final JsonObject object_;

		private Builder() {
			this.object_ = new JsonObject();
			this.keyValuePairs = new LinkedHashMap<>();
		}

		public <T> Builder put(String key, T value) {
			this.keyValuePairs.put(key, new ValueToMap<>(value));
			return this;
		}

		public JsonPacket build() {
			keyValuePairs.forEach((k, v) -> {
				switch(v.classType) {
					case BOOLEAN:
						object_.addProperty(k, (boolean) v.value);
						break;
					case BYTE:
						object_.addProperty(k, (byte) v.value);
						break;
					case CHAR:
						object_.addProperty(k, (char) v.value);
						break;
					case DOUBLE:
						object_.addProperty(k, (double) v.value);
						break;
					case FLOAT:
						object_.addProperty(k, (float) v.value);
						break;
					case INT:
						object_.addProperty(k, (int) v.value);
						break;
					case LONG:
						object_.addProperty(k, (long) v.value);
						break;
					case SHORT:
						object_.addProperty(k, (short) v.value);
						break;
					case STRING:
						object_.addProperty(k, (String) v.value);
						break;
					case JSON_OBJECT:
						object_.add(k, (JsonObject) v.value);
						break;
					case JSON_PACKET:
						object_.add(k, ((JsonPacket) v.value).object);
						break;
					case BAD_TYPE:
					default:
						System.out.println("Unrecognized Class type. Please make sure you're adding a valid Object to the Json");
						LOGGER.error("Unrecognized Class type for value[{}, class={}]. Please make sure you're adding a valid Object to the Json",
								v.value, v.value.getClass());
						break;
				}
			});
			
			final JsonPacket packet = new JsonPacket(keyValuePairs);
			packet.object = object_;
			return packet;
		}

		public static final class ValueToMap<T> {
			private final T value;
			private final ClassType classType;

			private ValueToMap(T value) {
				this.value = value;
				this.classType = ClassType.getByClass(value.getClass());
			}
			
			public T getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return String.format("VALUE=[%s, %s]", value, classType);
			}
		}

	}
}
