package kiyobot.message;

import com.google.gson.*;
import kiyobot.util.BasicCommandType;
import kiyobot.util.MessageContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public enum MessageEvent {

	INSTANCE();
    
    private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson GSON = new Gson();
	private static final Gson GSON_PRETTY = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	
	private static final String COMMAND_LIST;
    
    private int PINGS = 0;
    
	static {
        final StringBuilder builder = new StringBuilder();
        getBasicCommandList(builder);
        
        COMMAND_LIST = builder.toString();
    }

	/**
	 * Adds message listener to the api, which allows the bot to listen to Discord messages
	 * @param api - Diskiyord API class
	 */
	public void listenOnMessage(DiscordApi api) {
		api.addMessageCreateListener(this::onMessage);
	}
    
    /**
     * Parse message and checks the content type to determine whether to respond to a command or not
     * @param messageCreateEvent message event
     */
	private void onMessage(MessageCreateEvent messageCreateEvent) {
        final String content = messageCreateEvent.getMessageContent();
        final MessageContentType messageType = MessageContentType.getByPrefix(content);
        
        switch(messageType) {
            case BASIC_COMMAND:
                decodeBasicCommand(messageCreateEvent, content);
                break;
            case DEFAULT:
                break;
        }
    }
    
    /**
     * Parse basic command
     * @param messageEvent message event
     * @param message message
     */
    private void decodeBasicCommand(MessageCreateEvent messageEvent, String message) {
	    final BasicCommandType commandType = BasicCommandType.getByCommandMessage(message);
	    switch(commandType) {
            case COMMANDS:
            case HELP:
                encodeCommandsList(messageEvent);
                break;
            case HEWWO:
                encodeHewwo(messageEvent);
                break;
            case PING:
                encodePing(messageEvent);
                break;
            case DEFAULT:
                break;
        }
    }
    
    /**
     * Perform ping command
     * @param messageEvent message event
     */
    private void encodePing(MessageCreateEvent messageEvent) {
        if (PINGS > 4) {
            messageEvent.getChannel().sendMessage("https://i.imgur.com/gOJdCJS.gif");
        } else if (PINGS > 2) {
            messageEvent.getChannel().sendMessage("...");
        } else {
            messageEvent.getChannel().sendMessage("Pong!");
        }
        PINGS++;
    }
    
    private void encodeHewwo(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("OWO what's this?");
    }
    
    // Gets the message from the channel
//	final String message = messageEvent.getMessageContent();
//			LOGGER.info("Got message = {}", message);
//
//	onMessage(message);
// 	Gets the JDBCHandler singleton
//			JDBCHandler handler = JDBCEnum.INSTANCE.getJDBCHandler();
    // Check if message matches command regex
//			Matcher matcher;
    // Only check messages that start with !
//			if(message.startsWith("!")) {
//				if ((matcher = Pattern.compile(ADD_EXGFX_REGEX).matcher(message)).matches()) {
//					// !addexgfx
//					addExgfx(messageEvent, matcher);
//				} else if ((matcher = Pattern.compile(GET_EXGFX_REGEX).matcher(message)).matches()) {
//					// !getexgfx
//					getExGFXInfo(messageEvent, matcher, handler);
//				} else if (Pattern.compile(GET_ALL_EXGFX_REGEX).matcher(message).matches()) {
//					// !getallexgfx
//					getAllExGFX(messageEvent);
//				} else if (Pattern.compile("!ping").matcher(message).matches()) {
//					if (PINGS < 3) {
//						messageEvent.getChannel().sendMessage("Pong!");
//					} else if (PINGS >= 5) {
//						messageEvent.getChannel().sendMessage("https://i.imgur.com/gOJdCJS.gif");
//					} else {
//						messageEvent.getChannel().sendMessage("...");
//					}
//					PINGS++;
//				} else if (Pattern.compile("!hewwo").matcher(message).matches()) {
//					messageEvent.getChannel().sendMessage("*notices command* OwO what's this?");
//				} else if (Pattern.compile("!commands").matcher(message).matches()) {
//					getCommands(messageEvent);
//				} else {
//					messageEvent.getChannel().sendMessage(MessageArgumentError.UNKNOWN_COMMAND.getErrorMsg());
//				}
//			}

	/**
	 * Sends unkown command message to the channel
	 * @param messageEvent;
	 */
	private void encodeCommandsList(MessageCreateEvent messageEvent) {
	    final String commands =
            "**General Bot Commands**\n------------------------\n" +
            "!commands\n\t- Gets a current list of commands.\n" +
            "!ping\n\t- A generic ping message. Please don't overuse.\n" +
            "!hewwo\n\t- What's this?\n";
		messageEvent.getChannel().sendMessage(commands);
	}
	
	private static void getBasicCommandList(StringBuilder builder) {
        builder.append("**Basic Bot Commands**\n------------------------\n");
        for (BasicCommandType commandType : BasicCommandType.values()) {
            builder.append(String.format("%s\n",commandType.getCommand()));
        }
    }
}
