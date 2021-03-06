package com.github.edgar615.direwolves.plugin.transformer;

import com.google.common.collect.Multimap;

import com.github.edgar615.direwolves.core.dispatch.ApiContext;
import com.github.edgar615.direwolves.core.rpc.RpcRequest;
import com.github.edgar615.direwolves.core.rpc.eventbus.EventbusRpcRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 将RpcRequest中的请求头，请求参数，请求体按照RequestTransformerPlugin中的配置处理.
 * <p>
 * 执行的顺序为: remove add
 * <p>
 * 该filter的order=15000
 * Created by edgar on 16-9-20.
 */
public class EventbusRequestTransformerFilter extends AbstractTransformerFilter {

  private final RequestTransformer globalTransfomer;


  EventbusRequestTransformerFilter(JsonObject config) {
    JsonObject jsonObject = config.getJsonObject("request.transformer", new JsonObject());
    if (jsonObject.isEmpty()) {
      globalTransfomer = null;
    } else {
      globalTransfomer = RequestTransformer.create("global");
      RequestTransfomerConverter.fromJson(jsonObject, globalTransfomer);
    }
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    if (apiContext.apiDefinition() == null) {
      return false;
    }
    if (apiContext.requests().size() > 0
        && apiContext.requests().stream()
                .anyMatch(e -> e instanceof EventbusRpcRequest)) {
      return globalTransfomer != null
             || apiContext.apiDefinition()
                        .plugin(RequestTransformerPlugin.class.getSimpleName()) != null;
    }
    return false;
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    for (int i = 0; i < apiContext.requests().size(); i++) {
      RpcRequest request = apiContext.requests().get(i);
      if (request instanceof EventbusRpcRequest) {
        if (globalTransfomer != null) {
          doTransformer((EventbusRpcRequest) request, globalTransfomer);
        }
        transformer(apiContext, (EventbusRpcRequest) request);
      }
    }
    completeFuture.complete(apiContext);
  }

  private void doTransformer(EventbusRpcRequest request, RequestTransformer transformer) {
    Multimap<String, String> headers = tranformerHeaders(request.headers(), transformer);
    request.clearHeaders().addHeaders(headers);
    if (request.message() != null) {
      JsonObject body = tranformerBody(request.message(), transformer);
      request.replaceMessage(body);
    }
  }

  private void transformer(ApiContext apiContext, EventbusRpcRequest request) {
    String name = request.name();
    RequestTransformerPlugin plugin =
            (RequestTransformerPlugin) apiContext.apiDefinition()
                    .plugin(RequestTransformerPlugin.class.getSimpleName());
    RequestTransformer transformer = plugin.transformer(name);
    if (transformer != null) {
      doTransformer(request, transformer);
    }
  }

}