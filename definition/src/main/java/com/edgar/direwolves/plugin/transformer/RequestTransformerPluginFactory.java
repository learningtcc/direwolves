package com.edgar.direwolves.plugin.transformer;

import com.edgar.direwolves.plugin.ApiPlugin;
import com.edgar.direwolves.plugin.ApiPluginFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.stream.Collectors;

/**
 * Created by edgar on 16-10-23.
 */
public class RequestTransformerPluginFactory implements ApiPluginFactory<RequestTransformerPlugin> {
  @Override
  public String name() {
    return "REQUEST_TRANSFORMER";
  }

  @Override
  public ApiPlugin create() {
    return new RequestTransformerPluginImpl();
  }

  @Override
  public RequestTransformerPlugin decode(JsonObject jsonObject) {
    RequestTransformerPlugin plugin = new RequestTransformerPluginImpl();
    JsonArray jsonArray = jsonObject.getJsonArray("request_transformer", new JsonArray());
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject request = jsonArray.getJsonObject(i);
      String name = request.getString("name");
      RequestTransformer transformer = RequestTransformer.create(name);
      removeBody(request, transformer);
      removeHeader(request, transformer);
      removeParam(request, transformer);

      replaceBody(request, transformer);
      replaceHeader(request, transformer);
      replaceParam(request, transformer);

      addBody(request, transformer);
      addHeader(request, transformer);
      addParam(request, transformer);

      plugin.addTransformer(transformer);
    }

    return plugin;
  }

  @Override
  public JsonObject encode(RequestTransformerPlugin plugin) {
    JsonArray jsonArray = new JsonArray();
    plugin.transformers().stream()
        .map(t -> toJson(t)
        ).forEach(j -> jsonArray.add(j));
    return new JsonObject().put("request_transformer", jsonArray);
  }

  private JsonObject toJson(RequestTransformer transformer) {
    return new JsonObject()
        .put("name", transformer.name())
        .put("request.header.remove", transformer.headerRemoved())
        .put("request.query.remove", transformer.paramRemoved())
        .put("request.body.remove", transformer.bodyRemoved())
        .put("request.header.replace", transformer.headerReplaced()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("request.query.replace", transformer.paramReplaced()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("request.body.replace", transformer.bodyReplaced()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("request.header.add", transformer.headerAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("request.query.add", transformer.paramAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("request.body.add", transformer.bodyAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()));
  }

  private void removeHeader(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("request.header.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeHeader(removes.getString(j));
    }
  }

  private void removeParam(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("request.query.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeParam(removes.getString(j));
    }
  }

  private void removeBody(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("request.body.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeBody(removes.getString(j));
    }
  }

  private void replaceHeader(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray replaces = endpoint.getJsonArray("request.header.replace", new JsonArray());
    for (int j = 0; j < replaces.size(); j++) {
      String value = replaces.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer
          .replaceHeader(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void replaceParam(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray replaces = endpoint.getJsonArray("request.query.replace", new JsonArray());
    for (int j = 0; j < replaces.size(); j++) {
      String value = replaces.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer
          .replaceParam(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void replaceBody(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray replaces = endpoint.getJsonArray("request.body.replace", new JsonArray());
    for (int j = 0; j < replaces.size(); j++) {
      String value = replaces.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer
          .replaceBody(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void addHeader(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("request.header.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addHeader(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void addParam(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("request.query.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addParam(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void addBody(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("request.body.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addBody(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }
}
