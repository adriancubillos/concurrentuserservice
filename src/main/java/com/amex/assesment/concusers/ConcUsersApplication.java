package com.amex.assesment.concusers;

import com.amex.assesment.concusers.verticles.MainVerticle;
import io.vertx.core.Vertx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ConcUsersApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ConcUsersApplication.class, args);
        MainVerticle mainVerticle = context.getBean(MainVerticle.class);

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(mainVerticle);
    }

}
