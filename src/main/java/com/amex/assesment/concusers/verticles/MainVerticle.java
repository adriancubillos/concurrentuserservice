package com.amex.assesment.concusers.verticles;

import com.amex.assesment.concusers.verticles.handlers.UserHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class MainVerticle extends AbstractVerticle {

    private final UserHandler userHandler;

    public MainVerticle(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // Failure handler for decoding errors
        router.route().failureHandler(ctx -> {
            if (ctx.failure() instanceof io.vertx.core.json.DecodeException) {
                ctx.response().setStatusCode(400).end("Invalid JSON format");
            } else {
                ctx.next();
            }
        });

        router.get("/users").handler(userHandler::getAllUsers);
        router.post("/users").handler(userHandler::createUser);
        router.get("/users/:id").handler(userHandler::getUserById);
        router.put("/users/:id").handler(userHandler::updateUser);
        router.put("/users/:id/email").handler(userHandler::updateUserEmail);
        router.delete("/users/:id").handler(userHandler::deleteUser);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8080");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}
