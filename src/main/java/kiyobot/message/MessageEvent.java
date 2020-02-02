package kiyobot.message;

import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.CHANNEL_ID_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.AUTHOR_ID_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.REMINDER_MESSAGE_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TARGET_TIME_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TIME_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TIME_UNIT_KEY;
import static kiyobot.util.reminders.ReminderSuffixType.CHAR;
import static kiyobot.util.reminders.ReminderSuffixType.FULL;
import static kiyobot.util.reminders.ReminderSuffixType.NULL;
import static kiyobot.util.reminders.ReminderSuffixType.PARTIAL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import db.mongo.documents.UserReminderDocument;
import db.mongo.settings.KiyoMongoSettings;
import db.mongo.settings.MongoCollectionType;
import kiyobot.util.emotes.NewEmoteMessageType;
import kiyobot.util.reminders.ReminderSuffixType;
import kiyobot.util.reminders.ReminderTimeUnit;
import kiyobot.util.BasicCommandType;
import kiyobot.util.Buzzword;
import kiyobot.util.MessageContentType;
import kiyobot.util.TimeConverter;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedImage;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MessageEvent {
    
    INSTANCE();

//	private static final Logger LOGGER = LogManager.getLogger();
    
    private static final Gson GSON = new Gson();
    private static final Gson GSON_PRETTY = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);
    
    private static final Pattern REMINDER_FULL_SUFFIX_REGEX = Pattern.compile("!remindme (?<time>\\d+) (?<suffix>seconds|minutes|hours) +(?<msg>.*)");
    private static final Pattern REMINDER_PARTIAL_SUFFIX_REGEX = Pattern.compile("!remindme (?<time>\\d+) (?<suffix>sec|min|hr|day) +(?<msg>.*)");
    private static final Pattern REMINDER_CHAR_SUFFIX_REGEX = Pattern.compile("!remindme (?<time>\\d+) (?<suffix>[smhd]) +(?<msg>.*)");
    private static final Pattern NEW_EMOTE_EMBED_REGEX = Pattern.compile("!newemote (?<name>[a-zA-Z0-9_]{2,})");
    // private static final Pattern NEW_EMOTE_IMAGE_URL_REGEX = Pattern.compile("!newemote (?<name>[a-zA-Z0-9_]{2,}) (?<url>.*)");
    
    private static final Matcher REMINDER_FULL_MATCHER = REMINDER_FULL_SUFFIX_REGEX.matcher("").reset();
    private static final Matcher REMINDER_PARTIAL_MATCHER = REMINDER_PARTIAL_SUFFIX_REGEX.matcher("").reset();
    private static final Matcher REMINDER_CHAR_MATCHER = REMINDER_CHAR_SUFFIX_REGEX.matcher("").reset();
    private static final Matcher NEW_EMOTE_EMBED_MATCHER = NEW_EMOTE_EMBED_REGEX.matcher("").reset();
    // private static final Matcher NEW_EMOTE_IMAGE_URL_MATCHER = NEW_EMOTE_IMAGE_URL_REGEX.matcher("").reset();
    
    private static final String SUGGESTION_LINK = "https://forms.gle/Y6pKqMAgYUS6eJJL7";
    private static final String DOC_LINK_VIEW_ONLY = "https://docs.google.com/document/d/1gmVzkkEiOadXF6ThIalBqzCuuyrGVs2ZGUzqcGeQZyE/edit?usp=sharing";
    private static final String CELTX_LINK = "https://www.celtx.com/a/ux/#documents";
    private static final String GITHUB_LINK = "https://github.com/dknoma/Calytrix";
    
    private static final EmbedBuilder COMMANDS_EMBED;
    
    static {
        COMMANDS_EMBED = new EmbedBuilder()
                                     .setTitle("Bot commands")
                                     .setColor(new Color(186, 120, 252));
    
        Arrays.stream(BasicCommandType.values())
              .filter(type -> type != BasicCommandType.DEFAULT)
              .forEach(commandType -> COMMANDS_EMBED.addField(String.format("**%s**", commandType.getCommand()), commandType.getDescription()));
    }
    
    private Matcher matcher;
    
    private int PINGS;
    private ScheduledFuture pingExpiration;
    
    private MongoDatabase db;
    
    MessageEvent() {
        // Utility
    }
    
    /**
     * Adds message listener to the api, which allows the bot to listen to Discord messages
     *
     * @param api - Javacord API class
     */
    public void listenOnMessage(DiscordApi api, KiyoMongoSettings settings) {
        this.db = settings.getDatabase();
        final Collection<Server> servers = api.getServers();
        onStartBot(servers);
        
        api.addMessageCreateListener(messageEvent -> {
            try {
                onMessage(messageEvent);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    private void onStartBot(Collection<Server> servers) {
        servers.forEach(this::getReminders);
    }
    
    private void getReminders(Server server) {
        final MongoCollection<Document> collection =
                db.getCollection(MongoCollectionType.USER_REMINDERS.collectionName());
        
        final FindIterable<Document> documents = collection.find();
        final MongoCursor<Document> cursor = documents.cursor();
        
        while (cursor.hasNext()) {
            final Document doc = cursor.next();
            final long channelId = doc.getLong(CHANNEL_ID_KEY);
            final Optional<ServerChannel> channel;
            
            if ((channel = server.getChannelById(channelId)).isPresent()) {
                final long authorId = doc.getLong(AUTHOR_ID_KEY);
                final long time = doc.getLong(TIME_KEY);
                final long targetTime = doc.getLong(TARGET_TIME_KEY);
                final ReminderTimeUnit unit = ReminderTimeUnit.getUnit(doc.getString(TIME_UNIT_KEY));
                final String reminderMessage = doc.getString(REMINDER_MESSAGE_KEY);
                
                final long currentMillis = System.currentTimeMillis();
                final long deltaTime = targetTime - currentMillis;
                final TimeUnit timeUnit = unit.toTimeUnit();
                
                scheduleReminder(() -> {
                    channel.flatMap(Channel::asTextChannel).get().sendMessage(String.format("<@%s> - %s", authorId,
                                                                                            reminderMessage));
                    collection.deleteOne(doc);
                }, deltaTime > 0 ? deltaTime : 0, timeUnit);
            }
        }
    }
    
    /**
     * Parse message and checks the content type to determine whether to respond to a command or not
     *
     * @param messageEvent message event
     */
    private void onMessage(MessageCreateEvent messageEvent) throws ExecutionException, InterruptedException {
        final String content = messageEvent.getMessageContent();
        final MessageContentType messageType = MessageContentType.getByPrefix(content);
        
        switch (messageType) {
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
        switch (buzzword) {
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
     *
     * @param messageEvent message event
     * @param message      message
     */
    private void decodeBasicCommand(MessageCreateEvent messageEvent, String message) throws ExecutionException,
                                                                                                    InterruptedException {
        final BasicCommandType commandType = BasicCommandType.getByCommandMessage(message);
        
        switch (commandType) {
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
            case REMIND_ME:
                doEncodeReminder(messageEvent);
                break;
            case GITHUB:
                doEncodeGithub(messageEvent);
                break;
            case NEW_EMOTE:
                doEncodeNewEmote(messageEvent);
                break;
            case DEFAULT:
                doEncodeUnknownCommand(messageEvent);
                break;
        }
    }
    
    /**
     * Sends a list of the current commands to the channel
     *
     * @param messageEvent;
     */
    private void doEncodeCommandsList(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(COMMANDS_EMBED);
    }
    
    private void doEncodeDocLink(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(String.format("**Viewable design document link**\n%s\n", DOC_LINK_VIEW_ONLY));
    }
    
    private void doEncodeCeltx(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(String.format("**Celtx story collab link**\n%s\n", CELTX_LINK));
    }
    
    /**
     * What is this?
     *
     * @param messageEvent message event
     */
    private void doEncodeHewwo(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("OWO what's this?");
    }
    
    /**
     * Perform ping command
     *
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
     *
     * @param messageEvent
     */
    private void doEncodeReminder(MessageCreateEvent messageEvent) {
        final TextChannel channel = messageEvent.getChannel();
        final Message message = messageEvent.getMessage();
        final String text = message.getContent();
        REMINDER_FULL_MATCHER.reset(text);
        REMINDER_PARTIAL_MATCHER.reset(text);
        REMINDER_CHAR_MATCHER.reset(text);
        
        final ReminderSuffixType type;
        if (REMINDER_FULL_MATCHER.matches()) {
            this.matcher = REMINDER_FULL_MATCHER;
            type = FULL;
        } else if (REMINDER_PARTIAL_MATCHER.matches()) {
            this.matcher = REMINDER_PARTIAL_MATCHER;
            type = PARTIAL;
        } else if (REMINDER_CHAR_MATCHER.matches()) {
            this.matcher = REMINDER_CHAR_MATCHER;
            type = CHAR;
        } else {
            type = NULL;
        }
        
        if (type != NULL) {
            final String unit = matcher.group("suffix");
            final String reminderMessage = matcher.group("msg");
            
            try {
                final MessageAuthor author = message.getAuthor();
                final long userId = author.getId();
                
                final long time = Long.parseLong(matcher.group("time"));
                final ReminderTimeUnit timeUnit = ReminderTimeUnit.getUnit(unit);
                
                final TimeUnit targetUnit = timeUnit.toTimeUnit();
                final long targetTime = TimeConverter.fromMillis(time, targetUnit) + System.currentTimeMillis();
                
                // LOGGER.debug("user: {}({}), channelId: {}, time: {}, unit: {}, message: {}",
                //             author.getDisplayName(), userId, channel.getId(), time, timeUnit, reminderMessage);
                
                final MongoCollection<Document> collection =
                        db.getCollection(MongoCollectionType.USER_REMINDERS.collectionName());
                
                UserReminderDocument document = new UserReminderDocument();
                document.putAll(userId, channel.getId(), time, unit, reminderMessage, targetTime);
                Document doc = document.getDocument();
                
                collection.insertOne(doc);
                
                scheduleReminder(() -> {
                    channel.sendMessage(String.format("<@%s> - %s", userId, reminderMessage));
                    collection.deleteOne(doc);
                }, time, targetUnit);
                
                channel.sendMessage("Haaaaaai~ :thumbsup:");
            } catch (NumberFormatException nfe) {
                // LOGGER.error(nfe.getMessage());
            }
        } else {
            channel.sendMessage(String.format("Command \"%s\" not recognized. Valid format: !remindme <time> <unit> <reminder message>", text));
        }
    }
    
    private static void scheduleReminder(Runnable run, long time, TimeUnit timeUnit) {
        final ScheduledFuture<?> future = scheduler.schedule(run, time, timeUnit);
    }
    
    private void doEncodeGithub(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage(GITHUB_LINK);
    }
    
    private void doEncodeNewEmote(MessageCreateEvent messageEvent) throws ExecutionException, InterruptedException {
        final Message message = messageEvent.getMessage();
        final String text = message.getContent();
        NEW_EMOTE_EMBED_MATCHER.reset(text);
        // NEW_EMOTE_IMAGE_URL_MATCHER.reset(text);
        
        final NewEmoteMessageType type;
        if (NEW_EMOTE_EMBED_MATCHER.matches()) {
            this.matcher = NEW_EMOTE_EMBED_MATCHER;
            type = NewEmoteMessageType.EMBED;
        // } else if (NEW_EMOTE_IMAGE_URL_MATCHER.matches()) {
        //     this.matcher = NEW_EMOTE_IMAGE_URL_MATCHER;
        //     type = NewEmoteMessageType.URL;
        } else {
            type = NewEmoteMessageType.NULL;
        }
        
        switch (type) {
            case EMBED: {
                if(messageEvent.getServer().isPresent()) {
                    final List<MessageAttachment> attachments = message.getAttachments();
                    
                    if(attachments.size() > 0) {
                        final Server server = messageEvent.getServer().get();
                        final MessageAttachment attachment = attachments.get(0);
                        
                        final boolean isImage = attachment.isImage();
                        final int attachmentSize = attachment.getSize();
                        final int serverEmoteCount = server.getCustomEmojis().size();
                        
                        if(serverEmoteCount < 100 && isImage && attachmentSize <= 256000) {
                            final CustomEmojiBuilder customEmojiBuilder = new CustomEmojiBuilder(server);
                            final String emoteName = matcher.group("name");
                            final URL imageUrl = attachment.getUrl();
    
                            final CompletableFuture<KnownCustomEmoji> knownCustomEmojiCompletableFuture =
                                    customEmojiBuilder.setImage(imageUrl)
                                                      .setName(emoteName)
                                                      .create();
    
                            final KnownCustomEmoji knownCustomEmoji = knownCustomEmojiCompletableFuture.get();
                            final Optional<CustomEmoji> customEmojiO = knownCustomEmoji.asCustomEmoji();
    
                            if (customEmojiO.isPresent()) {
                                final CustomEmoji customEmoji = customEmojiO.get();
        
                                messageEvent.getChannel().sendMessage(String.format("Enjoy your new emote %s!",
                                                                                    customEmoji.getMentionTag()));
                            }
                        } else if(!isImage) {
                            messageEvent.getChannel()
                                        .sendMessage("Message attachment is not an image. Please try again.");
                        } else if(attachmentSize > 256000) {
                            messageEvent.getChannel()
                                        .sendMessage(String.format("Attachment size %d > 256kB. Please try a smaller image.",
                                                                   attachmentSize));
                        } else {
                            messageEvent.getChannel()
                                        .sendMessage("Server emote limit has been reached. Please remove some emotes and try again.");
                        }
                    } else {
                        messageEvent.getChannel()
                                    .sendMessage("No message attachments present. Please attach an image and try again.");
                    }
                } else {
                    messageEvent.getChannel()
                                .sendMessage("Couldn't retrieve server from message. Please try again.");
                }
                break;
            }
            default: {
                messageEvent.getChannel()
                            .sendMessage(
                                    String.format("Command \"%s\" not recognized. Valid format: !newemote <name> {image attachment (size <= 256kB)}",
                                                  text));
                break;
            }
        }
    }
    
    private void doEncodeUnknownCommand(MessageCreateEvent messageEvent) {
        messageEvent.getChannel().sendMessage("Unknown command");
    }
    
    private void schedulePingExpiration() {
        if (pingExpiration != null) {
            pingExpiration.cancel(false);
        }
        pingExpiration = scheduler.schedule(() -> {
            PINGS = 0;
        }, 60, TimeUnit.SECONDS);
    }
}
