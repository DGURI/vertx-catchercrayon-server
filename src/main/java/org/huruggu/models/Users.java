package org.huruggu.models;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.sql.SQLConnection;
import org.huruggu.dbs.Jdbc;
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
        user.gender = userData.getInteger("gender");
        user.icon = userData.getInteger("icon");
        Future<Integer> future = Future.future();
        user.insert((AsyncResult<Integer> result) -> {
            if (result.succeeded()) {
                aHandler.handle(Future.succeededFuture(result.result()));
            } else {
                aHandler.handle(Future.failedFuture("insert error"));
            }
        });
    }

    public static void duplicate(String identity, Handler<AsyncResult<String>> aHandler) {
        Users user = new Users();
        user.where("identity", identity).get(1, (AsyncResult<List<Users>> result) -> {
            if (result.succeeded()) {
                aHandler.handle(Future.failedFuture("duplicated"));
            } else{
                aHandler.handle(Future.succeededFuture("no duplicate"));
            }
        });
    }

    public static void get(String identity, Handler<AsyncResult<Users>> aHandler) {
        Users user = new Users();
        user.select("identity, name, gender, icon").where("identity", identity).get(1, (AsyncResult<List<Users>> result) -> {
            if (result.succeeded()) {
                aHandler.handle(Future.succeededFuture(result.result().get(0)));
            } else {
                aHandler.handle(Future.failedFuture("no user"));
            }
        });
    }
}
