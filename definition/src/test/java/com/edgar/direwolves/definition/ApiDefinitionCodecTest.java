package com.edgar.direwolves.definition;

import static org.awaitility.Awaitility.await;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Edgar on 2016/9/13.
 *
 * @author Edgar  Date 2016/9/13
 */
@RunWith(VertxUnitRunner.class)
public class ApiDefinitionCodecTest {

  Vertx vertx;


  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();
  }

  @After
  public void clear(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testAddSuccess(TestContext context) {
    JsonObject addDeviceJson = JsonUtils.getJsonFromFile("src/test/resources/device_add.json");
    ApiDefinition apiDefinition = ApiDefinition.fromJson(addDeviceJson);
    ApiDefinition apiDefinition1 = new ApiDefinitionCodec().transform(apiDefinition);
    Assert.assertNotSame(apiDefinition, apiDefinition1);

  }

}