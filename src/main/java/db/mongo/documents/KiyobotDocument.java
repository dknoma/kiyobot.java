package db.mongo.documents;

import org.bson.Document;

public interface KiyobotDocument {
    void putData(Object... data);

    Document getDocument();
}
