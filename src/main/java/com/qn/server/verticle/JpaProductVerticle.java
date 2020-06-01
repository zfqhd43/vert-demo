package com.qn.server.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qn.server.dto.ProductResult;
import com.qn.server.service.ProductService;
import com.qn.server.utils.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nick
 */
@Slf4j
@Component
public class JpaProductVerticle extends AbstractVerticle {

    private final static ObjectMapper objectMapper = Json.mapper;

    @Autowired
    private ProductService productService;

    private Handler<Message<String>> allProductsHandler(ProductService productService) {
        return msg -> vertx.<String>executeBlocking(future -> {
                    try {
                        future.complete(objectMapper.writeValueAsString(productService.getAllProduct()));
                        log.info("got all products from blocking...");
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize result");
                        future.fail(e);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        msg.reply(result.result());
                    } else {
                        msg.reply(result.cause().toString());
                    }
                });
    }

    private Handler<Message<String>> getOneProductHandler(ProductService productService) {
        return msg -> vertx.<String>executeBlocking(future -> {
                    try {
                        Integer productId = Integer.valueOf(msg.body());
                        log.info("productId from sender: {}", productId);
                        val productDTO = productService.findProductById(productId);
                        future.complete(objectMapper.writeValueAsString(productDTO));
                        log.info("got one product from blocking...");
                    } catch (Exception e) {
                        log.error("Failed to serialize result");
                        future.fail(e);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        msg.reply(result.result());
                    } else {
                        msg.reply(result.cause().toString());
                    }
                });
    }

    @Override
    public void start() throws Exception {
        super.start();
        EventBus eventBus = vertx.eventBus();
        eventBus.<String>consumer(Constants.ALL_PRODUCTS_ADDRESS).handler(allProductsHandler(productService));
        eventBus.<String>consumer(Constants.GET_ONE_PRODUCT_ADDRESS).handler(getOneProductHandler(productService));
        eventBus.<JsonObject>consumer(Constants.ADD_PRODUCT_ADDRESS).handler(addProductHandler(productService));
        eventBus.<Integer>consumer(Constants.DELETE_PRODUCT_ADDRESS).handler(deleteProductHandler(productService));
        eventBus.<JsonObject>consumer(Constants.PATCH_PRODUCT_ADDRESS).handler(patchProductHandler(productService));
    }

    private Handler<Message<Integer>> deleteProductHandler(ProductService productService) {

        return message -> vertx.<Integer>executeBlocking(
                future -> {
                    try {
                        Integer productId = message.body();
                        log.info("productId from sender: {}", productId);
                        future.complete(productService.deleteProduct(productId));
                        log.info("deleted one product from blocking...");
                    } catch (Exception e) {
                        log.error("Failed to serialize result");
                        future.fail(e);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        message.reply(result.result());
                    } else {
                        message.reply(result.cause().toString());
                    }
                }
        );
    }

    private Handler<Message<JsonObject>> patchProductHandler(ProductService productService) {

        return message -> vertx.<JsonObject>executeBlocking(
                future -> {
                    try {
                        JsonObject product = message.body();
                        log.info("product to be patched from sender: {}", product);
                        future.complete(JsonObject.mapFrom(productService.patchProduct(product.mapTo(ProductResult.class))));
                    } catch (Exception e) {
                        log.error("Failed to serialize result");
                        future.fail(e);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        message.reply(result.result());
                    } else {
                        message.reply(result.cause().toString());
                    }
                }
        );
    }

    private Handler<Message<JsonObject>> addProductHandler(ProductService productService) {

        return message -> vertx.<JsonObject>executeBlocking(
                future -> {
                    try {
                        JsonObject product = message.body();
                        log.info("product from sender: {}", product);
                        future.complete(JsonObject.mapFrom(productService.addProduct(product.mapTo(ProductResult.class))));
                        log.info("got one product from blocking...");
                    } catch (Exception e) {
                        log.error("Failed to serialize result");
                        future.fail(e);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        message.reply(result.result());
                    } else {
                        message.reply(result.cause().toString());
                    }
                }
        );
    }

    private Handler<Message<String>> getAllService() {
        return msg -> vertx.<String>executeBlocking(future -> {
            log.info("try to get json.....");
            try {
                log.info("get json success..");
                future.complete(new JsonObject().put("name", "wade").toString());
            } catch (Exception e) {
                log.info("Failed to serialize result");
                future.fail(e);
            }
        }, result -> {
            if (result.succeeded()) {
                msg.reply(result.result());
            } else {
                msg.reply(result.cause()
                        .toString());
            }
        });
    }
}