package org.huruggu.controllers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by hwangdonghyeon on 2017. 6. 21..
 */
public abstract class Controller extends AbstractVerticle {
    public abstract void start(Future<Void> future);
}
