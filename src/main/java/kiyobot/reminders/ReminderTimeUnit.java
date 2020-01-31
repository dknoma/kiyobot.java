package kiyobot.reminders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ReminderTimeUnit {
    SECONDS("s", "sec", "seconds"),
    MINUTES("m", "min", "minute"),
    HOURS("h", "hr", "hour"),
    DAYS("d", "day", "day");
    
    private static final Map<String, ReminderTimeUnit> UNIT_BY_SINGLE_CHAR_SUFFIX = new HashMap<>();
    private static final Map<String, ReminderTimeUnit> UNIT_BY_PARTIAL_SUFFIX = new HashMap<>();
    private static final Map<String, ReminderTimeUnit> UNIT_BY_FULL_SUFFIX = new HashMap<>();

    static {
        Arrays.asList(ReminderTimeUnit.values())
              .forEach(unit -> UNIT_BY_SINGLE_CHAR_SUFFIX.put(unit.unitChar, unit));
        Arrays.asList(ReminderTimeUnit.values())
              .forEach(unit -> UNIT_BY_PARTIAL_SUFFIX.put(unit.partialUnitSuffix, unit));
        Arrays.asList(ReminderTimeUnit.values())
              .forEach(unit -> UNIT_BY_FULL_SUFFIX.put(unit.fullUnitSuffix, unit));
    }
    
    final private String unitChar;
    final private String partialUnitSuffix;
    final private String fullUnitSuffix;

    ReminderTimeUnit(String unit, String partialUnitSuffix, String fullUnitSuffix) {
        this.unitChar = unit;
        this.partialUnitSuffix = partialUnitSuffix;
        this.fullUnitSuffix = fullUnitSuffix;
    }

    public String suffix() {
        return unitChar;
    }
    
    public static ReminderTimeUnit getUnit(String unit) {
        final ReminderTimeUnit timeUnit;
        final int len = unit.length();
        if(len == 1) {
            timeUnit = UNIT_BY_SINGLE_CHAR_SUFFIX.getOrDefault(unit.toLowerCase(), SECONDS);
        } else if(2 <= len && len <=3) {
            timeUnit = UNIT_BY_PARTIAL_SUFFIX.getOrDefault(unit.toLowerCase(), SECONDS);
        } else if(len > 3){
            timeUnit = UNIT_BY_FULL_SUFFIX.getOrDefault(unit.toLowerCase(), SECONDS);
        } else {
            timeUnit = SECONDS;
        }

        return timeUnit;
    }
}
