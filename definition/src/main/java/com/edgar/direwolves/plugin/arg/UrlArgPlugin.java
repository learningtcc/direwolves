package com.edgar.direwolves.plugin.arg;

import com.edgar.direwolves.plugin.ApiPlugin;

/**
 * Created by edgar on 16-10-22.
 */
public interface UrlArgPlugin extends ApiPlugin, ArgPlugin {
  default String name() {
    return "URL_ARG";
  }

}
