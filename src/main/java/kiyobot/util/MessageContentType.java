package kiyobot.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MessageContentType {
    BASIC_COMMAND("!"),
    DEFAULT();
    
    private static final Map<String, MessageContentType> INSTANCE_BY_PREFIX = new HashMap<>();
    
    static {
        Arrays.asList(MessageContentType.values())
                .forEach(instance -> INSTANCE_BY_PREFIX.put(instance.getPrefix(), instance));
    }
    
    private final String prefix;
    
    MessageContentType() {
        this.prefix = "";
    }
    
    MessageContentType(String message) {
        this.prefix = String.valueOf(message.charAt(0));
    }
    
    /**
     * Gets the name of this event
     * @return event
     */
    public String getPrefix() {
        return this.prefix;
    }
    
    public static MessageContentType getByPrefix(String message) {
        return INSTANCE_BY_PREFIX.getOrDefault(String.valueOf(message.charAt(0)), DEFAULT);
    }
}
