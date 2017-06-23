package org.huruggu.models;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.huruggu.dbs.Mongo;

/**
 * Created by hwangdonghyeon on 2017. 6. 23..
 */
public class Rooms extends Users {

    private static String collection = "rooms";
    private static MongoClient client = Mongo.getClient();
    private static int maxPlayer = 8;

    public static void join(JsonObject player, Handler<AsyncResult<JsonObject>> aHandler) {
        JsonObject query = new JsonObject().put("$where", "this.players.length < " + Rooms.maxPlayer);
        JsonObject update = new JsonObject().put("$push", new JsonObject().put("players", player));
        FindOptions findOptions = new FindOptions().setSort(new JsonObject().put("_id", 1));
        client.findOneAndUpdateWithOptions(Rooms.collection, query, update, findOptions, new UpdateOptions() , result -> {
            if(result.succeeded()) {
                if(result.result() == null) {
                    aHandler.handle(Future.failedFuture("no found room"));
                } else {
                    client.find(Rooms.collection, new JsonObject().put("_id", result.result().getString("_id")), result_ -> {
                        if(result_.succeeded()) {
                            aHandler.handle(Future.succeededFuture(result_.result().get(0)));
                        }
                    });
                    System.out.println(result.result().encodePrettily());
                }
            } else {
                result.cause().printStackTrace();
            }
        });
    }

    public static void attachSlot(String roomID, JsonObject slots_, Handler<AsyncResult<Void>> aHandler) {
        JsonObject query = new JsonObject().put("_id", roomID);
        JsonObject slots = new JsonObject().put("$set", new JsonObject().put("slots", slots_));
        client.update(Rooms.collection, query, slots, result -> {
            if(result.succeeded()) {
                aHandler.handle(Future.succeededFuture());
            } else {
                result.cause().printStackTrace();
            }
        });
    }

    public static void create(JsonObject player, Handler<AsyncResult<JsonObject>> aHandler) {
        JsonObject slots = new JsonObject();
        slots.put("s-0", player.getString("socketID"));
        for(int i = 1; i < Rooms.maxPlayer; i++) {
            slots.put("s-"+ String.valueOf(i), "");

        }
        JsonObject room = new JsonObject().put("players", new JsonArray().add(player)).put("slots", slots);
        client.insert(Rooms.collection, room, result -> {
            if(result.succeeded()) {
                room.put("_id", result.result());
                System.out.println(room);
                aHandler.handle(Future.succeededFuture(room));
            } else {
                result.cause().printStackTrace();
                aHandler.handle(Future.failedFuture(""));
            }
        });
    }

    public static void getRoom(String _id, Handler<AsyncResult<JsonObject>> aHandler) {
        JsonObject query = new JsonObject().put("_id", _id);
        client.find(Rooms.collection, query, result -> {
            if(result.succeeded()) {
                aHandler.handle(Future.succeededFuture(result.result().get(0)));
            }
        });
    }

    public static void leave() {

    }

    public static void reset() {
        client.remove(Rooms.collection, new JsonObject(), res -> {

            if (res.succeeded()) {

                System.out.println("Rooms Document Reset Complete");

            } else {

                res.cause().printStackTrace();

            }
        });
    }
}
