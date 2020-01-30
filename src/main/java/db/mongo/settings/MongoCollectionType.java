package db.mongo.settings;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MongoCollectionType {
    USER_REMINDERS("user_reminders"),
    NULL("");
    
    private static final Map<String, MongoCollectionType> COLLECTIONS_BY_NAME = new HashMap<>();
    
    static {
        Arrays.asList(MongoCollectionType.values())
              .forEach(collection -> COLLECTIONS_BY_NAME.put(collection.collectionName, collection));
    }
    
    private String collectionName;
    
    MongoCollectionType(String name) {
        this.collectionName = name;
    }
    
    public String collectionName() {
        return this.collectionName;
    }
    
    public static void initCollections(MongoDatabase db) {
        COLLECTIONS_BY_NAME.entrySet()
                           .stream()
                           .filter(e -> e.getValue() != NULL)
                           .forEach(entry -> {
                               final String name = entry.getKey();
                               final MongoCollectionType title = entry.getValue();
                               MongoCollection collection = db.getCollection(name);
                               if(collection == null) {
                                   db.createCollection(name);
                               }
                           });
    }
    
    public static MongoCollectionType getByName(String name) {
        return COLLECTIONS_BY_NAME.getOrDefault(name, NULL);
    }
}
