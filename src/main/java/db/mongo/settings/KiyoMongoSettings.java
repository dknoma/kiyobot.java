package db.mongo.settings;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class KiyoMongoSettings {
    private static final String CONNECTION_STRING_FORMAT = "mongodb+srv://%s:%s@botto-j6mtg.mongodb.net/test?retryWrites=true&w=majority";
    
    private ConnectionString connectionStringFormat;
    
    public KiyoMongoSettings(String user, String password) {
        this.connectionStringFormat = new ConnectionString(String.format(CONNECTION_STRING_FORMAT, user, password));
    }
    
    public MongoDatabase getDatabase() {
        final ConnectionString uri = getConnectionString();
        
        final MongoClientSettings settings = MongoClientSettings.builder()
                                                                .applyConnectionString(uri)
                                                                .build();
        
        final MongoClient mongoClient = MongoClients.create(settings);
        final MongoDatabase db = mongoClient.getDatabase("test");
        
        MongoCollectionType.initCollections(db);
        
        return db;
    }
    
    private ConnectionString getConnectionString() {
        return connectionStringFormat;
    }
}
