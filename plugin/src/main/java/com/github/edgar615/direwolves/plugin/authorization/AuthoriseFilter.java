package com.github.edgar615.direwolves.plugin.authorization;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import com.github.edgar615.direwolves.core.dispatch.ApiContext;
import com.github.edgar615.direwolves.core.dispatch.Filter;
import com.github.edgar615.util.log.Log;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 权限校验的filter.
 *
 * 如果接口包括AppKeyPlugin插件，那么在AppKeyFilter调用之后会在上下文中存入<b>app.permissions</b>变量
 * 如果接口包括Authentication插件，那么在AuthenticationFilter调用之后会在用户属性中存入<b>permissions</b>变量
 * 如果调用方或者用户没有对应的权限，直接返回1004的错误.
 * <p>
 *
 *   该filter的order=1100
 */
public class AuthoriseFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthoriseFilter.class);
  AuthoriseFilter(Vertx vertx, JsonObject config) {
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public int order() {
    return 11000;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    return apiContext.apiDefinition().plugin(AuthorisePlugin.class.getSimpleName()) != null;
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    AuthorisePlugin plugin = (AuthorisePlugin) apiContext.apiDefinition()
            .plugin(AuthorisePlugin.class.getSimpleName());
    String appScope = plugin.scope();
    boolean appMatch = true;
    if (apiContext.variables().containsKey("app.permissions")) {
      Set<String> permissions = Sets.newHashSet(Splitter.on(",").omitEmptyStrings().trimResults()
                                                        .split((String) apiContext.variables()
                                                                .get("app.permissions")));
      appMatch = permissions.contains("all") || permissions.contains(appScope);
    }
    boolean userMatch = true;
    if (apiContext.principal() != null) {
      Set<String> permissions = Sets.newHashSet(Splitter.on(",").omitEmptyStrings().trimResults()
                                                        .split(apiContext.principal()
                                                                       .getString("permissions",
                                                                                  "all")));
      userMatch = permissions.contains("all") || permissions.contains(appScope);
    }

    if (userMatch && appMatch) {
      completeFuture.complete(apiContext);
    } else {
      Log.create(LOGGER)
              .setTraceId(apiContext.id())
              .setEvent("authorise.failed")
              .addData("userMatch", userMatch)
              .addData("appMatch", appMatch)
              .warn();
      SystemException ex = SystemException.create(DefaultErrorCode.PERMISSION_DENIED);
      if (!userMatch) {
        ex.set("details", "User does not have permission");
      }
      if (!appMatch) {
        ex.set("details", "AppKey does not have permission");
      }
      completeFuture.fail(ex);
    }


  }

}