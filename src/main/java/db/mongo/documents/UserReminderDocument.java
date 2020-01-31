package db.mongo.documents;

import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.AUTHOR_ID_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.CHANNEL_ID_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.REMINDER_MESSAGE_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TARGET_TIME_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TIME_KEY;
import static db.mongo.documents.util.DocumentConstants.UserReminderDocumentKeys.TIME_UNIT_KEY;

import org.bson.Document;

public class UserReminderDocument implements KiyobotDocument {

    private Document document;

    public UserReminderDocument() {
        this.document = new Document();
    }

    public void putAll(long authorId, long channelId, long time, String timeUnit, String reminderMessage, long targetTime) {
        putData(authorId, channelId, time, timeUnit, reminderMessage, targetTime);
    }

    @Override
    public void putData(Object... data) {
        document.put(AUTHOR_ID_KEY, data[0]);
        document.put(CHANNEL_ID_KEY, data[1]);
        document.put(TIME_KEY, data[2]);
        document.put(TIME_UNIT_KEY, data[3]);
        document.put(REMINDER_MESSAGE_KEY, data[4]);
        document.put(TARGET_TIME_KEY, data[5]);
    }

    @Override
    public Document getDocument() {
        return document;
    }
}
