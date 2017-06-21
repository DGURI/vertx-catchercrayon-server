package org.huruggu.controllers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by hwangdonghyeon on 2017. 6. 21..
 */
public class GameController extends AbstractVerticle {

    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        startFuture.complete();
    }
}
