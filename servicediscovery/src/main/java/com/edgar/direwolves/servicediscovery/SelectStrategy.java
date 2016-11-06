package com.edgar.direwolves.servicediscovery;

import io.vertx.servicediscovery.Record;

import java.util.List;

/**
 * Created by Edgar on 2016/8/5.
 *
 * @author Edgar  Date 2016/8/5
 */
public interface SelectStrategy {

  static SelectStrategy random() {
    return new RandomStrategy();
  }

  static SelectStrategy roundRobin() {
    return new RoundRobinStrategy();
  }

  /**
   * 从一组records中取出一个record
   *
   * @return
   */
  Record select(List<Record> records);
}