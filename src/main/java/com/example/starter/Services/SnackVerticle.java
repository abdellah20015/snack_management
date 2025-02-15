package com.example.starter.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import com.example.starter.constants.Services;

public class SnackVerticle extends AbstractVerticle {
    private static final String COLLECTION = "products";

    @Override
    public void start() {
        vertx.eventBus().consumer(Services.GET_SNACK_BY_ID, message -> {
            String snackId = (String) message.body();

            JsonObject query = new JsonObject()
                .put("collection", COLLECTION)
                .put("query", new JsonObject().put("snackId", snackId));

            vertx.eventBus().<JsonObject>request(
                Services.DB_FIND_ONE,
                query,
                reply -> {
                    if (reply.succeeded()) {
                        JsonObject response = reply.result().body();
                        if ("success".equals(response.getString("status"))) {
                            message.reply(response.getJsonObject("data"));
                        } else {
                            message.fail(response.getInteger("code", 500),
                                       response.getString("message", "Erreur interne"));
                        }
                    } else {
                        message.fail(500, reply.cause().getMessage());
                    }
                });
        });
    }
}
