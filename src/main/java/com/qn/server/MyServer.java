package com.qn.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * @Description:
 * @Author: fuqiang.zhao@luckincoffee.com
 * @Date: 20/6/1 15:55
 */
public class MyServer extends AbstractVerticle {
    @Override
    public void start() {

        Router router = Router.router(vertx);

        router.route().handler(this::queryHandler);
        vertx.createHttpServer().requestHandler(router::accept)
                .listen(8080);
        System.out.println("MyServer启动成功");
    }

    private void queryHandler(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content_type", "application/json")
                .end("hello world");
    }

}
