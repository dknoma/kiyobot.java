package kiyobot.util;

public final class TimeConverter {

    public static long milliToSeconds(long milli) {
        return milli * 1000;
    }

    public static long milliToMinutes(long milli) {
        return milli * 1000 * 60;
    }

    public static long milliToHours(long milli) {
        return milli * 1000 * 60 * 60;
    }

    public static long milliToDays(long milli) {
        return milli * 1000 * 60 * 60 * 24;
    }

    public static long secondsToMilli(long milli) {
        return milli / 1000;
    }

    public static long minutesToMilli(long milli) {
        return milli / 1000 / 60;
    }

    public static long hoursToMilli(long milli) {
        return milli / 1000 / 60 / 60;
    }

    public static long daysToMilli(long milli) {
        return milli / 1000 / 60 / 60 / 24;
    }

    private TimeConverter() {
        // Utility
    }
}
