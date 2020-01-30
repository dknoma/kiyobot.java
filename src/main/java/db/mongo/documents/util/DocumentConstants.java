package db.mongo.documents.util;

public class DocumentConstants {
    public enum Type {
        REMINDER,
    }
    
    public static class UserReminderDocumentKeys {
        public static final String AUTHOR_ID_KEY = "authorId";
        public static final String CHANNEL_ID_KEY = "channelId";
        public static final String TIME_KEY = "time";
        public static final String TIME_UNIT_KEY = "unit";
        public static final String REMINDER_MESSAGE_KEY = "reminderMessage";
        public static final String TARGET_TIME_KEY = "targetTime";
        
        private UserReminderDocumentKeys() {
            // Utility
        }
    }
    
    private DocumentConstants() {
        // Utility
    }
}
