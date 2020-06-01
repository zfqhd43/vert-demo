package com.qn.server.verticle;
import com.qn.server.utils.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author nick
 */
@Slf4j
@Component
public class ProductServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/products/:productID").handler(this::handleGetProduct);
        router.put("/products/:productID").handler(this::handleAddProduct);
        router.get("/products/").handler(this::handleListProducts);
        router.delete("/products/:productID").handler(this::handleDeleteProduct);
        router.patch("/products/:productID").handler(this::handlePatchProduct);
        vertx.createHttpServer().requestHandler(router).listen(8080);
//                .exceptionHandler(
//                throwable -> log.error("HTTPServer error happened: {}", throwable.toString())
//        );
    }

    private void getProductFailureHandler(RoutingContext failureRoutingContext){
        HttpServerResponse response = failureRoutingContext.response();
        response.end("something bad happened!");
    }

    private void handlePatchProduct(RoutingContext routingContext){
        String productId = routingContext.request().getParam("productID");
        log.info("productId used to patch product from request param: {}", productId);
        if (productId == null) {
            log.error("GET: the required productId is null!");
            sendError(400, routingContext.response());
            return;
        }
        JsonObject product = routingContext.getBodyAsJson();
        vertx.eventBus().<JsonObject>send(Constants.PATCH_PRODUCT_ADDRESS, product, asyncResult -> {
            log.info("result is: {}", asyncResult.result().body());
            if (asyncResult.succeeded()) {
                log.info("handle PATCH_PRODUCT_ADDRESS success!!");
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(String.valueOf(asyncResult.result().body()));
            } else {
                log.info("handle PATCH_PRODUCT_ADDRESS failed!!");
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void handleDeleteProduct(RoutingContext routingContext){
        Integer productId = Integer.valueOf(routingContext.request().getParam("productID"));
        log.info("productId from request param: {}", productId);
        vertx.eventBus()
                .<Integer>send(Constants.DELETE_PRODUCT_ADDRESS, productId, result -> {
                    log.info("result is: {}", result.result().body());
                    if (result.succeeded()) {
                        log.info("handle DELETE_PRODUCT_ADDRESS success!!");
                        routingContext.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(String.valueOf(result.result().body()));
                    } else {
                        log.info("handle DELETE_PRODUCT_ADDRESS failed!!");
                        routingContext.response().setStatusCode(500).end();
                    }
                });
    }

    private void handleGetProduct(RoutingContext routingContext) {
        String productId = routingContext.request().getParam("productID");
        if (productId == null) {
            log.error("GET: the required productId is null!");
            sendError(400, routingContext.response());
            return;
        }
        vertx.eventBus().<String>send(Constants.GET_ONE_PRODUCT_ADDRESS, productId, asyncResult -> {
            log.info("Got one product by productId: {}", asyncResult);
            if (asyncResult.succeeded()) {
                String body = asyncResult.result().body();
                if ("null".equals(body)){
                    log.info("No product found by the given productId: {}", productId);
                    routingContext.response().setStatusCode(404).end();
                }else {
                    log.info("handle GET_ONE_PRODUCT_ADDRESS success!!");
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(body);
                }

            } else {
                log.info("handle GET_ONE_PRODUCT_ADDRESS failed!!");
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void handleAddProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        if (productID == null) {
            log.error("PUT: the productId is null!");
            sendError(400, routingContext.response());
            return;
        }
        JsonObject product = routingContext.getBodyAsJson();
        vertx.eventBus().<JsonObject> send(Constants.ADD_PRODUCT_ADDRESS, product, asyncResult -> {
            log.info("added one product: {}", asyncResult);
            if (asyncResult.succeeded()) {
                log.info("handle ADD_PRODUCT_ADDRESS success!!");
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(asyncResult.result().body().toString());
            } else {
                log.info("handle ADD_PRODUCT_ADDRESS failed!!");
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void handleListProducts(RoutingContext routingContext) {
        EventBus eventBus = vertx.eventBus();
        eventBus.<String>send(Constants.ALL_PRODUCTS_ADDRESS, "", asyncResult -> {
            log.info("result is: {}", asyncResult.result().body());
            if (asyncResult.succeeded()) {
                log.info("handle  ALL_PRODUCTS_ADDRESS success!!");
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(asyncResult.result().body());
            } else {
                log.info("handle ALL_PRODUCTS_ADDRESS failed!!");
                routingContext.response().setStatusCode(500).end();
            }
        });
        log.info("ALL_PRODUCTS_ADDRESS Event already sent!");
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}