package com.edgar.direwolves.core.dispatch;

import com.edgar.direwolves.core.definition.ApiDefinition;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class ApiContextImpl implements ApiContext {

  private final String id = UUID.randomUUID().toString();

  private final String path;

  private final HttpMethod method;

  private final Multimap<String, String> headers;

  private final Multimap<String, String> params;

  private final JsonObject body;

  private final Map<String, Object> variables = new HashMap<>();

  private final JsonArray request = new JsonArray();

  private final JsonArray response = new JsonArray();

  private JsonObject principal;

  private ApiDefinition apiDefinition;

  ApiContextImpl(HttpMethod method, String path, Multimap<String, String> headers,
                 Multimap<String, String> params, JsonObject body) {
    this.path = path;
    this.method = method;
    if (headers == null) {
      headers = ArrayListMultimap.create();
    }
    this.headers = headers;
    if (params == null) {
      params = ArrayListMultimap.create();
    }
    this.params = params;
    this.body = body;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public Multimap<String, String> params() {
    return params;
  }

  @Override
  public Multimap<String, String> headers() {
    return headers;
  }

  @Override
  public JsonObject body() {
    return body;
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public HttpMethod method() {
    return method;
  }

  @Override
  public JsonObject principal() {
    return principal;
  }

  @Override
  public void setPrincipal(JsonObject principal) {
    this.principal = principal;
  }

  @Override
  public Map<String, Object> variables() {
    return variables;
  }

  @Override
  public void addVariable(String name, Object value) {
    variables.put(name, value);
  }

  @Override
  public ApiDefinition apiDefinition() {
    return apiDefinition;
  }

  @Override
  public void setApiDefinition(ApiDefinition apiDefinition) {
    this.apiDefinition = apiDefinition;
  }

  @Override
  public JsonArray request() {
    return request;
  }

  @Override
  public void addRequest(JsonObject jsonObject) {
    this.request.add(jsonObject);
  }

  @Override
  public JsonArray response() {
    return response;
  }

  @Override
  public void addResponse(JsonObject jsonObject) {
    this.response.add(jsonObject);
  }

  @Override
  public String toString() {
    MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper("ApiContext")
        .add("method", method)
        .add("path", path)
        .add("params", params)
        .add("headers", headers)
        .add("body", body)
        .add("variables", variables)
        .add("apiDefinition", apiDefinition);
    if (principal != null) {
      helper.add("principal", principal.encode());
    }
    helper.add("request", request.encode());
    helper.add("response", response);

    return helper.toString();
  }

  public ApiContext copy() {
    ApiContext apiContext = null;
    if (body() == null) {
      apiContext = new ApiContextImpl(method(), path(), ArrayListMultimap.create(headers()),
          ArrayListMultimap.create(params()), null);
    } else {
      apiContext = new ApiContextImpl(method(), path(), ArrayListMultimap.create(headers()),
          ArrayListMultimap.create(params()), body().copy());
    }

    final ApiContext finalApiContext = apiContext;
    variables().forEach((key, value) -> finalApiContext.addVariable(key, value));
    for (int i = 0; i < request().size(); i++) {
      finalApiContext.addRequest(request().getJsonObject(i).copy());
    }
    for (int i = 0; i < response().size(); i++) {
      finalApiContext.addResponse(response().getJsonObject(i).copy());
    }
    return finalApiContext;
  }

}