package com.qn.server;

/**
 * @Description:
 * @Author: fuqiang.zhao@luckincoffee.com
 * @Date: 20/6/1 16:27
 */
import com.qn.server.verticle.JpaProductVerticle;
import com.qn.server.verticle.ProductServerVerticle;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class VertxspringbootApplication {

    @Autowired
    private ProductServerVerticle productServerVerticle;

    @Autowired
    private JpaProductVerticle jpaProductVerticle;

    public static void main(String[] args) {
        SpringApplication.run(VertxspringbootApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(productServerVerticle);
        vertx.deployVerticle(jpaProductVerticle);
        vertx.exceptionHandler(throwable -> log.error("exception happened: {}", throwable.toString()));
        log.info("verticle deployed!!");
    }

}