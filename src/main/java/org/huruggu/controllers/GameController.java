package org.huruggu.controllers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.huruggu.models.Rooms;
import org.huruggu.models.Users;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hwangdonghyeon on 2017. 6. 21..
 */
public class GameController extends Controller {
    SharedData sharedData;
    LocalMap<String, String> sockets;
    EventBus eventBus;
    public void start(Future<Void> startFuture) {
        sharedData = vertx.sharedData();
        sockets = sharedData.getLocalMap("sockets");
        eventBus = vertx.eventBus();
        eventBus.consumer("route:game.join", this::join);
        eventBus.consumer("route:game.chat", this::chat);
        startFuture.complete();
    }

    private void join(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject().put("route", "game.join");
        String identity = sockets.get(request.getString("socketID"));
        Users.get(identity, result -> {
            if (result.succeeded()) {
                Users user = result.result();
                JsonObject jsonUser = user.toJsonObject();
                jsonUser.put("socketID", request.getString("socketID"));

                Rooms.join(jsonUser, (AsyncResult<JsonObject> result_) -> {
                    if(result_.succeeded()) {
                        JsonObject data = result_.result();
                        JsonArray players = data.getJsonArray("players");
                        JsonObject slots = data.getJsonObject("slots");
                        int slotsKey;
                        for(slotsKey = 0; slotsKey < 8; slotsKey++) {
                            if(slots.getString("s-"+String.valueOf(slotsKey)).isEmpty()) {
                                data.getJsonObject("slots").put("s-"+String.valueOf(slotsKey), request.getString("socketID"));
                                break;
                            }
                        }
                        jsonUser.put("slot", slotsKey);
                        Rooms.attachSlot(data.getString("_id"), data.getJsonObject("slots"), (AsyncResult<Void> result__) -> {
                            JsonObject response_ = new JsonObject().put("route", "game.anotherPlayerJoin");
                            response_.put("player", jsonUser);
                            if(result__.succeeded()) {
                                Iterator<JsonObject> itr = players.getList().iterator();
                                while(itr.hasNext()) {
                                    JsonObject player = itr.next();
                                    if(player.getString("socketID").equals(request.getString("socketID"))) {
                                        player.put("isMe", true);
                                    } else {
                                        player.put("isMe", false);
                                        System.out.println(player.getString("socketID"));
                                        if(sockets.containsKey(player.getString("socketID"))) {
                                            eventBus.send(player.getString("socketID"), Buffer.buffer().appendString(response_.toString()+"\n"));
                                        }

                                    }
                                }
                                response.put("data", data);
                                message.reply(response);
                            }
                        });
                    } else {
                        Rooms.create(jsonUser,(AsyncResult<JsonObject> result__) -> {
                            if(result__.succeeded()) {
                                response.put("data", result__.result());
                                message.reply(response);
                            }

                        });
                    }
                });
            }
        });
    }

    private void play(Message<JsonObject> message) {

    }

    private void chat(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject().put("route", "game.chat");

        JsonObject data = request.getJsonObject("data");


        Rooms.getRoom(data.getString("roomID"), (AsyncResult<JsonObject> result) -> {
            if(result.succeeded()) {
                JsonObject roomData = result.result();
                System.out.println(roomData.toString());
                response.put("status","success");
                response.put("data", new JsonObject().put("sender", request.getString("socketID")).put("message", data.getString("message")));
                Iterator<JsonObject> itr = roomData.getJsonArray("players").getList().iterator();
                while(itr.hasNext()) {
                    JsonObject player = itr.next();
                    if(!player.getString("socketID").equals(request.getString("socketID"))) {
                        if(sockets.containsKey(player.getString("socketID"))) {
                            eventBus.send(player.getString("socketID"), Buffer.buffer().appendString(response.toString()+"\n"));
                        }
                    }
                }
                message.reply(response);
            } else {
                response.put("status", "error");
                message.reply(response);
            }
        });


    }
}
