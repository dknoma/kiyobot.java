package kiyobot.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BasicCommandType {
    COMMANDS("!commands", "List of useful commands."),
    CELTX("!celtx", "Provides a link to the celtx docs."),
    DOC("!doc", "Provides a link to the project docs."),
    GITHUB("!github", "Provides a link to the Github repository for the project."),
    HELP("!help", "List of useful commands."),
    HEWWO("!hewwo", "What's dis?"),
    PING("!ping", "Pong!"),
    SUGGESTION("!suggestion", "Provides a link to a Google Forms to create suggestions for me!"),
    REMIND_ME("!remindme", "Gives me a message to remind and ping you after a certain amount of time.\n + Format: !remindme <number> <unit: valid units=[s, m, h, d]> <message>"),
    DEFAULT();
    
    private static final Map<String, BasicCommandType> INSTANCE_BY_COMMAND = new HashMap<>();
    
    static {
        Arrays.asList(BasicCommandType.values())
              .forEach(instance -> INSTANCE_BY_COMMAND.put(instance.getCommand(), instance));
    }
    
    private final String command;
    private final String description;

    BasicCommandType() {
        this.command = "";
        this.description = "";
    }
    
    BasicCommandType(String command) {
        this.command = command;
        this.description = "TODO";
    }

    BasicCommandType(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
    
    // Pattern.compile("!ping").matcher(message).matches()
    public static BasicCommandType getByCommandMessage(String message) {
        final String[] parts = message.split("\\s+");
        // System.out.printf("parts=%s\n", Arrays.toString(parts));
        final String command = parts[0];
        return INSTANCE_BY_COMMAND.getOrDefault(command, DEFAULT);
    }
}
