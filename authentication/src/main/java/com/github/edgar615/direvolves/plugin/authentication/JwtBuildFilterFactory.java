package com.github.edgar615.direvolves.plugin.authentication;

import com.github.edgar615.direwolves.core.dispatch.Filter;
import com.github.edgar615.direwolves.core.dispatch.FilterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * JwtBuildFilter的工厂类.
 * Created by edgar on 16-12-11.
 */
public class JwtBuildFilterFactory implements FilterFactory {
  @Override
  public String name() {
    return JwtBuildFilter.class.getSimpleName();
  }

  @Override
  public Filter create(Vertx vertx, JsonObject config) {
    return new JwtBuildFilter(vertx, config);
  }
}