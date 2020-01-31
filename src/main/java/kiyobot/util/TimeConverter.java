package kiyobot.util;

import java.util.concurrent.TimeUnit;

public final class TimeConverter {
    public static long fromMillis(long time, TimeUnit timeUnit) {
        long result;
        switch (timeUnit) {
            case SECONDS:
                result = milliToSeconds(time);
                break;
            case MINUTES:
                result = milliToMinutes(time);
                break;
            case HOURS:
                result = milliToHours(time);
                break;
            case DAYS:
                result = milliToDays(time);
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

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
