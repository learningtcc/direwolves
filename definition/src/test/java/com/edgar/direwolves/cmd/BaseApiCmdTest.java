package com.edgar.direwolves.cmd;

import com.edgar.direwolves.core.cmd.CmdRegister;
import com.edgar.direwolves.core.definition.ApiDiscovery;
import com.edgar.util.vertx.eventbus.Event;
import com.edgar.util.vertx.eventbus.EventCodec;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.awaitility.Awaitility;
import org.junit.Before;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2017/7/5.
 *
 * @author Edgar  Date 2017/7/5
 */
public class BaseApiCmdTest {
  protected Vertx vertx;

  protected ApiDiscovery discovery;

  protected String namespace;
  //  ApiCmd cmd;
  @Before
  public void setUp() {
    namespace = UUID.randomUUID().toString();
    vertx = Vertx.vertx();
    vertx.eventBus().registerDefaultCodec(Event.class, new EventCodec());
    discovery = ApiDiscovery.create(vertx, namespace);
    Future<Void> importCmdFuture = Future.future();
    new CmdRegister().initialize(vertx, new JsonObject(), importCmdFuture);
//    cmd = new AddApiCmdFactory().create(vertx, new JsonObject());
  }

  protected void addMockApi() {
    AddApiCmd addApiCmd = new AddApiCmd(vertx);
    JsonObject jsonObject = new JsonObject()
            .put("name", "add_device")
            .put("method", "POST")
            .put("path", "/devices");
    JsonArray endpoints = new JsonArray()
            .add(new JsonObject().put("type", "http")
                         .put("name", "add_device")
                         .put("service", "device")
                         .put("method", "POST")
                         .put("path", "/devices"));
    jsonObject.put("endpoints", endpoints);

    AtomicBoolean check1 = new AtomicBoolean();
    addApiCmd.handle(new JsonObject().put("namespace", namespace).put("data", jsonObject.encode()))
            .setHandler(ar -> {
              if (ar.succeeded()) {
                check1.set(true);
              } else {
                ar.cause().printStackTrace();
              }
            });
    Awaitility.await().until(() -> check1.get());

    jsonObject = new JsonObject()
            .put("name", "get_device")
            .put("method", "GET")
            .put("path", "/devices");
    endpoints = new JsonArray()
            .add(new JsonObject().put("type", "http")
                         .put("name", "get_device")
                         .put("service", "device")
                         .put("method", "GET")
                         .put("path", "/devices"));
    jsonObject.put("endpoints", endpoints);

    AtomicBoolean check2 = new AtomicBoolean();
    addApiCmd.handle(new JsonObject().put("namespace", namespace).put("data", jsonObject.encode()))
            .setHandler(ar -> {
              if (ar.succeeded()) {
                check2.set(true);
              } else {
                ar.cause().printStackTrace();
              }
            });
    Awaitility.await().until(() -> check2.get());
  }
}
