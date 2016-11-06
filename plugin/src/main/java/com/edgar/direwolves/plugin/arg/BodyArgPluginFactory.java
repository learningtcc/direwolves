package com.edgar.direwolves.plugin.arg;

import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.ApiPluginFactory;
import com.edgar.util.validation.Rule;
import com.google.common.base.Preconditions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * body参数控制的工厂类.
 * <p/>
 * Json配置
 * <Pre>
 * "body_arg" : [
 * {
 * "name" : "encryptKey",
 * "rules" : {
 * "required" : true,
 * "regex" : "[0-9A-F]{16}"
 * }
 * },
 * {
 * "name" : "type",
 * "default_value" : 1,
 * "rules" : {
 * "required" : true,
 * "optional" : [1, 2, 3]
 * }
 * },
 * {
 * "name" : "barcode",
 * "rules" : {
 * "required" : true,
 * "regex" : "[0-9a-zA-Z]{16}"
 * }
 * },
 * {
 * "name": "deviceCode",
 * "rules": {
 * "prohibited": true
 * }
 * }
 * ]
 * </Pre>
 * Created by edgar on 16-10-22.
 */
public class BodyArgPluginFactory implements ApiPluginFactory<BodyArgPlugin> {

  @Override
  public String name() {
    return BodyArgPlugin.NAME;
  }

  @Override
  public ApiPlugin create() {
    return new BodyArgPluginImpl();
  }

  @Override
  public BodyArgPlugin decode(JsonObject jsonObject) {
    if (!jsonObject.containsKey("body_arg")) {
      return null;
    }
    JsonArray jsonArray = jsonObject.getJsonArray("body_arg", new JsonArray());
    BodyArgPlugin bodyArgPlugin = new BodyArgPluginImpl();
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject parameterJson = jsonArray.getJsonObject(i);
      String name = parameterJson.getString("name");
      Preconditions.checkNotNull(name, "arg name cannot be null");
      Object defaultValue = parameterJson.getValue("default_value");
      Parameter parameter = Parameter.create(name, defaultValue);
      List<Rule> rules = RulesDecoder.instance().apply(parameterJson.getJsonObject("rules", new
          JsonObject()));
      rules.forEach(rule -> parameter.addRule(rule));
      bodyArgPlugin.add(parameter);
    }
    return bodyArgPlugin;


  }

  @Override
  public JsonObject encode(BodyArgPlugin plugin) {
    return new JsonObject()
        .put("body_arg", createParamterArray(plugin.parameters()));
  }

  private JsonArray createParamterArray(List<Parameter> parameters) {
    JsonArray jsonArray = new JsonArray();
    parameters.forEach(parameter -> {
      JsonObject jsonObject = new JsonObject()
          .put("name", parameter.name())
          .put("default_value", parameter.defaultValue());
      jsonArray.add(jsonObject);
      JsonObject rules = new JsonObject();
      jsonObject.put("rules", rules);
      parameter.rules().forEach(rule -> {
        rules.mergeIn(new JsonObject(rule.toMap()));
      });
    });
    return jsonArray;
  }
}