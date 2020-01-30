package kiyobot.util;

public final class HerokuEnv {
    private static final String AUTH_TOK_KEY = "authTok";
    private static final String BOT_STUFF_KEY = "botStuff";
    private static final String MONGO_USER_KEY = "mongoUser";
    private static final String MONGO_PASS_KEY = "mongoPass";
    
    private String authTok;
    private String botStuff;
    private String mongoUser;
    private String mongoPass;
    
    public void parseEnv() {
        this.authTok = System.getenv(AUTH_TOK_KEY);
        this.botStuff = System.getenv(BOT_STUFF_KEY);
        this.mongoUser = System.getenv(MONGO_USER_KEY);
        this.mongoPass = System.getenv(MONGO_PASS_KEY);
    }
    
    public String getAuthTok() {
        return authTok;
    }
    
    public String getBotStuff() {
        return botStuff;
    }
    
    public String getMongoUser() {
        return mongoUser;
    }
    
    public String getMongoPass() {
        return mongoPass;
    }
}
