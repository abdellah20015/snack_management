package com.example.starter.Services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.file.FileSystem;
import io.vertx.core.eventbus.Message;
import com.example.starter.constants.Services;
import java.util.UUID;

public class FileVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    // Register handler for file upload
    vertx.eventBus().consumer(Services.FILE_UPLOAD, this::handleFileUpload);
    startPromise.complete();
  }

  private void handleFileUpload(Message<JsonObject> message) {
    JsonObject body = message.body();
    String fileName = body.getString("fileName");
    String uploadedFilePath = body.getString("uploadedFilePath");

    // Générer un nom de fichier unique
    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
    String destinationPath = "uploads/" + uniqueFileName;

    FileSystem fs = vertx.fileSystem();

    // Déplacer le fichier vers le dossier de destination
    fs.move(uploadedFilePath, destinationPath, moveResult -> {
      if (moveResult.succeeded()) {
        JsonObject response = new JsonObject()
          .put("success", true)
          .put("message", "File uploaded successfully")
          .put("fileName", uniqueFileName)
          .put("path", destinationPath);

        message.reply(response);
      } else {
        JsonObject error = new JsonObject()
          .put("success", false)
          .put("message", "Failed to process file: " + moveResult.cause().getMessage());

        message.fail(500, error.encode());
      }
    });
  }
}
