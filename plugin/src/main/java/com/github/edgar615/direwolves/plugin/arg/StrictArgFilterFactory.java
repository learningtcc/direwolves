package com.github.edgar615.direwolves.plugin.arg;

import com.github.edgar615.direwolves.core.dispatch.Filter;
import com.github.edgar615.direwolves.core.dispatch.FilterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by edgar on 16-10-28.
 */
public class StrictArgFilterFactory implements FilterFactory {

  @Override
  public String name() {
    return StrictArgFilter.class.getSimpleName();
  }

  @Override
  public Filter create(Vertx vertx, JsonObject config) {
    return new StrictArgFilter(config);
  }
}
