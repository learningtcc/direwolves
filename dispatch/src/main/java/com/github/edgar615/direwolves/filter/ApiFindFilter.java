package com.github.edgar615.direwolves.filter;

import com.github.edgar615.direwolves.core.apidiscovery.ApiDiscovery;
import com.github.edgar615.direwolves.core.apidiscovery.ApiDiscoveryOptions;
import com.github.edgar615.direwolves.core.apidiscovery.ApiFinder;
import com.github.edgar615.direwolves.core.definition.ApiDefinition;
import com.github.edgar615.direwolves.core.dispatch.ApiContext;
import com.github.edgar615.direwolves.core.dispatch.Filter;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * 该filter根据请求从API路由注册表中读取到对应的API定义.
 * <p>
 * 该filter可以接受下列的配置参数
 * <pre>
 * </pre>
 * <p>
 * <b>该filter应该在所有的filter之前执行</b>如果未找到对应的API定义，直接返回对应的异常。
 * 该filter的order=-2147482648
 * Created by edgar on 17-1-4.
 */
public class ApiFindFilter implements Filter {

  private final Vertx vertx;

  private final ApiFinder apiFinder;

  public ApiFindFilter(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
//    String namespace = config.getString("namespace", "api-gateway");
    JsonObject dicoveryConfig = config.getJsonObject("api.discovery", new JsonObject());
    ApiDiscovery discovery = ApiDiscovery.create(vertx,
                                                 new ApiDiscoveryOptions(dicoveryConfig));
    this.apiFinder = ApiFinder.create(vertx, discovery);
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public int order() {
    return Integer.MIN_VALUE + 1000;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    return apiContext.apiDefinition() == null;
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    apiFinder.getDefinitions(apiContext.method().name(), apiContext.path(), ar -> {
      if (ar.failed()) {
        failed(completeFuture, apiContext.id(), "api.tripped", ar.cause());
        return;
      }
      try {
        List<ApiDefinition> apiDefinitions = ar.result();
        ApiDefinition apiDefinition = matchApi(apiDefinitions);
        completeFuture.complete(apiContext.setApiDefinition(apiDefinition));
        return;
      } catch (SystemException e) {
        e.set("details", String.format("Undefined Api %s:%s",
                                       apiContext.method().name(),
                                       apiContext.path()));
        failed(completeFuture, apiContext.id(), "api.tripped", e);
        return;
      } catch (Exception e) {
        failed(completeFuture, apiContext.id(), "api.tripped", e);
        return;
      }
    });
  }

  private ApiDefinition matchApi(List<ApiDefinition> apiDefinitions) {
    if (apiDefinitions.isEmpty()) {//没有API
      throw SystemException.create(DefaultErrorCode.RESOURCE_NOT_FOUND);
    }
    if (apiDefinitions.size() != 1) {//有多个异常
      throw SystemException.create(DefaultErrorCode.CONFLICT);
    }
    return apiDefinitions.get(0);
  }

}
