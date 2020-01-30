package kiyobot.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Buzzword {
    AYYLMAO(Pattern.compile("\\s*ay++\\s*")),
    OWO(Pattern.compile("(.*\\s+)*(owo)(\\s+.*)*")),
    DEFAULT();

    private static final Map<Pattern, Buzzword> INSTANCE_BY_BUZZWORD = new HashMap<>();
    private static final Matcher matcher = Pattern.compile("").matcher("").reset();
    
    static {
        Arrays.asList(Buzzword.values())
                .forEach(instance -> INSTANCE_BY_BUZZWORD.put(instance.getBuzzwordPattern(), instance));
    }
    
    private final Pattern buzzwordPattern;
    
    Buzzword() {
        this.buzzwordPattern = Pattern.compile(".");
    }
    
    Buzzword(Pattern buzzwordPattern) {
        this.buzzwordPattern = buzzwordPattern;
    }
    
    public Pattern getBuzzwordPattern() {
        return buzzwordPattern;
    }
    
    public static Buzzword getByFirstMatch(String message) {
        return foundBuzzword(message);
    }
    
    private static Buzzword foundBuzzword(String message) {
        final AtomicReference<Boolean> stop = new AtomicReference<>(false);
        final AtomicReference<Buzzword> instance = new AtomicReference<>(DEFAULT);
        Arrays.stream(Buzzword.values())
              .filter(buzzword -> buzzword != DEFAULT)
              .forEach(buzzword -> {
                  if(!stop.get() && buzzword.hasMatch(message)) {
                      instance.set(buzzword);
                      stop.set(true);
                  }
              });
        return instance.get();
    }
    
    private boolean hasMatch(String message) {
        final Matcher match = matcher.usePattern(this.buzzwordPattern).reset(message.toLowerCase());
        return match.matches();
    }
}
