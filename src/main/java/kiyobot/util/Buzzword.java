package kiyobot.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Buzzword {
    AYYLMAO(Pattern.compile("\\s*ay++\\s*")),
    OWO(Pattern.compile("(.*\\s+)*(owo)(\\s+.*)*")),
    DEFAULT();
    
    private static final Map<Pattern, Buzzword> INSTANCE_BY_BUZZWORD = new HashMap<>();
    
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
        Buzzword instance = DEFAULT;
        final String[] parts = message.split("\\s++");
        for(String word : parts) {
            instance = foundBuzzword(word);
            if(instance != DEFAULT) {
                break;
            }
        }
        return instance;
    }
    
    private static Buzzword foundBuzzword(String message) {
        Buzzword instance = DEFAULT;
        for(Buzzword buzzword : Buzzword.values()) {
            if(buzzword.hasMatch(message)) {
                instance = buzzword;
                break;
            }
        }
        return instance;
    }
    
    public boolean hasMatch(String message) {
        final Matcher matcher = this.buzzwordPattern.matcher(message.toLowerCase());
        return matcher.find();
    }
}
