package com.github.edgar615.direwolves.verticle;

import com.google.common.base.Strings;

import com.github.edgar615.direwolves.core.apidiscovery.ApiDiscovery;
import com.github.edgar615.direwolves.core.apidiscovery.ApiDiscoveryOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从文件读取API定义.
 *
 * @author Edgar  Date 2016/9/13
 */
public class FileApiDiscoveryVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileApiDiscoveryVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    LOGGER.info("deploy FileApiDiscoveryVerticle");
    String path = null;
    if (config().getValue("path") instanceof String) {
      path = config().getString("path");
    }
    if (Strings.isNullOrEmpty(path)) {
      LOGGER.error("deploy FileApiDiscoveryVerticle failed,api path is null");
      startFuture.fail("api path is null");
      return;
    }
    JsonObject discoveryConfig = null;
    if (config().getValue("api.discovery") instanceof JsonObject) {
      discoveryConfig = config().getJsonObject("api.discovery");
    }
    if (discoveryConfig == null) {
      LOGGER.error("deploy FileApiDiscoveryVerticle failed,api.discovery is null");
      startFuture.fail("api.discovery is null");
      return;
    }
    ApiDiscovery discovery
            = ApiDiscovery.create(vertx,
                                  new ApiDiscoveryOptions(
                                          config().getJsonObject("api.discovery")));
    JsonObject importConfig = new JsonObject()
            .put("path", path);
    discovery.registerImporter(new FileApiImporter(), importConfig, ar -> {
      if (ar.succeeded()) {
        LOGGER.info("deploy FileApiDiscoveryVerticle succeed");
        startFuture.complete();
      } else {
        LOGGER.error("deploy FileApiDiscoveryVerticle failed", ar.cause());
        startFuture.fail(ar.cause());
      }
    });

  }

}
