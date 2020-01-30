package db.mongo.documents;

import org.bson.Document;

public interface KiyobotDocument {
    // protected Document document;

    void putData(Object... data);

    Document getDocument();
}
