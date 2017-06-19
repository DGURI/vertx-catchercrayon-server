package org.huruggu.engine;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hwangdonghyeon on 2017. 6. 18..
 */
public class ServerEngine extends AbstractVerticle {
    private LocalMap<String, HashMap<String, String>> sockets;
    private SharedData sd;

    public void start() {
        EventBus eventBus = vertx.eventBus();
        sd = vertx.sharedData();
        sockets = sd.getLocalMap("sockets");

        Handler<NetSocket> connectHandler = socket -> {
            String handlerID = socket.writeHandlerID();
            sockets.put(handlerID, null);
            for (Map.Entry<String, HashMap<String, String>> entry : sockets.entrySet()) {
                System.out.println(entry.getKey());
            }
            socket.handler(RecordParser.newDelimited("\n", buffer -> {
                String message = buffer.toString().trim();
                try {
                    JsonObject param = new JsonObject(message);
                    String route = param.getString("route");
                    eventBus.send("route." + route, param, (AsyncResult<Message<JsonObject>> result) -> {
                        if (result.succeeded()) {
                            JsonObject jsonObject = result.result().body();
                            eventBus.send(handlerID, Buffer.buffer().appendString(jsonObject.toString() + "\n"));
                        } else {

                        }
                    });

                } catch (Exception e) {

                }
            }));
            socket.closeHandler(v -> {
                sockets.remove(handlerID);
                Helper.printDebug(handlerID + "");
            });
            socket.exceptionHandler(v -> {
                sockets.remove(handlerID);
                Helper.printDebug(v);
            });
        };

        vertx.createNetServer()
                .connectHandler(connectHandler)
                .listen(9999, res -> {
                    if (res.succeeded()) {
                        Helper.printDebug("Socket Open");
                    } else {
                        Helper.printDebug("Failed Socket Open");
                    }
                });

        vertx.deployVerticle(new Routes(), (AsyncResult<String> result) -> {
            if(result.succeeded()) {
                Helper.printDebug("Route Engine Verticle deployment complete");
            }
        });
    }
}
