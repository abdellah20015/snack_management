package com.example.starter;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import com.example.starter.Services.*;
import com.example.starter.config.*;
import com.example.starter.constants.Services;

import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.openapi.contract.OpenAPIContract;


  public class MainVerticle extends AbstractVerticle {
  private static final Integer PORT = 8888;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String path = "src/main/api/openapi.json";
    Conf.createMongoClient(vertx);
    OpenAPIContract.from(vertx, path)
    .onSuccess(contract -> {
      // Create a router builder
      RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract, RequestExtractor.withBodyHandler());
      // Create a session store
      LocalSessionStore sessionStore = LocalSessionStore.create(vertx);
      // Create a session handler
      SessionHandler sessionHandler = SessionHandler.create(sessionStore);

      // Mount the session handler
      routerBuilder.rootHandler(sessionHandler);

      // Mount the CORS handler
      routerBuilder.rootHandler(
        CorsHandler.create().addOrigin("*")
          .addOrigin("*")
          .allowedMethod(HttpMethod.GET)
          .allowedMethod(HttpMethod.POST)
          .allowedMethod(HttpMethod.OPTIONS)
          .allowedMethod(HttpMethod.PATCH)
          .allowedMethod(HttpMethod.PUT)
          .allowedMethod(HttpMethod.DELETE)
          .allowedHeader("Authorization")
          .allowedHeader("Content-Type")
          .allowCredentials(true)
      );

      // Mount the body handler
      routerBuilder.rootHandler(
       BodyHandler.create()
         .setUploadsDirectory("uploads")
         .setBodyLimit(50 * 1024 * 1024)
      );


      //Add handlers
      routerBuilder.getRoute("uploadProducts").addHandler(this::handleFileUpload);
      routerBuilder.getRoute("getSnackById").addHandler(this::handleGetSnackById);
      routerBuilder.getRoute("getAllProducts").addHandler(this::handleGetAllProducts);

      // Create a router
      Router router = routerBuilder.createRouter();

      //  create a static handler for the uploads directory
      router.route("/uploads/*").handler(StaticHandler.create("uploads"));

      //  create http server and listen on port 8888
      vertx.createHttpServer().requestHandler(router).listen(PORT)
      .onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port " + PORT);
        } else {
          startPromise.fail(http.cause());
        }
      });
    })
    .onFailure(err -> {
      startPromise.fail(err);
      System.err.println("Failed to load OpenAPI contract: " + err.getMessage());
    });
  }

  private void handleFileUpload(RoutingContext ctx) {
    ctx.fileUploads().forEach(fileUpload -> {
      JsonObject fileInfo = new JsonObject()
        .put("fileName", fileUpload.fileName())
        .put("uploadedFilePath", fileUpload.uploadedFileName());

      vertx.eventBus().<JsonObject>request(
        Services.FILE_UPLOAD,
        fileInfo,
        reply -> {
          if (reply.succeeded()) {
            JsonObject response = reply.result().body();
            String processedFilePath = response.getString("path");

            vertx.eventBus().<JsonObject>request(
              Services.PROCESS_CSV,
              new JsonObject().put("filePath", processedFilePath),
              csvReply -> {
                if (csvReply.succeeded()) {
                  ctx.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(200)
                    .end(csvReply.result().body().encode());
                } else {
                  ctx.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(500)
                    .end(new JsonObject()
                      .put("error", "CSV processing failed")
                      .put("message", csvReply.cause().getMessage())
                      .encode());
                }
              });
          } else {
            ctx.response()
              .putHeader("content-type", "application/json")
              .setStatusCode(500)
              .end(new JsonObject()
                .put("error", "Upload failed")
                .put("message", reply.cause().getMessage())
                .encode());
          }
        });
    });
  }

  private void handleGetSnackById(RoutingContext ctx) {
    String snackId = ctx.pathParam("snackId");

    vertx.eventBus().<JsonObject>request(
      Services.GET_SNACK_BY_ID,
      snackId,
      reply -> {
        if (reply.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(reply.result().body().encode());
        } else {
          int statusCode = reply.cause().getMessage().contains("non trouvé") ? 404 : 500;
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(statusCode)
            .end(new JsonObject()
              .put("error", reply.cause().getMessage())
              .encode());
        }
      });
  }

  private void handleGetAllProducts(RoutingContext ctx) {
    JsonObject query = new JsonObject();
    if (ctx.queryParams().contains("category")) {
      query.put("category", ctx.queryParam("category").get(0));
    }

    vertx.eventBus().<JsonObject>request(
      Services.GET_ALL_PRODUCTS,
      query,
      reply -> {
        if (reply.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(reply.result().body().encode());
        } else {
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(500)
            .end(new JsonObject()
              .put("error", "Erreur lors de la récupération des produits")
              .put("message", reply.cause().getMessage())
              .encode());
        }
      });
  }

public static void main(String[] args) {
  Vertx vertx = Vertx.vertx();
  vertx.deployVerticle(new MainVerticle());
  vertx.deployVerticle(new FileVerticle());
  vertx.deployVerticle(new CSVProcessorVerticle());
  vertx.deployVerticle(new Db());
  vertx.deployVerticle(new SnackVerticle());
  vertx.deployVerticle(new ProductVerticle());
}

}


