package kiyobot.util.emotes;

public enum NewEmoteMessageType {
    EMBED,
    URL,
    NULL;
    
    public static NewEmoteMessageType valueOf(int ordinal) {
        switch (ordinal) {
            case 0:
                return EMBED;
            case 1:
                return URL;
        }
        return NULL;
    }
}
