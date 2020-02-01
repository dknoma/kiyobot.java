package kiyobot.util.reminders;

public enum ReminderSuffixType {
    FULL,
    PARTIAL,
    CHAR,
    NULL;

    public static ReminderSuffixType valueOf(int ordinal) {
        switch (ordinal) {
            case 0:
                return FULL;
            case 1:
                return PARTIAL;
            case 2:
                return CHAR;
        }
        return NULL;
    }
}
