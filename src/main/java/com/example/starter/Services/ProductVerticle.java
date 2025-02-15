package com.example.starter.Services;

import com.example.starter.constants.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ProductVerticle extends AbstractVerticle {
  private static final String COLLECTION = "products";

  @Override
  public void start() {
      vertx.eventBus().consumer(Services.GET_ALL_PRODUCTS, message -> {
          JsonObject body = (JsonObject) message.body();
          JsonObject query = new JsonObject()
              .put("collection", COLLECTION)
              .put("query", new JsonObject());


          if (body != null && body.getString("category") != null) {
              query.getJsonObject("query").put("category", body.getString("category"));
          }

          vertx.eventBus().<JsonObject>request(
              Services.DB_FIND,
              query,
              reply -> {
                  if (reply.succeeded()) {
                      JsonObject response = reply.result().body();
                      if ("success".equals(response.getString("status"))) {
                          message.reply(new JsonObject().put("products", response.getJsonArray("data")));
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
