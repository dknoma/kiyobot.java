package db.mongo.documents;

public class DocumentConstants {
    public enum Type {
        REMINDER,
    }
    
    public class UserReminderDocument {
        public static final String DISPLAY_NAME_KEY = "displayName";
        public static final String CHANNEL_ID_KEY = "channelId";
        public static final String TIME_KEY = "time";
        public static final String TIME_UNIT_KEY = "unit";
        public static final String REMINDER_MESSAGE_KEY = "reminderMessage";
        
        private UserReminderDocument() {
            // Utility
        }
    }
    
    private DocumentConstants() {
        // Utility
    }
}
