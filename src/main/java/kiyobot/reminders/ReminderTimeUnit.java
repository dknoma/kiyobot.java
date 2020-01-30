package kiyobot.reminders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ReminderTimeUnit {
    SECONDS("s"),
    MINUTES("m"),
    HOURS("h"),
    DAYS("d");
    
    private static final Map<String, ReminderTimeUnit> UNIT_BY_SUFFIX = new HashMap<>();
    
    static {
        Arrays.asList(ReminderTimeUnit.values())
              .forEach(unit -> UNIT_BY_SUFFIX.put(unit.unit, unit));
    }
    
    private String unit;
    
    ReminderTimeUnit(String unit) {
        this.unit = unit;
    }
    
    public String suffix() {
        return unit;
    }
    
    public static ReminderTimeUnit getUnit(String unit) {
        return UNIT_BY_SUFFIX.getOrDefault(unit.toLowerCase(), SECONDS);
    }
}
