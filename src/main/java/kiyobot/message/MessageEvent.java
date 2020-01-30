package kiyobot.message;

import com.google.gson.*;
import kiyobot.util.BasicCommandType;
import kiyobot.util.Buzzword;
import kiyobot.util.MessageContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.event.message.MessageCreateEvent;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public enum MessageEvent {

	INSTANCE();
    
    private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson GSON = new Gson();
	private static final Gson GSON_PRETTY = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);
	
	private static final String SUGGESTION_LINK = "https://forms.gle/Y6pKqMAgYUS6eJJL7";
	private static final String DOC_LINK_VIEW_ONLY = "https://docs.google.com/document/d/1gmVzkkEiOadXF6ThIalBqzCuuyrGVs2ZGUzqcGeQZyE/edit?usp=sharing";
	private static final String CELTX_LINK = "https://www.celtx.com/a/ux/#documents";
	
	private static final String COMMAND_LIST;
    
    static {
        final StringBuilder builder = new StringBuilder();
        getBasicCommandList(builder);
        
        COMMAND_LIST = builder.toString();
    }
    
    private int PINGS;
    private ScheduledFuture pingExpiration;
	/**
	 * Adds message listener to the api, which allows the bot to listen to Discord messages
	 * @param api - Javacord API class
	 */
	public void listenOnMessage(DiscordApi api) {
		api.addMessageCreateListener(this::onMessage);
	}
    
    /**
     * Parse message and checks the content type to determine whether to respond to a command or not
     * @param messageEvent message event
     */
	private void onMessage(MessageCreateEvent messageEvent) {
        final String content = messageEvent.getMessageContent();
        final MessageContentType messageType = MessageContentType.getByPrefix(content);
        
        switch(messageType) {
            case BASIC_COMMAND:
                decodeBasicCommand(messageEvent, content);
                break;
            case DEFAULT:
                checkForBuzzword(messageEvent, content);
                break;
        }
    }
    
    private void checkForBuzzword(MessageCreateEvent messageEvent, String message) {
	    final Buzzword buzzword = Buzzword.getByFirstMatch(message);
	    switch(buzzword) {
            case AYYLMAO:
                doEncodeAyylmao(messageEvent);
                break;
            case OWO:
                doEncodeOwoBuzzword(messageEvent);
                break;
            case DEFAULT:
                break;
        }
    }
    
    private void doEncodeAyylmao(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("lmao");
    }
    
    private void doEncodeOwoBuzzword(MessageCreateEvent messageEvent) {
        messageEvent.getMessage().addReaction(":oworld:642815137153286144");
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
                doEncodeCommandsList(messageEvent);
                break;
            case CELTX:
                doEncodeCeltx(messageEvent);
                break;
            case DOC:
                doEncodeDocLink(messageEvent);
                break;
            case HEWWO:
                doEncodeHewwo(messageEvent);
                break;
            case PING:
                doEncodePing(messageEvent);
                break;
            case SUGGESTION:
                doEncodeSuggestion(messageEvent);
                break;
            case DEFAULT:
                doEncodeUnknownCommand(messageEvent);
                break;
        }
    }
    
    /**
     * Sends a list of the current commands to the channel
     * @param messageEvent;
     */
    private void doEncodeCommandsList(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(COMMAND_LIST);
    }
    
    private void doEncodeDocLink(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(String.format("**Viewable design document link**\n%s\n", DOC_LINK_VIEW_ONLY));
    }
    
    private void doEncodeCeltx(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(String.format("**Celtx story collab link**\n%s\n", CELTX_LINK));
    }
    
    /**
     * What is this?
     * @param messageEvent message event
     */
    private void doEncodeHewwo(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("OWO what's this?");
    }
    
    /**
     * Perform ping command
     * @param messageEvent message event
     */
    private void doEncodePing(MessageCreateEvent messageEvent) {
        schedulePingExpiration();
        if (PINGS > 4) {
            messageEvent.getChannel().sendMessage("https://i.imgur.com/gOJdCJS.gif");
        } else if (PINGS > 2) {
            messageEvent.getChannel().sendMessage("...");
        } else {
            messageEvent.getChannel().sendMessage("Pong!");
        }
        PINGS++;
    }
    
    private void doEncodeSuggestion(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(String.format("**Send some bot feature suggestions with this link**\n%s\n",
                SUGGESTION_LINK));
    }
    
    private void doEncodeUnknownCommand(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("Unknown command");
    }
    
    private static void getBasicCommandList(StringBuilder builder) {
        builder.append("**Basic Bot Commands**\n");
        builder.append("```");
        for (BasicCommandType commandType : BasicCommandType.values()) {
            builder.append(String.format("%s\n",commandType.getCommand()));
        }
        builder.append("```");
    }
    
    private void schedulePingExpiration() {
        if(pingExpiration != null) {
            pingExpiration.cancel(false);
        }
        pingExpiration = scheduler.schedule(() -> {
            PINGS = 0;
        }, 60, TimeUnit.SECONDS);
    }
}
