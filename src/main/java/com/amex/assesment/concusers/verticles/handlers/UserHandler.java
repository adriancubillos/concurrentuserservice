package com.amex.assesment.concusers.verticles.handlers;

import com.amex.assesment.concusers.exception.DuplicateUserException;
import com.amex.assesment.concusers.exception.UserNotFoundException;
import com.amex.assesment.concusers.model.User;
import com.amex.assesment.concusers.service.UserService;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserHandler {

    private final UserService userService;
    private final Validator validator;

    public UserHandler(UserService userService, Validator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    public void createUser(RoutingContext context) {
        try {
            final User user = context.body().asPojo(User.class);

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                String errors = violations.stream().map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                context.response().setStatusCode(400).end(errors);
                return;
            }

            User createdUser = userService.createUser(user);
            context.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(createdUser));
        } catch (DuplicateUserException e) {
            context.response().setStatusCode(409).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }

    public void getUserById(RoutingContext context) {
        try {
            long id = Long.parseLong(context.pathParam("id"));
            User user = userService.getUserById(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(user));
        } catch (UserNotFoundException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }

    public void getAllUsers(RoutingContext context) {
        try {
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(userService.getAllUsers()));
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }

    public void updateUser(RoutingContext context) {
        try {
            long id = Long.parseLong(context.pathParam("id"));
            final User userDetails = context.body().asPojo(User.class);

            Set<ConstraintViolation<User>> violations = validator.validate(userDetails);
            if (!violations.isEmpty()) {
                String errors = violations.stream().map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                context.response().setStatusCode(400).end(errors);
                return;
            }

            User updatedUser = userService.updateUser(id, userDetails);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(updatedUser));
        } catch (UserNotFoundException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }

    public void updateUserEmail(RoutingContext context) {
        try {
            long id = Long.parseLong(context.pathParam("id"));
            String email = context.body().asJsonObject().getString("email");
            User updatedUser = userService.updateUserEmail(id, email);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(updatedUser));
        } catch (UserNotFoundException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }

    public void deleteUser(RoutingContext context) {
        try {
            long id = Long.parseLong(context.pathParam("id"));
            userService.deleteUser(id);
            context.response().setStatusCode(204).end();
        } catch (UserNotFoundException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(e.getMessage());
        }
    }
}
