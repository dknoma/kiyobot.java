package kiyobot.message;

import static db.mongo.documents.DocumentConstants.UserReminderDocument.CHANNEL_ID_KEY;
import static db.mongo.documents.DocumentConstants.UserReminderDocument.DISPLAY_NAME_KEY;
import static db.mongo.documents.DocumentConstants.UserReminderDocument.REMINDER_MESSAGE_KEY;
import static db.mongo.documents.DocumentConstants.UserReminderDocument.TIME_KEY;
import static db.mongo.documents.DocumentConstants.UserReminderDocument.TIME_UNIT_KEY;
import com.google.gson.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import db.mongo.documents.DocumentConstants;
import db.mongo.settings.KiyoMongoSettings;
import db.mongo.settings.MongoCollectionType;
import kiyobot.reminders.ReminderTimeUnit;
import kiyobot.util.BasicCommandType;
import kiyobot.util.Buzzword;
import kiyobot.util.MessageContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.CertainMessageEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import java.sql.Time;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MessageEvent {

	INSTANCE();
    
    private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson GSON = new Gson();
	private static final Gson GSON_PRETTY = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);
	
	private static final Pattern REMINDER_REGEX = Pattern.compile("!reminder ([0-9])+ (s|m|h|d|w|mon) ?(.*)");
	private static final Matcher REMINDER_MATCHER = REMINDER_REGEX.matcher("").reset();
	
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
    
    private MongoDatabase db;
    
    MessageEvent() {
    }
    
	/**
	 * Adds message listener to the api, which allows the bot to listen to Discord messages
	 * @param api - Javacord API class
	 */
	public void listenOnMessage(DiscordApi api, KiyoMongoSettings settings) {
        this.db = settings.getDatabase();
		api.addMessageCreateListener(this::onMessage);
		api.addServerJoinListener(this::getReminders);
	}
 
	private void getReminders(ServerJoinEvent joinEvent) {
        final MongoCollection<Document> collection =
                db.getCollection(MongoCollectionType.USER_REMINDERS.collectionName());
        
        final FindIterable<Document> documents = collection.find();
        final MongoCursor<Document> cursor = documents.cursor();
        
        while(cursor.hasNext()) {
            final Document doc = cursor.next();
            final String displayName = doc.getString(DISPLAY_NAME_KEY);
            final long channelId = doc.getLong(CHANNEL_ID_KEY);
            final long time = doc.getLong(TIME_KEY);
            final TimeUnit unit = doc.get(TIME_UNIT_KEY, TimeUnit.class);
            final String reminderMessage = doc.getString(REMINDER_MESSAGE_KEY);
    
            final Optional<ServerChannel> channelById = joinEvent.getServer().getChannelById(channelId);
    
            final long currentMillis = System.currentTimeMillis();
            
            final long convert;
            switch(unit) {
                case SECONDS:
                    convert = currentMillis / 1000 - time;
                    break;
                case MINUTES:
                    convert = currentMillis / 1000 / 60 - time;
                    break;
                case HOURS:
                    convert = currentMillis / 1000 / 60 / 60 - time;
                    break;
                case DAYS:
                    convert = currentMillis / 1000 / 60 / 60 / 24 - time;
                    break;
                default:
                    convert = 0;
                    break;
            }
    
            scheduleReminder(channelById.flatMap(Channel::asTextChannel).get(), displayName, convert > 0 ? convert : 0, unit, reminderMessage);
        }
        
        // scheduleReminder(messageEvent, displayName, time, timeUnit, displayName);
        //
        // final MongoCollection<Document> collection =
        //         db.getCollection(MongoCollectionType.USER_REMINDERS.collectionName());
        // Document doc = new Document();
        // doc.put("displayName", displayName);
        // doc.put("time", time);
        // doc.put("unit", timeUnit);
        // doc.put("reminderMessage", reminderMessage);
        // collection.insertOne(doc);
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
            case REMINDER:
                doEncodeReminder(messageEvent);
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
    
    /**
     * !remindme 1 h
     * Reminds user in 1 hour
     * @param messageEvent
     */
    private void doEncodeReminder(MessageCreateEvent messageEvent) {
        final TextChannel channel = messageEvent.getChannel();
        final Message message = messageEvent.getMessage();
        final String text = message.getContent();
        REMINDER_MATCHER.reset(text);
        
        if(REMINDER_MATCHER.matches()) {
            final String unit = REMINDER_MATCHER.group(2);
            final String reminderMessage = REMINDER_MATCHER.group(3);
            try {
                final MessageAuthor author = message.getAuthor();
                final String displayName = author.getDisplayName();

                long time = Long.parseLong(REMINDER_MATCHER.group(1));
                final ReminderTimeUnit timeUnit = ReminderTimeUnit.getUnit(unit);
                
                final TimeUnit targetUnit;
                switch (timeUnit) {
                    case SECOND:
                        // final long endDate = System.currentTimeMillis() + time;
                        targetUnit = TimeUnit.SECONDS;
                        break;
                    case MINUTE:
                        targetUnit = TimeUnit.MINUTES;
                        break;
                    case HOUR:
                        targetUnit = TimeUnit.HOURS;
                        break;
                    case DAY:
                        targetUnit = TimeUnit.DAYS;
                        break;
                    case WEEK:
                        targetUnit = TimeUnit.DAYS;
                        time *= 7;
                        break;
                    default:
                        targetUnit = TimeUnit.MILLISECONDS;
                        break;
                }
                scheduleReminder(channel, displayName, time, targetUnit, displayName);
    
                final MongoCollection<Document> collection =
                        db.getCollection(MongoCollectionType.USER_REMINDERS.collectionName());
                Document doc = new Document();
                doc.put(DISPLAY_NAME_KEY, displayName);
                doc.put(CHANNEL_ID_KEY, channel.getId());
                doc.put(TIME_KEY, time);
                doc.put(TIME_UNIT_KEY, timeUnit);
                doc.put(REMINDER_MESSAGE_KEY, reminderMessage);
                collection.insertOne(doc);
            } catch(NumberFormatException nfe) {
                LOGGER.error(nfe.getMessage());
            }
        }
    }
    
    private static void scheduleReminder(TextChannel channel, String displayName, long time, TimeUnit timeUnit, String reminderMessage) {
        final ScheduledFuture<?> future = scheduler.schedule(() -> {
            channel.sendMessage(String.format("@%s %s", displayName, reminderMessage));
        }, time, timeUnit);
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
