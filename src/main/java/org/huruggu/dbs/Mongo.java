package org.huruggu.dbs;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sql.SQLClient;

/**
 * Created by hwangdonghyeon on 2017. 6. 22..
 */
public class Mongo {
    private static MongoClient mongoClient = null;

    public static void initialize(Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config.getJsonObject("mongo"));
    }

    public static MongoClient getClient() {
        return Mongo.mongoClient;
    }
}
