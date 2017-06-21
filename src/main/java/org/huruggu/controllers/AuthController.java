package org.huruggu.controllers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.huruggu.models.Users;

import java.util.Map;

/**
 * Created by hwangdonghyeon on 2017. 6. 21..
 */
public class AuthController extends Controller {
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("route:auth.login", this::login);
        eventBus.consumer("route:auth.register", this::register);
        startFuture.complete();
    }

    private void login(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject();
        response.put("route", "auth.login");
        String identity = request.getString("identity").trim();
        String password = request.getString("password").trim();
        if (identity.isEmpty() || password.isEmpty()) {
            response.put("status", "error-empty");
            message.reply(response);
            return;
        }
        Users.login(identity, password, (AsyncResult<Users> result) -> {
            if (result.succeeded()) {
                Users user = result.result();
                response.put("status", "success");
                response.put("user", user.toJsonObject());
            } else {
                response.put("status", "error-notFound");
            }
            message.reply(response);
        });
    }

    private void register(Message<JsonObject> message) {
        JsonObject request = message.body();
        JsonObject response = new JsonObject();
        response.put("route", "auth.register");
        JsonObject user = request.getJsonObject("user");
        user.put("identity", user.getString("identity").trim());
        user.put("password", user.getString("password").trim());
        user.put("name", user.getString("name").trim());
        Users.register(user, (AsyncResult<Integer> resulit)-> {
            if(resulit.succeeded()) {
                response.put("status", "success");
            } else {
                response.put("status", "error-database");
            }
            message.reply(response);
        });
    }
}
