package org.huruggu.engine;


import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.sql.SQLConnection;
import org.huruggu.dbs.Jdbc;
import org.huruggu.dbs.Mongo;
import org.huruggu.models.Rooms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hwangdonghyeon on 2017. 6. 18..
 */
public class ServerEngine extends AbstractVerticle {
    private static JsonObject config;
    public LocalMap<String, String> sockets;
    public SharedData sharedData;

    public static JsonObject getConfig() {
        return ServerEngine.config;
    }

    public void start(Future<Void> future) {
        config = context.config();
        sharedData = vertx.sharedData();
        sockets = sharedData.getLocalMap("sockets");

        Jdbc.initialize(vertx, config);
        Mongo.initialize(vertx, config);
        startBackend(
                (connection) -> startSocketListen(connection,
                        (netServer) -> startDeployVerticle(netServer,
                                (complete) -> completeCacherCrayonEngine(complete, future),
                                future
                        ),
                        future
                ),
                future
        );

    }

    public void startBackend(Handler<AsyncResult<SQLConnection>> next, Future<Void> future) {
        Jdbc.getSQLClient().getConnection(ar -> {
            if (ar.failed()) {
                future.fail(ar.cause());
            } else {
                next.handle(Future.succeededFuture(ar.result()));
            }
        });
    }

    private void startSocketListen(AsyncResult<SQLConnection> sqlConnectionAsyncResult, Handler<AsyncResult<NetServer>> next, Future<Void> future) {
        if (sqlConnectionAsyncResult.failed()) {
            future.fail(sqlConnectionAsyncResult.cause());
        } else {
            EventBus eventBus = vertx.eventBus();
            Handler<NetSocket> connectHandler = socket -> {
                String handlerID = socket.writeHandlerID();
                sockets.put(handlerID, "guest");
                for (Map.Entry<String, String> entry : sockets.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                socket.handler(RecordParser.newDelimited("\n", buffer -> {
                    String message = buffer.toString().trim();
                    try {
                        JsonObject param = new JsonObject(message);
                        param.put("socketID", handlerID);
                        String route = param.getString("route");
                        eventBus.send("route:" + route, param, (AsyncResult<Message<JsonObject>> result) -> {
                            if (result.succeeded()) {
                                JsonObject jsonObject = result.result().body();
                                System.out.println("replies : "+jsonObject.toString());
                                eventBus.send(handlerID, Buffer.buffer().appendString(jsonObject.toString() + "\n"));
                            } else {

                                System.out.println(result.cause());
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
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
                    .listen(9999, next::handle);
        }
    }

    private void startDeployVerticle(AsyncResult<NetServer> netServer, Handler<AsyncResult<Void>> next, Future<Void> future) {
        if (netServer.succeeded()) {
            Map<String, Integer> controllers = new HashMap<String, Integer>();
            controllers.put("AuthController", 3);
            controllers.put("GameController", 3);
            controllers.put("CanvasController", 5);
            List<Future> futures = new ArrayList<>();
            for (Map.Entry<String, Integer> controller : controllers.entrySet()) {
                Future<String> future_ = Future.future();
                DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(controller.getValue());
                vertx.deployVerticle("org.huruggu.controllers." + controller.getKey(), options, future_.completer());
                futures.add(future_);
            }
            CompositeFuture.all(futures).setHandler(ar -> {
                if (ar.succeeded()) {
                    Rooms.reset();
                    next.handle(Future.succeededFuture());
                } else {
                    future.fail(ar.cause());
                }
            });
        } else {
            future.fail(netServer.cause());
        }

    }

    private void completeCacherCrayonEngine(AsyncResult<Void> complete, Future<Void> future) {
        if (complete.succeeded()) {
            future.complete();
            Helper.printDebug("Enjoy Yourself");
        } else {
            future.fail("Ops!");
            Helper.printDebug("Ops!");
        }
    }

}
