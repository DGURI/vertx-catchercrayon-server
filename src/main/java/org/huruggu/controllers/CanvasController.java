package org.huruggu.controllers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.huruggu.models.Rooms;

import java.util.Iterator;

/**
 * Created by hwangdonghyeon on 2017. 6. 23..
 */
public class CanvasController extends Controller {
    SharedData sharedData;
    LocalMap<String, String> sockets;
    EventBus eventBus;

    public void start(Future<Void> startFuture) {
        eventBus = vertx.eventBus();
        sharedData = vertx.sharedData();
        sockets = sharedData.getLocalMap("sockets");
        eventBus.consumer("route:canvas.drawing", this::drawing);
        eventBus.consumer("route:canvas.clear", this::clear);
        startFuture.complete();
    }

    private void drawing(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject().put("route", "canvas.drawing");

        JsonObject drawingInfo = request.getJsonObject("drawingInfo");

        Rooms.getRoom(request.getString("roomID"), (AsyncResult<JsonObject> result) -> {
            if(result.succeeded()) {
                JsonObject roomData = result.result();
                System.out.println(roomData.toString());
                response.put("data", new JsonObject().put("sender", request.getString("socketID")).put("drawingInfo", drawingInfo));
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

    private void clear(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject().put("route", "canvas.clear");

        Rooms.getRoom(request.getString("roomID"), (AsyncResult<JsonObject> result) -> {
            if(result.succeeded()) {
                JsonObject roomData = result.result();
                System.out.println(roomData.toString());
                response.put("data", new JsonObject().put("sender", request.getString("socketID")));
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
