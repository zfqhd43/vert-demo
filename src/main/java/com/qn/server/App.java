package com.qn.server;

import io.vertx.core.Vertx;

/**
 * @Description:
 * @Author: fuqiang.zhao@luckincoffee.com
 * @Date: 20/6/1 15:59
 */
public class App {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(MyServer.class.getName());
    }
}
