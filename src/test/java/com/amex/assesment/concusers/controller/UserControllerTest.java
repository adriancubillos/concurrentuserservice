package com.amex.assesment.concusers.controller;

import com.amex.assesment.concusers.ConcUsersApplication;
import com.amex.assesment.concusers.datastore.UserDatastore;
import com.amex.assesment.concusers.model.User;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class UserControllerTest {

    private static WebClient webClient;
    private static ConfigurableApplicationContext context;
    private UserDatastore userDatastore;

    @BeforeAll
    static void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        webClient = WebClient.create(vertx);
        context = SpringApplication.run(ConcUsersApplication.class);
        vertx.deployVerticle(context.getBean(com.amex.assesment.concusers.verticles.MainVerticle.class),
                testContext.succeeding(id -> testContext.completeNow()));
    }

    @BeforeEach
    void setup() {
        userDatastore = context.getBean(UserDatastore.class);
        userDatastore.clear();
    }

    @AfterAll
    static void close_context(Vertx vertx, VertxTestContext testContext) {
        context.close();
        vertx.close(testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    void testCreateAndGetUser(VertxTestContext testContext) {
        User user = new User(0, "Test User", "test@example.com");

        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.json(User.class))
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(201, response.statusCode());
                        User createdUser = response.body();
                        assertEquals("Test User", createdUser.getName());

                        webClient.get(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                .as(BodyCodec.json(User.class))
                                .send(testContext.succeeding(getResponse -> {
                                    testContext.verify(() -> {
                                        assertEquals(200, getResponse.statusCode());
                                        assertEquals("Test User", getResponse.body().getName());
                                        testContext.completeNow();
                                    });
                                }));
                    });
                }));
    }

    @Test
    void testCreateUserWithInvalidData(VertxTestContext testContext) {
        User user = new User(0, "", "invalid-email");

        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.string())
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(400, response.statusCode());
                        testContext.completeNow();
                    });
                }));
    }

    @Test
    void testCreateUserWithDuplicateEmail(VertxTestContext testContext) {
        User user = new User(0, "Test User", "test@example.com");
        userDatastore.save(user);

        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.string())
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(409, response.statusCode());
                        testContext.completeNow();
                    });
                }));
    }

    @Test
    void testGetUserByIdWhenUserExists(VertxTestContext testContext) {
        User user = new User(0, "Test User", "test@example.com");
        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.json(User.class))
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(201, response.statusCode());
                        User createdUser = response.body();

                        webClient.get(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                .as(BodyCodec.json(User.class))
                                .send(testContext.succeeding(getResponse -> {
                                    testContext.verify(() -> {
                                        assertEquals(200, getResponse.statusCode());
                                        assertEquals("Test User", getResponse.body().getName());
                                        testContext.completeNow();
                                    });
                                }));
                    });
                }));
    }

    @Test
    void testGetUserByIdWhenUserDoesNotExist(VertxTestContext testContext) {
        webClient.get(8080, "localhost", "/api/v1/users/999")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(404, response.statusCode());
                        testContext.completeNow();
                    });
                }));
    }

    @Test
    void testUpdateUserWhenUserExists(VertxTestContext testContext) {
        User user = new User(0, "Original Name", "original@example.com");
        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.json(User.class))
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(201, response.statusCode());
                        User createdUser = response.body();

                        User userDetails = new User(0, "Updated Name", "updated@example.com");
                        webClient.put(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                .as(BodyCodec.json(User.class))
                                .sendJson(userDetails, testContext.succeeding(updateResponse -> {
                                    testContext.verify(() -> {
                                        assertEquals(200, updateResponse.statusCode());
                                        assertEquals("Updated Name", updateResponse.body().getName());

                                        webClient.get(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                                .as(BodyCodec.json(User.class))
                                                .send(testContext.succeeding(getResponse -> {
                                                    testContext.verify(() -> {
                                                        assertEquals(200, getResponse.statusCode());
                                                        assertEquals("Updated Name", getResponse.body().getName());
                                                        testContext.completeNow();
                                                    });
                                                }));
                                    });
                                }));
                    });
                }));
    }

    @Test
    void testUpdateUserWhenUserDoesNotExist(VertxTestContext testContext) {
        User userDetails = new User(0, "Updated Name", "updated@example.com");
        webClient.put(8080, "localhost", "/api/v1/users/999")
                .as(BodyCodec.string())
                .sendJson(userDetails, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(404, response.statusCode());
                        testContext.completeNow();
                    });
                }));
    }

    @Test
    void testDeleteUserWhenUserExists(VertxTestContext testContext) {
        User user = new User(0, "To Be Deleted", "delete@example.com");
        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.json(User.class))
                .sendJson(user, testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(201, response.statusCode());
                        User createdUser = response.body();

                        webClient.delete(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                .send(testContext.succeeding(deleteResponse -> {
                                    testContext.verify(() -> {
                                        assertEquals(204, deleteResponse.statusCode());

                                        webClient.get(8080, "localhost", "/api/v1/users/" + createdUser.getId())
                                                .as(BodyCodec.string())
                                                .send(testContext.succeeding(getResponse -> {
                                                    testContext.verify(() -> {
                                                        assertEquals(404, getResponse.statusCode());
                                                        testContext.completeNow();
                                                    });
                                                }));
                                    });
                                }));
                    });
                }));
    }

    @Test
    void testDeleteUserWhenUserDoesNotExist(VertxTestContext testContext) {
        webClient.delete(8080, "localhost", "/api/v1/users/999")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(404, response.statusCode());
                        testContext.completeNow();
                    });
                }));
    }

    @Test
    void testGetAllUsersWhenUsersExist(VertxTestContext testContext) {
        User user1 = new User(0, "User 1", "user1@example.com");
        User user2 = new User(0, "User 2", "user2@example.com");
        webClient.post(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.json(User.class))
                .sendJson(user1, testContext.succeeding(response1 -> {
                    testContext.verify(() -> {
                        assertEquals(201, response1.statusCode());

                        webClient.post(8080, "localhost", "/api/v1/users")
                                .as(BodyCodec.json(User.class))
                                .sendJson(user2, testContext.succeeding(response2 -> {
                                    testContext.verify(() -> {
                                        assertEquals(201, response2.statusCode());

                                        webClient.get(8080, "localhost", "/api/v1/users")
                                                .as(BodyCodec.jsonArray())
                                                .send(testContext.succeeding(getResponse -> {
                                                    testContext.verify(() -> {
                                                        assertEquals(200, getResponse.statusCode());
                                                        assertEquals(2, getResponse.body().size());
                                                        testContext.completeNow();
                                                    });
                                                }));
                                    });
                                }));
                    });
                }));
    }

    @Test
    void testGetAllUsersWhenNoUsersExist(VertxTestContext testContext) {
        webClient.get(8080, "localhost", "/api/v1/users")
                .as(BodyCodec.jsonArray())
                .send(testContext.succeeding(response -> {
                    testContext.verify(() -> {
                        assertEquals(200, response.statusCode());
                        assertEquals(0, response.body().size());
                        testContext.completeNow();
                    });
                }));
    }
}
