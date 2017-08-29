package com.edgar.direwolves.http.loadbalance;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * Created by Edgar on 2017/7/31.
 *
 * @author Edgar  Date 2017/7/31
 */
public interface LoadBalance {

  void chooseServer(String service, Handler<AsyncResult<Record>> resultHandler);

  /**
   * config的配置：
   * "strategy": {
   * "user": "random",
   * "device": "round_robin"
   * }
   *
   * @param serviceFinder
   * @param config
   * @return
   */
  static LoadBalance create(ServiceFinder serviceFinder, JsonObject config) {
    return new LoadBalanceImpl(serviceFinder, config);
  }
}
