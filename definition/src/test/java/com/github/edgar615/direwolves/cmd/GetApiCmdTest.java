package com.github.edgar615.direwolves.cmd;

import com.github.edgar615.util.exception.DefaultErrorCode;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2017/1/19.
 *
 * @author Edgar  Date 2017/1/19
 */
@RunWith(VertxUnitRunner.class)
public class GetApiCmdTest extends BaseApiCmdTest {


  @Before
  public void setUp() {
    super.setUp();

    addMockApi();
  }

  @Test
  public void testMissNameShouldThrowValidationException(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    vertx.eventBus().<JsonObject>send("api.get", new JsonObject(), ar -> {
      if (ar.succeeded()) {
        testContext.fail();
      } else {
        testContext.assertTrue(ar.cause() instanceof ReplyException);
        testContext.assertEquals(DefaultErrorCode.INVALID_ARGS.getNumber(),
                                 ReplyException.class.cast(ar.cause()).failureCode());
        check.set(true);
      }
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testGetApiByFullname(TestContext testContext) {
    JsonObject jsonObject = new JsonObject()
            .put("namespace", namespace)
            .put("name", "add_device");
    AtomicBoolean check1 = new AtomicBoolean();

    vertx.eventBus().<JsonObject>send("api.get", jsonObject, ar -> {
      if (ar.succeeded()) {
        check1.set(true);
      } else {
        ar.cause().printStackTrace();
        testContext.fail();

      }
    });
    Awaitility.await().until(() -> check1.get());

  }

  @Test
  public void testGetApiByWildcard(TestContext testContext) {
    JsonObject jsonObject = new JsonObject()
            .put("namespace", namespace)
            .put("name", "*device");
    AtomicBoolean check1 = new AtomicBoolean();

    vertx.eventBus().<JsonObject>send("api.get", jsonObject, ar -> {
      if (ar.succeeded()) {
        check1.set(true);
      } else {
        ar.cause().printStackTrace();
        testContext.fail();

      }
    });
    Awaitility.await().until(() -> check1.get());
  }

  @Test
  public void testGetApiByUndefinedNameShouldFailed(TestContext testContext) {
    JsonObject jsonObject = new JsonObject()
            .put("namespace", namespace)
            .put("name", UUID.randomUUID().toString());
    AtomicBoolean check1 = new AtomicBoolean();

    vertx.eventBus().<JsonObject>send("api.get", jsonObject, ar -> {
      if (ar.succeeded()) {
        testContext.fail();
      } else {
        ar.cause().printStackTrace();
        testContext.assertTrue(ar.cause() instanceof ReplyException);
        testContext.assertEquals(DefaultErrorCode.RESOURCE_NOT_FOUND.getNumber(),
                                 ReplyException.class.cast(ar.cause()).failureCode());
        check1.set(true);
      }
    });
    Awaitility.await().until(() -> check1.get());
  }

}
