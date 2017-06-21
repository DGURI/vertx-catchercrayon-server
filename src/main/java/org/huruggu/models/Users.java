package org.huruggu.models;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.huruggu.engine.Models;

import java.util.List;

/**
 * Created by hwangdonghyeon on 2017. 6. 20..
 */
public class Users extends Models {

    public Integer id;
    public String identity;
    public String password;
    public String name;
    public Integer gender;
    public Integer icon;

    public static void login(String identity, String password, Handler<AsyncResult<Users>> aHandler) {
        Users user = new Users();
        Future<List<Users>> future = Future.future();
        user.where("identity", identity).where("password", password).get(1, (AsyncResult<List<Users>> result) -> {
            if (result.succeeded()) {
                aHandler.handle(Future.succeededFuture(result.result().get(0)));
            } else {
                aHandler.handle(Future.failedFuture("no user"));
            }
        });
    }

    public static void register(JsonObject userData, Handler<AsyncResult<Integer>> aHandler) {
        Users user = new Users();
        user.identity = userData.getString("identity");
        user.password = userData.getString("password");
        user.name = userData.getString("name");
        Future<Integer> future = Future.future();
        user.insert((AsyncResult<Integer> result) -> {
            if (result.succeeded()) {
                aHandler.handle(Future.succeededFuture(result.result()));
            } else {
                aHandler.handle(Future.failedFuture("insert error"));
            }
        });
    }
}
