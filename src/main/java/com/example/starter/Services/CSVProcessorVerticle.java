package com.example.starter.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.eventbus.Message;
import com.example.starter.constants.Services;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class CSVProcessorVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.eventBus().consumer(Services.PROCESS_CSV, this::processCSV);
    startPromise.complete();
  }

  private void processCSV(Message<JsonObject> message) {
    String filePath = message.body().getString("filePath");

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      String[] headers = br.readLine().split(",");
      List<JsonObject> products = new ArrayList<>();

      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        JsonObject product = new JsonObject();

        for (int i = 0; i < headers.length; i++) {
          String value = values[i].trim();
          // Convert price to number
          if (headers[i].equals("price")) {
            product.put(headers[i], Double.parseDouble(value));
          } else {
            product.put(headers[i], value);
          }
        }
        products.add(product);
      }

      // Insert all products into MongoDB
      for (JsonObject product : products) {
        JsonObject insertMessage = new JsonObject()
          .put("collection", "products")
          .put("query", product);

        vertx.eventBus().request(Services.DB_INSERT, insertMessage, reply -> {
          if (reply.failed()) {
            System.err.println("Failed to insert product: " + reply.cause().getMessage());
          }
        });
      }

      message.reply(new JsonObject()
        .put("success", true)
        .put("message", "CSV processed successfully")
        .put("productsImported", products.size()));

    } catch (Exception e) {
      message.fail(500, "Failed to process CSV: " + e.getMessage());
    } finally {
      try {
        Files.deleteIfExists(Paths.get(filePath));
      } catch (Exception e) {
        System.err.println("Failed to delete temporary file: " + e.getMessage());
      }
    }
  }
}
