package com.github.edgar615.direwolves.http.loadbalance;

import io.vertx.servicediscovery.Record;

import java.util.function.Function;

/**
 * 服务节点的过滤.
 *
 * @author Edgar  Date 2017/7/28
 */
public interface ServiceFilter extends Function<Record, Boolean> {

}
