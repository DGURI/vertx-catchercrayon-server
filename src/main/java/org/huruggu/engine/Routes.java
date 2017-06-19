package org.huruggu.engine;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by hwangdonghyeon on 2017. 6. 18..
 */
public class Routes extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("route.login", (Message<JsonObject> message) -> {
            JsonObject param = message.body();
            System.out.println(param.getString("route") + " START");
            System.out.println("id : " + param.getString("identity"));
            System.out.println("pw : " + param.getString("password"));
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("status", param.getString("route") + " END");
            message.reply(jsonObject);
        });
    }
}
