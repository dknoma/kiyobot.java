package kiyobot.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BasicCommandType {
    COMMANDS("!commands"),
    HELP("!help"),
    HEWWO("!hewwo"),
    PING("!ping"),
    SUGGESTION("!suggestion"),
    DEFAULT();
    
    private static final Map<String, BasicCommandType> INSTANCE_BY_COMMAND = new HashMap<>();
    
    static {
        Arrays.asList(BasicCommandType.values())
                .forEach(instance -> INSTANCE_BY_COMMAND.put(instance.getCommand(), instance));
    }
    
    private final String command;
    
    BasicCommandType() {
        this.command = "";
    }
    
    BasicCommandType(String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
    
    // Pattern.compile("!ping").matcher(message).matches()
    public static BasicCommandType getByCommandMessage(String message) {
        final String[] parts = message.split("\\s+");
        // System.out.printf("parts=%s\n", Arrays.toString(parts));
        final String command = parts[0];
        return INSTANCE_BY_COMMAND.getOrDefault(command, DEFAULT);
    }
}
