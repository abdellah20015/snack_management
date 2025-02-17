package com.example.starter.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class Conf {
  private static final String CONNECTION_STRING = System.getenv("MONGO_CONNECTION_STRING");
  private static final String DATABASE_NAME = System.getenv("MONGO_DATABASE_NAME");


  public static MongoClient createMongoClient(Vertx vertx) {
    JsonObject config = new JsonObject()
      .put("connection_string", CONNECTION_STRING)
      .put("db_name", DATABASE_NAME);

      System.out.println("Connecting to MongoDB with connection string: " + CONNECTION_STRING);
      System.out.println("Using database: " + DATABASE_NAME);
    return MongoClient.createShared(vertx, config);
  }
}
