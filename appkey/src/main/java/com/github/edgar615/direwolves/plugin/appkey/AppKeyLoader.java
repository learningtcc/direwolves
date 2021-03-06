package com.github.edgar615.direwolves.plugin.appkey;

import com.github.edgar615.util.vertx.cache.CacheLoader;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/10/27.
 */
class AppKeyLoader implements CacheLoader<String, JsonObject> {
  private final Vertx vertx;

  private final JsonObject config;

  private final Map<String, JsonObject> localAppKeys = new HashMap<>();

  private final String notExistsKey;

  private final String prefix;

  AppKeyLoader(Vertx vertx, String prefix, JsonObject config) {
    this.vertx = vertx;
    this.prefix = prefix;
    this.config = config;
    this.notExistsKey = config.getString("notExistsKey", UUID.randomUUID().toString());
    JsonArray jsonArray = config.getJsonArray("data", new JsonArray());
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject = config.getJsonArray("data").getJsonObject(i);
      String appKey = jsonObject.getString("appKey");
      String appSecret = jsonObject.getString("appSecret");
      if (appKey != null && appSecret != null) {
        localAppKeys.put(appKey, jsonObject);
      }
    }
  }

  @Override
  public void load(String key, Handler<AsyncResult<JsonObject>> handler) {
    String appkey = key.substring(prefix.length());
    JsonObject notExists = new JsonObject().put(notExistsKey, notExistsKey);
    if (localAppKeys.containsKey(appkey)) {
      handler.handle(Future.succeededFuture(localAppKeys.get(appkey)));
      return;
    }
    if (config.getValue("url") instanceof String) {
      httpLoader(appkey, handler, notExists);
      return;
    }
    handler.handle(Future.succeededFuture(notExists));
  }

  private void httpLoader(String appKey, Handler<AsyncResult<JsonObject>> handler, JsonObject notExists) {
    try {
      int port = config.getInteger("port", 80);
      String path = config.getString("url", "/");
      if (path.contains("?")) {
        path = path + "&appKey=" + appKey;
      } else {
        path = path + "?appKey=" + appKey;
      }
      vertx.createHttpClient().get(port, "127.0.0.1", path, response -> {
        if (response.statusCode() >= 400) {
          handler.handle(Future.succeededFuture(notExists));
        } else {
          response.bodyHandler(body -> {
            handler.handle(Future.succeededFuture(body.toJsonObject()));
          });
        }
      }).exceptionHandler(e -> handler.handle(Future.succeededFuture(notExists)))
              .end();
    } catch (Exception e) {
      handler.handle(Future.succeededFuture(notExists));
    }
  }
}
